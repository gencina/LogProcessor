package oracle.tuxedo.logprocessor;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import oracle.tuxedo.logprocessor.config.Config;
import oracle.tuxedo.logprocessor.config.ConfigurationManager;
import oracle.tuxedo.logprocessor.config.ServiceConfig;
import oracle.tuxedo.logprocessor.dao.FileProcessRecord;
import oracle.tuxedo.logprocessor.dao.LogEntry;
import oracle.tuxedo.logprocessor.exceptions.TuxedoLogProcessException;
import oracle.tuxedo.logprocessor.process.EntryProcessor;
import oracle.tuxedo.logprocessor.process.EntryProcessorFactory;
import oracle.tuxedo.logprocessor.util.Utility;

import tp.comp.util.logger.mensaje.LogMDWMensaje;


public class LogProcessManager {
    
    private ConfigurationManager configurationManager;
    private Connection dbConnection;
    protected BlockingQueue<LogMDWMensaje> msgQueue;
    protected boolean endProcess = false;
    
    private final String insertSql = "INSERT INTO LOG_TUXEDO_PROCESSOR " + 
                                     "(LOG_ID, FILE_PATH, FINISHED, FILE_SIZE, LINES_READ, POSITION, SERVICE_NAME, UPDATED_DATE) " + 
                                     "VALUES (LOG_ID_TUXEDO.NEXTVAL,?,?,?,?,?,?,?)";
    
    private final String updateSql = "UPDATE LOG_TUXEDO_PROCESSOR " + 
                                     "SET FINISHED = ?, POSITION = ?, UPDATED_DATE = ?, LINES_READ = ?, FILE_SIZE = ?" + 
                                     "WHERE LOG_ID = ?";
    
    private final String selectSql = "SELECT LOG_ID, FINISHED, FILE_SIZE, FILE_PATH, POSITION, SERVICE_NAME, LINES_READ " + 
                                     "FROM LOG_TUXEDO_PROCESSOR " + 
                                     "WHERE FINISHED = 'false'" +
                                     "OR TO_CHAR(UPDATED_DATE, 'ddMMyyyy') = TO_CHAR(SYSDATE, 'ddMMyyyy') " + 
                                     "ORDER BY LOG_ID";
        
    public LogProcessManager( ConfigurationManager configurationManager ) {
            
        this.configurationManager = configurationManager;
        this.msgQueue = new LinkedBlockingQueue<LogMDWMensaje>();
        setUp();
    }
    
    public void processFile( FileProcessRecord fileProcessRecord, Date date ) throws TuxedoLogProcessException{
        
        ServiceConfig serviceConfig = fileProcessRecord.getServiceConfig();
        long lineCount = fileProcessRecord.getLinesRead();
        long startPosition = fileProcessRecord.getCursorPosition();
        long fileSize = fileProcessRecord.getFileSize();
        String filePath = fileProcessRecord.getFilePath();
        
        RandomAccessFile raf = null;
        
        try {
            EntryProcessorFactory epf = new EntryProcessorFactory( serviceConfig );
            Set<LogEntry> logEntries = epf.getLogEntries();
            
            raf = new RandomAccessFile(filePath, "r");
            raf.seek( startPosition );
            String line = null;
            
            while ( raf.getFilePointer() < startPosition+Config._FILE_OFFSET_SIZE && raf.getFilePointer() < fileSize ) {
                line = raf.readLine();
                lineCount++;
                
                for( LogEntry logEntry : logEntries ){
                    if( line.contains( logEntry.getText() ) ){
                        
                        EntryProcessor entryProcessor = epf.getEntryProcessor(logEntry);
                        LogMDWMensaje message = entryProcessor.processLine(line, logEntry.getText(), date);
                        logEntry.getPropertyFile();
                        if( message != null ){
                            msgQueue.put(message);                            
                        }
                    }
                }
            }
            
            fileProcessRecord.setLinesRead( lineCount );
            fileProcessRecord.setCursorPosition( raf.getFilePointer() );
            
            if (raf.getFilePointer() == fileProcessRecord.getFileSize())
                fileProcessRecord.setFinished(true);
            
        }catch( Exception e) {
            e.printStackTrace();
            throw new TuxedoLogProcessException( fileProcessRecord );
        }finally{
            try{ 
                if(raf != null)raf.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void getFilesToProcessForDate(Set<FileProcessRecord> fileProcessRecords, Date date) {
        
        if( fileProcessRecords == null || date == null ){
            return;
        }
        
        List<ServiceConfig> services = configurationManager.getServices();
        
        for( ServiceConfig serviceConfig : services ){
            
            File logFolder = new File( serviceConfig.getLogFolder() );
            
            if( logFolder.isDirectory() ){
            
                final String fileName = Utility.parseFileMask( serviceConfig.getFileMask(), date );
                
                FileFilter filter = new FileFilter(){
                    @Override
                    public boolean accept(File file) {
                        if( file.getName().equals( fileName ) ){
                            return true;
                        }
                        return false;
                    }
                };
                
                File[] files = logFolder.listFiles( filter );
                
                for( File file : files ){
                    
                    if( !file.isDirectory() ){
                        
                        FileProcessRecord fileProcessRecord = new FileProcessRecord(serviceConfig);
                        fileProcessRecord.setFilePath( file.getAbsolutePath() );
                        fileProcessRecord.setFileSize( file.length() );
                        fileProcessRecord.setType("NEW");
                        
                        if( !fileProcessRecords.contains( fileProcessRecord ))
                        {                            
                            fileProcessRecords.add(fileProcessRecord);
                        } else {
                            for( FileProcessRecord fp : fileProcessRecords){
                                if (fp.equals(fileProcessRecord))
                                    if (fp.getFileSize() < fileProcessRecord.getFileSize()){
                                        fp.setFinished(false);
                                        fp.setFileSize(fileProcessRecord.getFileSize());
                                        break;
                                    }
                            }
                        }
                    }
                }
            }
            
        }
        
    }

    public void getFilesToProcessUnFinished(Set<FileProcessRecord> fileProcessRecord) {
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        // Recuperar los archivo procesados sin finalizar desde la base de datos
        try {
            stmt = dbConnection.prepareStatement(selectSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery();
            while (rs.next()){
                String serviceName = rs.getString(6);
                
                List<ServiceConfig> services = configurationManager.getServices();
                int i = 0;
                while (i <services.size()){
                    if (services.get(i).getName().equals(serviceName)){
                        break;
                    }
                }
                    FileProcessRecord fp = new FileProcessRecord(services.get(i));
                    fp.setId(rs.getLong(1));
                    fp.setFinished(Boolean.valueOf(rs.getString(2)));
                    fp.setFileSize(rs.getLong(3));
                    fp.setFilePath(rs.getString(4));
                    fp.setCursorPosition(rs.getLong(5));
                    fp.setLinesRead(rs.getLong(7));
                    fp.setType("OLD");
                    fileProcessRecord.add(fp);
            }
            stmt.clearParameters(); 
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void saveFileResults(FileProcessRecord fileProcessRecord) {
        
        // Guardar en base de datos los resultados del archivo procesado
        PreparedStatement stmt = null;

        try {
            if(fileProcessRecord.getType().equals("NEW")){
                stmt = dbConnection.prepareStatement(insertSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                
                stmt.setString(1, fileProcessRecord.getFilePath());
                stmt.setString(2, fileProcessRecord.getFinished());
                stmt.setLong(3, fileProcessRecord.getFileSize());
                stmt.setLong(4,fileProcessRecord.getLinesRead());
                stmt.setLong(5, fileProcessRecord.getCursorPosition());
                stmt.setString(6, fileProcessRecord.getServiceConfig().getName());
                stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            }
            else {
                stmt = dbConnection.prepareStatement(updateSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                stmt.setString(1, fileProcessRecord.getFinished());
                stmt.setLong(2, fileProcessRecord.getCursorPosition());
                stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                stmt.setLong(4, fileProcessRecord.getLinesRead());
                stmt.setLong(5, fileProcessRecord.getFileSize());
                stmt.setLong(6, fileProcessRecord.getId());
            }
        
            stmt.executeUpdate();
            stmt.clearParameters(); 
            
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }
    
    void setUp(){
        
        Context initialContext = null;
        DataSource datasource = null;
        String datasourceContext = null;
        Hashtable ht = new Hashtable();
        Properties props = new Properties();
        
        try {
            props.load(new FileInputStream(Config._SERVER_CONFIG_FILE ) );

            datasourceContext = props.getProperty("java.naming.lookup.datasource");
        
            ht.put(Context.INITIAL_CONTEXT_FACTORY, props.getProperty("java.naming.factory.initial"));
            ht.put(Context.PROVIDER_URL, props.getProperty("java.naming.provider.url"));
            initialContext = new InitialContext(ht);
            
                       
          if ( initialContext != null){
              datasource = (DataSource)initialContext.lookup(datasourceContext);
          }
          
          if (datasource != null) {
            dbConnection = datasource.getConnection();
          }
          
          initialContext.close();
        
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch ( NamingException ex ) {
            ex.printStackTrace();
        } catch(SQLException ex){
            ex.printStackTrace();
        } 
    }

    public void close() {  
        try {  
            dbConnection.close();
  
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setEndProcess(boolean endProcess) {
        this.endProcess = endProcess;
    }

    public boolean isEndProcess() {
        return endProcess;
    }
}
