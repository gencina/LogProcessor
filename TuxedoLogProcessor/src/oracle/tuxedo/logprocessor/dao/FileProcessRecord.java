package oracle.tuxedo.logprocessor.dao;

import oracle.tuxedo.logprocessor.config.ServiceConfig;

public class FileProcessRecord {
    
    private ServiceConfig serviceConfig;
    private long linesRead = 0;
    private long cursorPosition = 0;
    private long fileSize = 0;
    private String filePath;
    private boolean finished = false;
    private String type = null;
    private long id = 0;
    private long lastUpdateTimestamp = 0;
    
    public FileProcessRecord( ServiceConfig serviceConfig ){

        this.serviceConfig = serviceConfig;
        
    }
    
    public FileProcessRecord(){
              
    }
    
    
    public long getLinesRead() {
        return linesRead;
    }

    public void setLinesRead(long linesRead) {
        this.linesRead = linesRead;
    }

    public long getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(long currentPosition) {
        this.cursorPosition = currentPosition;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    
    public String getFinished() {
        if(this.finished)
            return "true";
        else 
            return "false";
    }

    public FileProcessRecord copy() {
        FileProcessRecord newResult = new FileProcessRecord( serviceConfig );
        newResult.setCursorPosition(cursorPosition);
        newResult.setFilePath(filePath);
        newResult.setFileSize(fileSize);
        newResult.setFinished(finished);
        newResult.setLinesRead(linesRead);
        newResult.setLastUpdateTimestamp(lastUpdateTimestamp);
        
        return newResult;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof FileProcessRecord)) {
            return false;
        }
        final FileProcessRecord other = (FileProcessRecord)object;
        if (!(filePath == null ? other.filePath == null : filePath.equals(other.filePath))) {
            return false;
        }
        return true;
    }

    @Override
   public int hashCode() {
        final int PRIME = 37;
        int result = 1;
        result = PRIME * result + ((filePath == null) ? 0 : filePath.hashCode());
        return result;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public ServiceConfig getServiceConfig() {
        return serviceConfig;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
