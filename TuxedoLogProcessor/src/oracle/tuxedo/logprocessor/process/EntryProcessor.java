package oracle.tuxedo.logprocessor.process;

import java.util.Date;

import tp.comp.util.logger.mensaje.LogMDWMensaje;


public interface EntryProcessor {

    public LogMDWMensaje processLine( String line, String msgBegin, Date date );
    
    public void setConfigFile(String configFile);
    
}
