package oracle.tuxedo.logprocessor.dao;

public class MessageStart extends Message {
    private String consumerCode;
    private String operation;
    private String line;
    private String systemCode;
    private String region;

    public MessageStart(){
        
    }

    public void setConsumerCode(String consumerCode) {
        this.consumerCode = consumerCode;
    }

    public String getConsumerCode() {
        return consumerCode;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }


}
