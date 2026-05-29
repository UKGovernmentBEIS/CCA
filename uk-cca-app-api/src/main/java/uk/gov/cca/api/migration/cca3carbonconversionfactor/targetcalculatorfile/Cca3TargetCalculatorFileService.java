package uk.gov.cca.api.migration.cca3carbonconversionfactor.targetcalculatorfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.cca.api.migration.ftp.GenericFtpResult;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3TargetCalculatorFileService {
	
    private final FtpFileService ftpService;
    private final FtpProperties ftpProperties;
    private final CcaFileAttachmentService ccaFileAttachmentService;
    private final Cca3TargetCalculatorFileUpdateService cca3TargetCalculatorFileUpdateService;

    
    private static final String CCA3_CARBON_CONVERSION_FACTOR_MIGRATION_PREFIX = "CCA3 Carbon Conversion Factor";
    private static final int BATCH_SIZE = 100;
    private static final String FORWARD_SLASH = "/";
    
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
				errors.addAll(cca3TargetCalculatorFileUpdateService.transferFilesToDatabase(batch));
			} catch (Exception e) {
				errors.add(String.format("Files'%s' could not be uploaded: %s", batch, e.getMessage()));
			}
		});

        return errors;
	}
    
    public Map<String, FileInfoDTO> getCalculatorFilesByFacilityBusinessId(Set<String> duplicateFacilityBusinessIds) {
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
			cca3TargetCalculatorFileUpdateService.updateCalculatorFilesDetails(accountId, applicableFacilities, errors);
		}		
	}

	private String getFacilityBusinessIdFromFilename(String name) {
		return name.trim().split("\\s+")[0];
	}
}
