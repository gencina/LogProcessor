package oracle.tuxedo.logprocessor.exceptions;

import oracle.tuxedo.logprocessor.dao.FileProcessRecord;

public class TuxedoLogProcessException extends Exception {
    public TuxedoLogProcessException(FileProcessRecord fileProcessRecord) {
        
        super( "Exception processing file: '"+fileProcessRecord.getFilePath()+"' line: '"+fileProcessRecord.getLinesRead()+"'"+"' position: '"+fileProcessRecord.getCursorPosition()+"'" );
        
    }
}
