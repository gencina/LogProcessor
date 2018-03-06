package oracle.tuxedo.logprocessor.config;

public interface ServiceConstants {
    
    // Operacion dataobject root elements
    public static final String service = "service";
    public static final char[] serviceCriteria = "service ".toCharArray();
    
    
    // Operacion fields
    public static final String serviceCode = "code";
    public static final String serviceName = "name";
    
    // Field criterias
    public static final char[] serviceCodeCriteria = "code=".toCharArray();
    public static final char[] serviceNameCriteria = "name=".toCharArray();
    
    
}
