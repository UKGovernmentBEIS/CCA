package uk.gov.cca.api.migration.cca3carbonconversionfactor.targetcalculatorfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.netz.api.files.attachments.transform.FileAttachmentMapper;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.common.service.FileValidatorService;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3TargetCalculatorFileUpdateService {
	
    private final FtpFileService ftpService;
    private final CcaFileAttachmentService ccaFileAttachmentService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
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
            else if(!ALLOWED_ATTACHMENTS.contains(ftpFileDTOResult.getFileDTO().getFileType())) {
                errors.add(String.format(ERROR_FORMAT, ftpFileDTOResult.getFileDTO().getFileName(), "Not allowed file type"));
            }
            else {
                try {
                	FileAttachment attachment = createFileAttachment(ftpFileDTOResult.getFileDTO(), errors);
                	if (attachment != null) {
                	    attachments.add(attachment);
                	}
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
    
    public void updateCalculatorFilesDetails(Long accountId, 
    		Map<String, FileInfoDTO> facilityCalculatorFileMap, List<String> errors) {
    	try {
    		UnderlyingAgreementContainer unaContainer = underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId);
			Set<Facility> facilities = unaContainer.getUnderlyingAgreement().getFacilities();
			Map<UUID, String> underlyingAgreementAttachments = unaContainer.getUnderlyingAgreementAttachments();
			
			facilities.forEach(facility -> {
			    FileInfoDTO file = facilityCalculatorFileMap.get(facility.getFacilityItem().getFacilityId());

			    if (file != null) {
			        facility.getFacilityItem()
			                .getCca3BaselineAndTargets()
			                .getTargetComposition()
			                .setCalculatorFile(UUID.fromString(file.getUuid()));
			        underlyingAgreementAttachments.put(UUID.fromString(file.getUuid()), file.getName());
			    }
			});
			ccaFileAttachmentService.updateNameAndStatus(facilityCalculatorFileMap.values().stream().toList(), FileStatus.SUBMITTED);
        } catch (Exception e) {
            errors.add(String.format("Error updating target calculator files for facilities %s for account with ID %d: %s",
            		facilityCalculatorFileMap.keySet(), accountId, e.getMessage()));
        }	
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
