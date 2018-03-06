package oracle.tuxedo.logprocessor.util;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Formatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utility {
    
    public static String composeXMLMessage( String rootElement, Map<String,String> values ) {

        StringBuilder sb = new StringBuilder();
        sb.append("<"+rootElement+">");
        
        for(Map.Entry<String,String> entry : values.entrySet() ) {
            sb.append( "<"+entry.getKey()+">" );
            sb.append( entry.getValue() );
            sb.append( "</"+entry.getKey()+">" );
        }
        sb.append("</"+rootElement+">");
        
        return sb.toString();
        
    }
    
    public static boolean validateRestrictions(String value, String[] restrictions){
        for (int i=0;i<restrictions.length;i++){
            if(value.trim().equals(restrictions[i]))
                return false;
        }
        return true;    
    }
    
    public static String parseFileMask( String fileMask, Date date ){
        
        String fileName = null;
        
        try{
            Pattern p = Pattern.compile( "(.*)<([-_\\w]+)>(.*)" );
            
            Matcher m = p.matcher(fileMask);
            if( m.find() ){
                SimpleDateFormat sdf = new SimpleDateFormat(m.group(2));
                
                fileName = m.group(1)+sdf.format( date )+m.group(3);
            }
        }catch( Throwable t ){
            t.printStackTrace();
        }
        
        return fileName;
    }
    
    public static String generateMessageId (int processId, Date date ){
        
        SequenceId seq = SequenceId.getInstance();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");
        String dt = sdf.format(date);
        Formatter fmt = new Formatter();
        fmt.format("%06d",processId);
        fmt.format("%03d",seq.getSeqId());
        
        seq.saveMsgIncomplete(processId, sdf.format(date)+fmt.toString());
        
        String msgId = sdf.format(date) + fmt.toString();
        
        return msgId;
    }
}
