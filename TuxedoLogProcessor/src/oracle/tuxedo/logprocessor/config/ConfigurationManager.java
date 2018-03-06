package oracle.tuxedo.logprocessor.config;

import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;


public class ConfigurationManager {
    
    private static ConfigurationManager instance;
    protected List<ServiceConfig> services = new ArrayList<ServiceConfig>();
    
    private ConfigurationManager() {
        
    }
        
    public static synchronized ConfigurationManager getInstance(){
        if( instance == null ){
            instance = new ConfigurationManager();
        }
        
        return instance;
    }
    
    public List<ServiceConfig> getServices() {
        
        if (this.services.isEmpty()){
            List<ServiceConfig> services = new ArrayList<ServiceConfig>();
           
             try{
            
              Properties props = new Properties();
                props.load( new FileInputStream( Config._SERVICES_CONFIG_FILE ) );
            
                for( int i = 1; i <= Config._SERVICES_SUPPORTED; i++ ){
                
                    // Root Element
                    String serviceName = props.getProperty("service"+i+".name","");
                    if( "".equals(serviceName) ){
                        break;
                    }
                    ServiceConfig serviceConfig = new ServiceConfig();
                    serviceConfig.setName(serviceName);
                    serviceConfig.setLogFolder(props.getProperty("service"+i+".log_folder",""));
                    serviceConfig.setFileMask(props.getProperty("service"+i+".file_mask",""));
                    serviceConfig.setConfigFile(props.getProperty("service"+i+".config_file",""));
                
                    services.add(serviceConfig);
                }
            
            }catch( Throwable t ){
            t.printStackTrace();
            }
        
        return services;
        }  else {
        return this.services;
        }
    }
}
