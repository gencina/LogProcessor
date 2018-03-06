package oracle.tuxedo.logprocessor.dao;

abstract public class Message {
    private String messageID;
    private String type;
    private String serviceCode;
    private String description;
    
    private String xmlMessages;
    
    public Message(){
        
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


    public void setXmlMessages(String xmlMessages) {
        this.xmlMessages = xmlMessages;
    }

    public String getXmlMessages() {
        return xmlMessages;
    }
    
    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
