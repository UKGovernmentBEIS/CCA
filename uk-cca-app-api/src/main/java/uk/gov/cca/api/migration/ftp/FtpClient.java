package uk.gov.cca.api.migration.ftp;

import java.util.List;

interface FtpClient extends AutoCloseable {
    
    /**
     * Fetch file
     * @param file the file path
     * @return bytes of the file
     * @throws Exception 
     */
    byte[] fetchFile(String file) throws FtpException;
    
    /**
     * Fetch file in batch mode (leaves the session and channel open)
     * @param file the file path
     * @return bytes of the file
     * @throws Exception
     */
    byte[] fetchFileBatch(String file) throws FtpException;
    
    void healthCheck();
    
    boolean existsByPrefix(String sftpDirectory, String prefix) throws FtpException;
    
    List<String> findFilesByPrefix(String sftpDirectory, String prefix) throws FtpException;
    
    List<String> listFiles(String sftpDirectory) throws FtpException;
}
