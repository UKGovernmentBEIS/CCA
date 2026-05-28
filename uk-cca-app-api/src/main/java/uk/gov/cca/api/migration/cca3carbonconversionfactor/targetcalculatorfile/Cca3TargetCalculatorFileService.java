package uk.gov.cca.api.migration.cca3carbonconversionfactor.targetcalculatorfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.cca.api.migration.ftp.GenericFtpResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
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
public class Cca3TargetCalculatorFileService {
	
    private final FtpFileService ftpService;
    private final FtpProperties ftpProperties;
    private final CcaFileAttachmentService ccaFileAttachmentService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final List<FileValidatorService> fileValidators;
    private final FileAttachmentRepository fileAttachmentRepository;

    private static final FileAttachmentMapper FILE_ATTACHMENT_MAPPER = Mappers.getMapper(FileAttachmentMapper.class);
    private static final String ERROR_FORMAT = "%s: %s";
    private static final Set<String> ALLOWED_ATTACHMENTS = Stream.of(FileType.XLSX.getMimeTypes(), FileType.XLS.getMimeTypes())
            .flatMap(Collection::stream).collect(Collectors.toSet());
    
    private static final String CCA3_CARBON_CONVERSION_FACTOR_MIGRATION_PREFIX = "CCA3 Carbon Conversion Factor";
    private static final int BATCH_SIZE = 100;
    private static final String FORWARD_SLASH = "/";
    
    @Transactional
    public List<String> uploadFilesToDatabase() {
    	List<String> errors = new ArrayList<>();
    	final String sftpDirectory = ftpProperties.getServerCca3TargetCalculatorFileMigrationDirectory();
        GenericFtpResult<List<String>> files = ftpService.listFiles(sftpDirectory);

		if (files.getErrorReport() != null) {
			throw new BusinessException(ErrorCode.UPLOAD_FILE_FAILED_ERROR, files.getErrorReport());
		}
		
		final List<FileInfoDTO> filesMigrated = ccaFileAttachmentService
				.getAllByFileNameLikeAndStatus(CCA3_CARBON_CONVERSION_FACTOR_MIGRATION_PREFIX, FileStatus.PENDING_MIGRATION);
		List<String> filesToMigrate = files.getData().stream()
				.filter(name -> filesMigrated.stream().noneMatch(fm -> fm.getName().equals(name)))
				.map(name -> sftpDirectory + FORWARD_SLASH + name)
				.toList();

		ListUtils.partition(filesToMigrate, BATCH_SIZE).forEach(batch -> {
			try {
				errors.addAll(transferFilesToDatabase(batch));
			} catch (Exception e) {
				errors.add(String.format("Files'%s' could not be uploaded: %s", batch, e.getMessage()));
			}
		});

        return errors;
	}
    
    public Map<String, FileInfoDTO> getCalculatorFileMap(Set<String> duplicateFacilityBusinessIds) {
        Map<String, FileInfoDTO> result = new HashMap<>();
        ccaFileAttachmentService
                .getAllByFileNameLikeAndStatus(CCA3_CARBON_CONVERSION_FACTOR_MIGRATION_PREFIX, FileStatus.PENDING_MIGRATION)
                .forEach(fileInfo -> {
                    String originalName = fileInfo.getName();
                    String facilityBusinessId = getFacilityBusinessIdFromFilename(originalName);

                    String updatedName = originalName
                            .replace(facilityBusinessId + " " + CCA3_CARBON_CONVERSION_FACTOR_MIGRATION_PREFIX, "")
                            .strip();
                    fileInfo.setName(updatedName);

                    if (result.containsKey(facilityBusinessId)) {
                        duplicateFacilityBusinessIds.add(facilityBusinessId);
                    } else {
                        result.put(facilityBusinessId, fileInfo);
                    }
                });
        
        return result;
	}
    
    @Transactional
    public void processAccount(List<String> errors, Map<String, FileInfoDTO> facilityCalculatorFileMap, Long accountId, List<String> facilityBusinessIds) {
		Map<String, FileInfoDTO> applicableFacilities = facilityBusinessIds.stream()
		        .filter(facilityCalculatorFileMap::containsKey)
		        .collect(Collectors.toMap(
		                id -> id,
		                facilityCalculatorFileMap::get
		        ));
	
		if (!applicableFacilities.isEmpty()) {
			updateCalculatorFiles(accountId, applicableFacilities, errors);
		}		
	}
    
    public void updateCalculatorFiles(Long accountId, 
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

	private List<String> transferFilesToDatabase(List<String> files) {
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

	private String getFacilityBusinessIdFromFilename(String name) {
		return name.trim().split("\\s+")[0];
	}
}
