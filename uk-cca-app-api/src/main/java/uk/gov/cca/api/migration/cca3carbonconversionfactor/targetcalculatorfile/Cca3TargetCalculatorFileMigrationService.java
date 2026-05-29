package uk.gov.cca.api.migration.cca3carbonconversionfactor.targetcalculatorfile;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.cca3carbonconversionfactor.Cca3CarbonConversionFactorService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3TargetCalculatorFileMigrationService extends MigrationBaseService {

    final Cca3TargetCalculatorFileService cca3TargetCalculatorFileService;
    private final Cca3CarbonConversionFactorService cca3CarbonConversionFactorService;
        
    @Override
    public String getResource() {
        return "cca3-target-calculator-file";
    }
    
    @Override
    public List<String> migrate(String inputFiles) {
    	// Get all files from directory and upload them to DB as PENDING_MIGRATION
    	 List<String> errors = cca3TargetCalculatorFileService.uploadFilesToDatabase();
    	
    	// If there were errors during upload, abort the migration
        if(!CollectionUtils.isEmpty(errors)) {
        	errors.add("Migration aborted due to failures during file upload.");
        	return errors;
        }

        // Get all calculator files that are pending migration
        Set<String> duplicateFacilityBusinessIds = new HashSet<>();
        Map<String, FileInfoDTO> facilityCalculatorFileMap = cca3TargetCalculatorFileService.getCalculatorFilesByFacilityBusinessId(duplicateFacilityBusinessIds);
        
        if (facilityCalculatorFileMap == null || facilityCalculatorFileMap.isEmpty()) {
            errors.add("No carbon conversion factors provided.");
            return errors;
        }
        
        if(!CollectionUtils.isEmpty(duplicateFacilityBusinessIds)) {
        	errors.add("There are multiple files for the following facility IDs: " + duplicateFacilityBusinessIds + 
        			". Please ensure only one file per facility ID is present and try again.");
        	return errors;
        }
        
        // Find eligible accounts with their facilities
        Map<Long, List<String>> facilitiesByAccountId = cca3CarbonConversionFactorService.findEligibleAccounts();       

        // For each eligible account, update calculator files in BO and change file status to SUBMITTED
        facilitiesByAccountId.forEach((accountId, facilityBusinessIds) -> 
        		cca3TargetCalculatorFileService.processAccount(errors, facilityCalculatorFileMap, accountId, facilityBusinessIds));
        
        // Determine which facilities are eligible but not provided, and which are provided but not eligible
        cca3CarbonConversionFactorService.findEligibleAndProvidedFacilitiesDiff(errors, facilitiesByAccountId, facilityCalculatorFileMap);

        return errors;
    }
}
