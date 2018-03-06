package oracle.tuxedo.logprocessor.config;

public class ServiceConfig {
    
    private String name;
    private String logFolder;
    private String fileMask;
    private String configFile;
    
    public ServiceConfig() {
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogFolder() {
        return logFolder;
    }

    public void setLogFolder(String logFolder) {
        this.logFolder = logFolder;
    }

    public String getFileMask() {
        return fileMask;
    }

    public void setFileMask(String fileMask) {
        this.fileMask = fileMask;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}
