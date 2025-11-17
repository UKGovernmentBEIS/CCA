package uk.gov.cca.api.migration.cca3inprogressfacilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileDTOResult;
import uk.gov.cca.api.migration.ftp.FtpFileGenericException;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3InProgressFacilitiesMigrationService extends MigrationBaseService {
    
    private final FtpProperties ftpProperties;
    private final FtpFileService ftpFileService;
    private final Cca3InProgressFacilitiesMigrationValidationService validationService;
    private final Cca3InProgressFacilitiesService cca3InProgressFacilitiesService;
    
    @Override
    public String getResource() {
        return "cca3-in-progress-facilities";
    }
    
    @Override
    public List<String> migrate(String ids) {
        
        String csvInput = getCsvInputFromFtpServer();
        
        Cca3InProgressFacilityMigrationParseResult parseResult = Cca3InProgressFacilityParser.parse(csvInput);
        
        List<String> allErrors = new ArrayList<>(parseResult.getParsingErrors());
        
        validationService.validateData(parseResult.getSuccessfullyParsedInProgressFacilities(), allErrors);
        
        if(CollectionUtils.isEmpty(allErrors)) {
        	
        	List<String> accountBusinessIds = parseResult.getSuccessfullyParsedInProgressFacilities().stream()
                    .map(Cca3InProgressFacilityVO::getTargetUnitId)
                    .distinct()
                    .toList();

            List<RequestTask> unaReviewRequestTasks =
                    cca3InProgressFacilitiesService.getUnderlyingAgreementReviewRequestTasksByTargetUnits(accountBusinessIds);
            
            if(CollectionUtils.isEmpty(unaReviewRequestTasks)) {
            	return List.of("No In-progress UnA tasks in review step found.");
            }

            allErrors = new ArrayList<>(
    				validationService.validate(parseResult.getSuccessfullyParsedInProgressFacilities(), unaReviewRequestTasks));
            
            if(CollectionUtils.isEmpty(allErrors)) {
            	cca3InProgressFacilitiesService.updateAllCca3Facilities(parseResult.getSuccessfullyParsedInProgressFacilities(), unaReviewRequestTasks, allErrors);
            }
        }
        
        if (CollectionUtils.isNotEmpty(allErrors)) {
            allErrors.add("Migration aborted due to failures.");
        }
        
        return allErrors;
    }
    
    private @NotNull String getCsvInputFromFtpServer() {
		final String filePath = ftpProperties.getServerCca3InProgressFacilitiesMigrationDirectory() + "/"
                + ftpProperties.getServerCca3InProgressFacilitiesMigrationSourceFile();
        
        final FtpFileDTOResult fileDTOResult = ftpFileService.fetchFile(filePath);
        if (fileDTOResult.getErrorReport() != null) {
            throw new FtpFileGenericException("Error fetching file from the FTP server: " + fileDTOResult.getErrorReport());
        }
        
        FileDTO fileDTO = fileDTOResult.getFileDTO();
        
        return new String(fileDTO.getFileContent(), StandardCharsets.UTF_8);
    }
}
