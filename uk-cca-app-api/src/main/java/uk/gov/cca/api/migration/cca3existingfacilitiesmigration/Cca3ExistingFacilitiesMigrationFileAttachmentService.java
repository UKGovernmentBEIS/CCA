package uk.gov.cca.api.migration.cca3existingfacilitiesmigration;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.netz.api.files.attachments.transform.FileAttachmentMapper;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.service.FileValidatorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationFileAttachmentService {

	private final FtpProperties ftpProperties;
    private final FtpFileService ftpService;
    private final List<FileValidatorService> fileValidators;
    private final FileAttachmentRepository fileAttachmentRepository;

    private static final FileAttachmentMapper FILE_ATTACHMENT_MAPPER = Mappers.getMapper(FileAttachmentMapper.class);
    private static final String ERROR_FORMAT = "%s: %s";
    private static final Set<String> ALLOWED_ATTACHMENTS = Stream.of(FileType.XLSX.getMimeTypes(), FileType.XLS.getMimeTypes())
            .flatMap(Collection::stream).collect(Collectors.toSet());

    @Transactional
	public List<String> transferFilesToDatabase(List<String> files) {
		List<String> errors = new ArrayList<>();
		List<FileAttachment> attachments = new ArrayList<>();

        ftpService.fetchFiles(files).forEach(ftpFileDTOResult -> {
            if(ftpFileDTOResult.getErrorReport() != null) {
                errors.add(String.format(ERROR_FORMAT, ftpFileDTOResult.getFileDTO().getFileName(), ftpFileDTOResult.getErrorReport()));
            }
            else if(!ALLOWED_ATTACHMENTS.contains(ftpFileDTOResult.getFileDTO().getFileType())
					&& !ftpFileDTOResult.getFileDTO().getFileName().equals(ftpProperties.getServerCca3ExistingFacilitiesMigrationSourceFile())) {
                errors.add(String.format(ERROR_FORMAT, ftpFileDTOResult.getFileDTO().getFileName(), "Not allowed file type"));
            }
			else if(ftpFileDTOResult.getFileDTO().getFileName().equals(ftpProperties.getServerCca3ExistingFacilitiesMigrationSourceFile())
					&& !ftpFileDTOResult.getFileDTO().getFileType().equals("text/csv")) {
				errors.add(String.format(ERROR_FORMAT, ftpFileDTOResult.getFileDTO().getFileName(), "Not csv file type"));
			}
            else {
                try {
                    attachments.add(createFileAttachment(ftpFileDTOResult.getFileDTO(), errors));
                } catch (IOException e) {
                    errors.add(String.format(ERROR_FORMAT, ftpFileDTOResult.getFileDTO().getFileName(), e.getMessage()));
                }
            }
        });

		if (!attachments.isEmpty()) {
			fileAttachmentRepository.saveAll(attachments);
		}

		return errors;
	}

	private FileAttachment createFileAttachment(FileDTO fileDTO, List<String> errors) throws IOException {
		List<String> validationErrors = new ArrayList<>();

		this.fileValidators.forEach(validator -> {
			try {
				validator.validate(fileDTO);
			} catch (RuntimeException e) {
				validationErrors.add(e.getMessage());
			}
		});

		if (!validationErrors.isEmpty()) {
			validationErrors.forEach(error -> errors.add(String.format(ERROR_FORMAT, fileDTO.getFileName(), error)));
			return null;
		}

		FileAttachment attachment = FILE_ATTACHMENT_MAPPER.toFileAttachment(fileDTO);
		attachment.setUuid(UUID.randomUUID().toString());
		attachment.setStatus(FileStatus.PENDING_MIGRATION);
		attachment.setCreatedBy(MigrationConstants.MIGRATION_PROCESS_USER);
		return attachment;
	}
}
