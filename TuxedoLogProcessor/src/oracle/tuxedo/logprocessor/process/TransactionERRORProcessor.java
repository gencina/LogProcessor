package oracle.tuxedo.logprocessor.process;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.Properties;

import oracle.tuxedo.logprocessor.config.Config;
import oracle.tuxedo.logprocessor.dao.MessageEnd;
import oracle.tuxedo.logprocessor.util.SequenceId;
import oracle.tuxedo.logprocessor.util.Utility;

import tp.comp.util.logger.mensaje.LogMDWMensaje;


public class TransactionERRORProcessor implements EntryProcessor {
    
    private Properties props = new Properties();

    public TransactionERRORProcessor() {
    
    }

    public LogMDWMensaje processLine( String line, String msgBegin, Date date ) {
        
        int processStart = line.indexOf('[', 2) +1;
        int processEnd = line.indexOf(']', processStart);
        
        int processId = Integer.valueOf(line.substring(processStart, processEnd));
        
        String msg = line.substring(line.indexOf(msgBegin));
        
        MessageEnd msgEnd = new MessageEnd(); 
        
        msgEnd.setServiceCode(props.getProperty("mensaje.serviceCode"));
        msgEnd.setDescription(props.getProperty("mensaje.description"));
        msgEnd.setMessageID(SequenceId.getInstance().getMsgIncomplete(processId));
              
        int i = 1;
        Map<String,String> elements = new HashMap<String,String>();
        
        while (props.containsKey("mensaje.receivedMessage.element"+i+".name")){
            
            String value = "";
            String [] index = props.getProperty("mensaje.receivedMessage.element"+i+".value").split(Config._VALUE_LIST_SEPARATOR);
            String restriction_values = props.getProperty("mensaje.receivedMessage.element"+i+".restriction_values","");
            
            value = msg.substring(Integer.valueOf(index[0]),Integer.valueOf(index[0])+Integer.valueOf(index[1])).replaceAll("]"," ").trim();
            
            
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
        
        msgEnd.setXmlMessages(Utility.composeXMLMessage(props.getProperty("mensaje.receivedMessage.root_element_name"), elements ));
        
        
        LogMDWMensaje lm = new LogMDWMensaje(null, msgEnd.getServiceCode(), msgEnd.getMessageID() , null, null,
                                                        msgEnd.getDescription(), msgEnd.getXmlMessages(),null,
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
