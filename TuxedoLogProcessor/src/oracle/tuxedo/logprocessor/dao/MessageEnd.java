package oracle.tuxedo.logprocessor.dao;

public class MessageEnd extends Message{
    private String status;
    private String result;
    
    public MessageEnd(){
        
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
