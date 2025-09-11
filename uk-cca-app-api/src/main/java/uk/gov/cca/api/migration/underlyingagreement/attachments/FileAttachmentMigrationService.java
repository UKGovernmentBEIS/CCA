package uk.gov.cca.api.migration.underlyingagreement.attachments;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;
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
import uk.gov.netz.api.common.domain.ResourceFile;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.utils.ResourceFileUtils;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@RequiredArgsConstructor
@Log4j2
public class FileAttachmentMigrationService {

    private static final FileAttachmentMigrationMapper fileAttachmentMapper = Mappers.getMapper(FileAttachmentMigrationMapper.class);
    private static final String FORWARD_SLASH = "/";
    
    private final FtpProperties ftpProperties;
    private final FtpFileService ftpService;

    private final FileAttachmentRepository fileAttachmentRepository;
    private final FileValidatorMigrationService fileValidatorMigrationService;
        
    public List<String> listUnderlyingAgreementAttachments() {
        final String sftpDirectory = ftpProperties.getServerUnaAttachmentsDirectory();
        GenericFtpResult<List<String>> files = ftpService.listFiles(sftpDirectory);
        if (files.getErrorReport() != null) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT, files.getErrorReport());
        }
        return files.getData();
    }

    @Transactional
    public void saveFileAttachmentMetadata(final List<String> files) {
        final int batchSize = 1000;
        List<FileAttachment> batch = new ArrayList<>();
        for (String filename : files) {
            batch.add(fileAttachmentMapper.toFileAttachment(filename));
            if (batch.size() == batchSize) {
                fileAttachmentRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (CollectionUtils.isNotEmpty(batch)) {
            fileAttachmentRepository.saveAll(batch);
        }
    }
    
    @Transactional
    public List<FileMigrationError> migrateFileAttachments(final Set<String> fileUuids) {
        List<FileMigrationError> migrationErrors = new ArrayList<>();

        List<FileAttachment> attachmentsToMigrate = collectFileAttachmentsToMigrate(fileUuids, migrationErrors);
        if(CollectionUtils.isEmpty(attachmentsToMigrate)) {
            return migrationErrors;
        }
        
        // Fetch actual files from ClassPath
        for(FileAttachment attachment: attachmentsToMigrate) {
            try {
                ResourceFile resourceFile = ResourceFileUtils.getResourceFile("migration" + File.separator + "attachments" + File.separator + attachment.getFileName());
                attachment.setFileContent(resourceFile.getFileContent());
                attachment.setFileSize(resourceFile.getFileSize());
                attachment.setFileType(resourceFile.getFileType());
                attachment.setStatus(FileStatus.SUBMITTED);
                fileAttachmentRepository.save(attachment);
            } catch (IOException e) {
                log.error("Failed to save file: {}", attachment.getFileName());
            }
        }
        
        attachmentsToMigrate.removeIf(fileAttachment -> !FileStatus.PENDING_MIGRATION.equals(fileAttachment.getStatus()));
        
        // Fetch actual files from FTP server
        final String ftpServertDirectory = ftpProperties.getServerUnaAttachmentsDirectory();
        List<String> filePaths = attachmentsToMigrate.stream()
                .map(fileAttachment -> new String(fileAttachment.getFileContent(), StandardCharsets.UTF_8))
                .map(fileStoredName -> ftpServertDirectory + FORWARD_SLASH + fileStoredName)
                .toList();
        List<FtpFileDTOResult> ftpFileDTOResults = ftpService.fetchFiles(filePaths);

        for (FtpFileDTOResult ftpFileDTOResult : ftpFileDTOResults) {
            FileAttachment fileAttachment = attachmentsToMigrate.stream()
                    .filter(att -> (new String(att.getFileContent(), StandardCharsets.UTF_8)).equals(ftpFileDTOResult.getFileDTO().getFileName()))
                    .findFirst()
                    .orElseThrow();

            if (ftpFileDTOResult.getErrorReport() != null) {
                migrationErrors.add(FileMigrationError.builder()
                        .uuid(fileAttachment.getUuid())
                        .fileName(new String(fileAttachment.getFileContent(), StandardCharsets.UTF_8))
                        .errorReport(ftpFileDTOResult.getErrorReport())
                        .build());
            } else {
                FileDTO fileDTO = ftpFileDTOResult.getFileDTO();

                // Validate file
                try {
                    fileValidatorMigrationService.validateFileDTO(fileDTO);
                } catch (Exception e) {
                    migrationErrors.add(FileMigrationError.builder()
                            .uuid(fileAttachment.getUuid())
                            .fileName(new String(fileAttachment.getFileContent(), StandardCharsets.UTF_8))
                            .errorReport(e.getMessage())
                            .build());
                    continue;
                }

                // Update file with actual values fetched from FTP server
                fileAttachment.setFileContent(fileDTO.getFileContent());
                fileAttachment.setFileSize(fileDTO.getFileSize());
                fileAttachment.setFileType(fileDTO.getFileType());
                fileAttachment.setStatus(FileStatus.SUBMITTED);
                fileAttachmentRepository.save(fileAttachment);
            }
        }

        return migrationErrors;
        
    }

    private List<FileAttachment> collectFileAttachmentsToMigrate(final Set<String> fileUuids, List<FileMigrationError> migrationErrors) {
        List<FileAttachment> attachmentsToMigrate = new ArrayList<>();
        
        Map<String, FileAttachment> persistentAttachments = fileAttachmentRepository.findAllByUuidIn(fileUuids).stream()
                .collect(Collectors.toMap(FileAttachment::getUuid, attachment -> attachment));

        fileUuids.stream().forEach(uuid -> {
            if (persistentAttachments.get(uuid) == null) {
                migrationErrors.add(FileMigrationError.builder()
                        .uuid(uuid)
                        .errorReport("File attachment not found")
                        .build());
            } else {
                FileAttachment fileAttachment = persistentAttachments.get(uuid);
                if (!FileStatus.PENDING_MIGRATION.equals(fileAttachment.getStatus())) {
                    migrationErrors.add(FileMigrationError.builder()
                            .uuid(fileAttachment.getUuid())
                            .fileName(fileAttachment.getFileName())
                            .errorReport("File attachment not in pending migration status")
                            .build());
                } else if (fileAttachment.getFileName().equals(new String(fileAttachment.getFileContent(), StandardCharsets.UTF_8))) {
                    migrationErrors.add(FileMigrationError.builder()
                            .uuid(fileAttachment.getUuid())
                            .fileName(fileAttachment.getFileName())
                            .errorReport("File attachment not used")
                            .build());
                } else {
                    attachmentsToMigrate.add(fileAttachment);
                }
            }
        });
        
        return attachmentsToMigrate;
    }

}
