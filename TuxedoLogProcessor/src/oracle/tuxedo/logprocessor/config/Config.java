package oracle.tuxedo.logprocessor.config;

import java.io.File;

public interface Config {
    
    public final static String _CONFIG_FOLDER = System.getProperty("oracle.tuxedo.logprocessor.configfolder","")+File.separator;
    public final static String _SERVER_CONFIG_FILE = _CONFIG_FOLDER+"tuxedo_server.properties";
    public final static String _SERVICES_CONFIG_FILE = _CONFIG_FOLDER+"tuxedo_services.properties";
    
    public static int _SERVICES_SUPPORTED = 100;
    public static int _LOG_ENTRIES_SUPPORTED = 10;
    public static int _MESSAGE_ELEMENTS_SUPPORTED = 20;
    
    public final static String _PARAM_LIST_SEPARATOR = ":";
    public final static String _VALUE_LIST_SEPARATOR = ",";
    
    public final static long _FILE_OFFSET_SIZE = 20971520; // Este valor puede ser ajustado según recursos del sistema
    
}
