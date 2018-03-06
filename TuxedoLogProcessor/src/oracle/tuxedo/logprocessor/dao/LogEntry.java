package oracle.tuxedo.logprocessor.dao;

public class LogEntry {
    
    private String text;
    private String className;
    private String propertyFile;
    
    public LogEntry() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPropertyFile() {
        return propertyFile;
    }

    public void setPropertyFile(String propertyFile) {
        this.propertyFile = propertyFile;
    }
}
