package oracle.tuxedo.logprocessor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import java.util.concurrent.TimeUnit;

import oracle.tuxedo.logprocessor.config.ConfigurationManager;
import oracle.tuxedo.logprocessor.dao.FileProcessRecord;
import oracle.tuxedo.logprocessor.exceptions.TuxedoLogProcessException;

public class TuxedoLogProcessor {
    
    protected ExecutorService consumerMsg;
    
    public TuxedoLogProcessor() {
        super();
    }
    
    public void doProcess( Date date , int qProcess){
        
        System.out.println("Inicio TuxedoLogProcessor");
        
        try{
            ConfigurationManager configurationManager = ConfigurationManager.getInstance();
            
            LogProcessManager logProcessManager = new LogProcessManager( configurationManager );
        
            Set<FileProcessRecord> filesToProcess = new HashSet<FileProcessRecord>();
            
            // Primero se cargan los archivos ya empezados desde la base de datos
            logProcessManager.getFilesToProcessUnFinished( filesToProcess );
            
            // Luego se buscan archivos nuevos
            logProcessManager.getFilesToProcessForDate( filesToProcess, date );
            
            if(!filesToProcess.isEmpty()){
                
               this.consumerMsg= Executors.newFixedThreadPool(qProcess);
                CountDownLatch latch = new CountDownLatch(qProcess);
                for (int i = 0; i < qProcess; i++) {
                            consumerMsg.submit(new LogMensajeTopicPublisher(logProcessManager, latch));
                }
                
                for( FileProcessRecord fileProcessRecord : filesToProcess ){
                    
                    try{
                        if (!fileProcessRecord.isFinished()){
                            
                           logProcessManager.processFile( fileProcessRecord, date );
                           logProcessManager.saveFileResults( fileProcessRecord );
                        }

                    } catch( TuxedoLogProcessException e ){
                        e.printStackTrace();
                    }
                }
                
                logProcessManager.setEndProcess(true);
                   
                latch.await();
                    
                logProcessManager.msgQueue.clear();
                shutdownAndAwaitTermination();
            
            }
            
            logProcessManager.close();
            
            System.out.println("Fin TuxedoLogProcessor");

        }catch( Throwable t ){
            
            t.printStackTrace();
        }
    }
    
    private void shutdownAndAwaitTermination() {
            
            consumerMsg.shutdown(); 
            try {
                    if (!consumerMsg.awaitTermination(60, TimeUnit.SECONDS)) {
                            consumerMsg.shutdownNow(); 
                            if (!consumerMsg.awaitTermination(60, TimeUnit.SECONDS))
                                    System.err.println("Pool did not terminate");
                    }
            } catch (InterruptedException ie) {
                    consumerMsg.shutdownNow();
                    Thread.currentThread().interrupt();
            }
    }
}
