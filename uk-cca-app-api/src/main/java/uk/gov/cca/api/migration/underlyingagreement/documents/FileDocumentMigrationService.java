package uk.gov.cca.api.migration.underlyingagreement.documents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.files.FileMigrationError;
import uk.gov.cca.api.migration.files.FileValidatorMigrationService;
import uk.gov.cca.api.migration.ftp.FtpFileDTOResult;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.cca.api.migration.ftp.GenericFtpResult;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.documents.domain.FileDocument;
import uk.gov.netz.api.files.documents.repository.FileDocumentRepository;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class FileDocumentMigrationService {
    
    private static final FileDocumentMigrationMapper fileDocumentMapper = Mappers.getMapper(FileDocumentMigrationMapper.class);
   
    private final FtpFileService ftpService;
    private final FtpProperties ftpProperties;

    private final FileDocumentRepository fileDocumentRepository;
    private final FileValidatorMigrationService fileValidatorMigrationService;
    
    public List<String> listUnaDocuments() {
        final String sftpDirectory = ftpProperties.getServerUnderlyingAgreementsDirectory();
        GenericFtpResult<List<String>> files = ftpService.listFiles(sftpDirectory);
        if (files.getErrorReport() != null) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT, files.getErrorReport());
        }
        return files.getData();
    }
    
    @Transactional
    public void saveFileDocumentMetadata(final List<String> files) {
        final int batchSize = 1000;
        List<FileDocument> batch = new ArrayList<>();

        for (String filename : files) {
            batch.add(fileDocumentMapper.toFileDocument(filename));
            if (batch.size() == batchSize) {
                fileDocumentRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (CollectionUtils.isNotEmpty(batch)) {
            fileDocumentRepository.saveAll(batch);
        }
    }

    @Transactional
    public List<FileMigrationError> migrateFileDocuments(final Set<String> fileUuids) {
        List<FileMigrationError> migrationErrors = new ArrayList<>();

        // Fetch existing (temporary) file documents from DB
        List<FileDocument> documentsToMigrate = collectFileDocumentsToMigrate(fileUuids, migrationErrors);

        // Fetch actual files from FTP server
        final String ftpServertDirectory = ftpProperties.getServerUnderlyingAgreementsDirectory();
        List<String> filePaths = documentsToMigrate.stream()
                .map(FileDocument::getFileName)
                .map(fileName -> ftpServertDirectory + "/" + fileName)
                .toList();
        List<FtpFileDTOResult> ftpFileDTOResults = ftpService.fetchFiles(filePaths);

        for (FtpFileDTOResult ftpFileDTOResult : ftpFileDTOResults) {
            FileDocument fileDocument = documentsToMigrate.stream()
                    .filter(att -> att.getFileName().equals(ftpFileDTOResult.getFileDTO().getFileName()))
                    .findFirst()
                    .get();

            if (ftpFileDTOResult.getErrorReport() != null) {
                migrationErrors.add(FileMigrationError.builder()
                        .uuid(fileDocument.getUuid())
                        .fileName(fileDocument.getFileName())
                        .errorReport(ftpFileDTOResult.getErrorReport())
                        .build());
            } else {
                FileDTO fileDTO = ftpFileDTOResult.getFileDTO();

                // Validate file
                try {
                    fileValidatorMigrationService.validateFileDTO(fileDTO);
                } catch (Exception e) {
                    migrationErrors.add(FileMigrationError.builder()
                            .uuid(fileDocument.getUuid())
                            .fileName(fileDocument.getFileName())
                            .errorReport(e.getMessage())
                            .build());
                    continue;
                }

                // Update file with actual values fetched from FTP server
                fileDocument.setFileContent(fileDTO.getFileContent());
                fileDocument.setFileSize(fileDTO.getFileSize());
                fileDocument.setFileType(fileDTO.getFileType());
                fileDocument.setStatus(FileStatus.SUBMITTED);
                fileDocumentRepository.save(fileDocument);
            }
        }

        return migrationErrors;
    }

    private List<FileDocument> collectFileDocumentsToMigrate(final Set<String> fileUuids, List<FileMigrationError> migrationErrors) {
        List<FileDocument> documentsToMigrate = new ArrayList<>();
        
        for (String uuid : fileUuids) {
            Optional<FileDocument> fileDocumentOptional = fileDocumentRepository.findByUuid(uuid);

            if (fileDocumentOptional.isEmpty()) {
                migrationErrors.add(FileMigrationError.builder()
                        .uuid(uuid)
                        .errorReport("File not found")
                        .build());
            } else {
                FileDocument fileDocument = fileDocumentOptional.get();
                if (!FileStatus.PENDING_MIGRATION.equals(fileDocument.getStatus())) {
                    migrationErrors.add(FileMigrationError.builder()
                            .uuid(fileDocument.getUuid())
                            .fileName(fileDocument.getFileName())
                            .errorReport("File not in pending migration status")
                            .build());
                } else {
                    documentsToMigrate.add(fileDocument);
                }
            }
        }
        return documentsToMigrate;
    }
}
