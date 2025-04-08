package uk.gov.cca.api.migration.files;

import java.io.ByteArrayInputStream;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.FileConstants;
import uk.gov.netz.api.files.common.FileTypesProperties;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.service.FileScanService;

@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@RequiredArgsConstructor
public class FileValidatorMigrationService {
    
    private final FileScanService fileScanService;
    private final FileTypesProperties fileTypesProperties;
    
    public void validateFileDTO(FileDTO fileDTO) throws Exception {
        
        final long fileSize = fileDTO.getFileSize();
        
        if (fileSize <= FileConstants.MIN_FILE_SIZE) {
            throw new Exception(ErrorCode.MIN_FILE_SIZE_ERROR.getMessage());
        }
        
        if (fileSize >= FileConstants.MAX_FILE_SIZE) {
            throw new Exception(ErrorCode.MAX_FILE_SIZE_ERROR.getMessage());
        }
        
        if (fileTypesProperties.getAllowedMimeTypes().stream()
                .noneMatch(mimeType -> mimeType.equals(fileDTO.getFileType()))) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE, fileDTO.getFileType());
        }

        try(ByteArrayInputStream is = new ByteArrayInputStream(fileDTO.getFileContent())) {
            fileScanService.scan(is);
        } catch (Exception e) {
            throw new Exception(ErrorCode.INFECTED_STREAM.getMessage());
        }
    }
}
