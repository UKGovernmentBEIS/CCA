package uk.gov.cca.api.migration.ftp;

public class FtpFileGenericException extends RuntimeException {
    
    public FtpFileGenericException(String message) {
        super(message);
    }
    
    public FtpFileGenericException(String message, Throwable cause) {
        super(message, cause);
    }
}

