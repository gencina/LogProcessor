package oracle.tuxedo.logprocessor.process;

import java.io.FileInputStream;

import java.io.IOException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import oracle.tuxedo.logprocessor.util.Utility;
import oracle.tuxedo.logprocessor.config.Config;
import oracle.tuxedo.logprocessor.dao.MessageStart;

import tp.comp.util.logger.mensaje.LogMDWMensaje;


public class TransactionStartProcessor implements EntryProcessor {
    
    private Properties props = new Properties();

    public TransactionStartProcessor() {
    
    }

    public LogMDWMensaje processLine( String line, String msgBegin , Date date ) {
        
        int processStart = line.indexOf('[', 2) +1;
        int processEnd = line.indexOf(']', processStart);
        
        int processId = Integer.valueOf(line.substring(processStart, processEnd));
        
        String msg = line.substring(line.indexOf(msgBegin));
        
        MessageStart msgStart = new MessageStart(); 
        
        String[] indexConsumerCode = props.getProperty("mensaje.consumerCode").split(Config._VALUE_LIST_SEPARATOR);
        msgStart.setConsumerCode(msg.substring(Integer.valueOf(indexConsumerCode[0]), Integer.valueOf(indexConsumerCode[0])+Integer.valueOf(indexConsumerCode[1])));
        
        msgStart.setServiceCode(props.getProperty("mensaje.serviceCode"));
        msgStart.setDescription(props.getProperty("mensaje.description"));
        msgStart.setMessageID(Utility.generateMessageId(processId, date));
              
        int i = 1;
        Map<String,String> elements = new HashMap<String,String>();
        
        while (props.containsKey("mensaje.receivedMessage.element"+i+".name")){
            
            String value = "";
            String [] index = props.getProperty("mensaje.receivedMessage.element"+i+".value").split(",");
            String restriction_values = props.getProperty("mensaje.receivedMessage.element"+i+".restriction_values","");
            
            value = msg.substring(Integer.valueOf(index[0]),Integer.valueOf(index[0])+Integer.valueOf(index[1])).trim();
            
            
            if(!value.isEmpty()){
                if(!restriction_values.isEmpty()){
                    String[] restrictions = restriction_values.split(",");
                    if (Utility.validateRestrictions(value, restrictions))
                        elements.put(props.getProperty("mensaje.receivedMessage.element"+i+".name"), value);                           
                } else 
                    elements.put(props.getProperty("mensaje.receivedMessage.element"+i+".name"), value);   
            }

            i++;
        }
        
        msgStart.setXmlMessages(Utility.composeXMLMessage(props.getProperty("mensaje.receivedMessage.root_element_name"), elements ));
        
        
        LogMDWMensaje lm = new LogMDWMensaje(msgStart.getConsumerCode(), msgStart.getServiceCode(), msgStart.getMessageID(), null, null,
                                                        msgStart.getDescription(), msgStart.getXmlMessages(),null,
                                                        null, null, Calendar.getInstance());
        
        return lm;
    }
    
    
    public void setConfigFile(String configFile){
        try {
            props.load( new FileInputStream( Config._CONFIG_FOLDER + configFile ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
