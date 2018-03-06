package oracle.tuxedo.logprocessor.process;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import oracle.tuxedo.logprocessor.config.Config;
import oracle.tuxedo.logprocessor.config.ServiceConfig;
import oracle.tuxedo.logprocessor.dao.LogEntry;

public class EntryProcessorFactory {
    
    private Map<String,EntryProcessor> entryProcessors = new HashMap<String,EntryProcessor>();
    
    private Properties props = new Properties();
    
    private Set<LogEntry> logEntries;
    
    public EntryProcessorFactory( ServiceConfig serviceConfig ) {
    
        try {
            props.load( new FileInputStream( Config._CONFIG_FOLDER + serviceConfig.getConfigFile() ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public EntryProcessor getEntryProcessor( LogEntry logEntry  ){
        
        EntryProcessor entryProcessor = entryProcessors.get(logEntry.getClassName());
        if( entryProcessor == null ){
            try{
                Class c = Class.forName(logEntry.getClassName());
                entryProcessor = (EntryProcessor)c.newInstance();
                entryProcessor.setConfigFile(logEntry.getPropertyFile());
                
                entryProcessors.put( logEntry.getClassName(), entryProcessor );
                
            }catch( Exception e ){
                throw new RuntimeException("Class can not be instatiated: '"+logEntry.getClassName()+"'");
            }
            
        }
        return entryProcessor;
        
    }
    
    public Set<LogEntry> getLogEntries(){
        if( logEntries == null ){
            logEntries = new HashSet<LogEntry>();
            
            for( int i = 1; i <= Config._LOG_ENTRIES_SUPPORTED; i++ ){
                
                // Entries
                String entryText = props.getProperty("log_entry"+i+".text","");
                if( "".equals(entryText) ){
                    break;
                }
                LogEntry logEntry = new LogEntry();
                logEntry.setText(entryText);
                logEntry.setClassName(props.getProperty("log_entry"+i+".class",""));
                logEntry.setPropertyFile(props.getProperty("log_entry"+i+".properties_file",""));
                
                logEntries.add(logEntry);
            }
            
        }
    
        return logEntries;
    }
    
}
