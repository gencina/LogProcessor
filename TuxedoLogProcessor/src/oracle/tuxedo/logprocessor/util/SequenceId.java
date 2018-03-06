package oracle.tuxedo.logprocessor.util;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SequenceId {
    
    private static SequenceId instance = new SequenceId();
    private static int seqMsgId = 0;
    private static Map<String,Integer> msgIncomplete = new TreeMap<String,Integer>();
    
    private SequenceId(){        
    }
    
    public static SequenceId getInstance(){
        return instance;
    }
    
    public Integer getSeqId(){
        if((seqMsgId +1) / 1000 < 1)
            seqMsgId++;       
        else
            seqMsgId = 1;
        
        return seqMsgId;
    }
    
    public void saveMsgIncomplete(int processId, String messageId ){
        this.msgIncomplete.put(messageId, processId);
    }
        
    public String getMsgIncomplete(int processId){
        Set<String> keys = this.msgIncomplete.keySet();
        for (String key: keys){
            if(this.msgIncomplete.get(key).equals(processId)){
                this.msgIncomplete.remove(key);
                return key;
            }
        }
        return "";
    }
    
   
}