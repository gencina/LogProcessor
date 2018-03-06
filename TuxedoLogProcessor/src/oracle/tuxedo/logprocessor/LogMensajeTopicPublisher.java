package oracle.tuxedo.logprocessor;

import java.io.FileInputStream;

import java.util.Properties;

import java.util.concurrent.CountDownLatch;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import javax.naming.Context;
import javax.naming.InitialContext;

import oracle.tuxedo.logprocessor.config.Config;

import tp.comp.util.logger.mensaje.LogMDWMensaje;

import utils.system;


public class LogMensajeTopicPublisher implements Runnable{
    
    private static int _RETRY_CONNECTION_COUNT = 3;
    
    private TopicConnection connection;
    private TopicSession session;
    private Topic topic;
    private TopicPublisher sender;
    private int retryConnection = 0;
    protected LogProcessManager  lpm;
    private CountDownLatch latch;
    private boolean isEnd;
    private int sleepTime = 1000;
    
    
    public LogMensajeTopicPublisher(LogProcessManager logProcessManager, CountDownLatch latch){
        this.lpm = logProcessManager;  
        this.latch = latch;
        //setUp();
    }
    
    public void run(){
        LogMDWMensaje message;
            while (((message = lpm.msgQueue.poll()) != null) || !lpm.isEndProcess()) {
               if(message != null){
                  System.out.println("MessageId "+ message.getMessageId()); 
                  //send(message);
               } else {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
               }
            }
            //close();

            latch.countDown();
    }
    
    public void setUp() {  
  
        Context ctx;  
  
        try {  
            Properties parm = new Properties();  
            parm.load(new FileInputStream(Config._SERVER_CONFIG_FILE ) );
            
            ctx = new InitialContext(parm);  
  
            TopicConnectionFactory connectionFactory = 
            (TopicConnectionFactory)ctx.lookup( parm.getProperty("TopicConnectionFactoryName") );  
  
            connection = connectionFactory.createTopicConnection();          
            connection.start();          
            session = connection.createTopicSession(false, Session.CLIENT_ACKNOWLEDGE);            
            topic = (Topic)ctx.lookup( parm.getProperty("TopicName") );   
            sender = session.createPublisher(topic);
  
            retryConnection = _RETRY_CONNECTION_COUNT;
            
        } catch (JMSException je) {  
            throw new RuntimeException("Can not start Topic connection",je);  
        } catch (Throwable t) {  
            throw new RuntimeException("Can not start",t);  
        }  
    }  
    
    public void send( LogMDWMensaje mensaje ){  
        
        try {  
            ObjectMessage message = session.createObjectMessage( mensaje );  
            
            sender.send(message);
        } catch (JMSException jmse) {  
            jmse.printStackTrace();  
            
            if( retryConnection > 0 ){
                try{
                    send( mensaje );
                }catch(Exception e){
                    e.printStackTrace();
                }
                retryConnection--;
            }
            
        }  
    }  
    
    public void close() {  
        try {  
            sender.close(); 
            session.close();   
            connection.close();  
  
        } catch (JMSException je) {  
            je.printStackTrace();  
        } 
    }  
    
}
