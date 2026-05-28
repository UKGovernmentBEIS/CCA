package uk.gov.cca.api.migration.cca3carbonconversionfactor;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileDTOResult;
import uk.gov.cca.api.migration.ftp.FtpFileGenericException;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3CarbonConversionFactorMigrationService extends MigrationBaseService {

	private final FtpProperties ftpProperties;
    private final FtpFileService ftpFileService;
    private final Cca3CarbonConversionFactorService cca3CarbonConversionFactorService;
    
    private static final String FORWARD_SLASH = "/";
    
    @Override
    public String getResource() {
        return "cca3-carbon-conversion-factor";
    }
    
    @Override
    public List<String> migrate(String ids) {

        String csvInput = getCsvInputFromFtpServer();
        
        List<String> allErrors = new ArrayList<>();
        
        Cca3CarbonConversionFactorMigrationParseResult parseResult = Cca3CarbonConversionFactorMigrationParser.parse(csvInput);
        
        // If there are parsing errors, abort the migration
        if(!CollectionUtils.isEmpty(parseResult.getParsingErrors())) {
        	allErrors.addAll(parseResult.getParsingErrors());
        	allErrors.add("Migration aborted due to failures.");
        	return allErrors;
        }
        
        Map<String, BigDecimal> carbonConversionFactorMap = parseResult.getParsedCarbonConversionFactorMap();
        if (carbonConversionFactorMap == null || carbonConversionFactorMap.isEmpty()) {
            allErrors.add("No carbon conversion factors provided.");
            return allErrors;
        }
        
        Map<Long, List<String>> facilitiesAccountsMap = cca3CarbonConversionFactorService.findEligibleAccounts();
        
        // For each eligible account, update the conversion factor for the facilities that are provided in the migration file
        facilitiesAccountsMap.forEach((accountId, facilityBusinessIds) ->
        		cca3CarbonConversionFactorService.processAccount(allErrors, carbonConversionFactorMap, accountId, facilityBusinessIds));
        
        // Determine which facilities are eligible but not provided, and which are provided but not eligible
        cca3CarbonConversionFactorService.findEligibleAndProvidedFacilitiesDiff(allErrors, facilitiesAccountsMap, carbonConversionFactorMap);
        
        return allErrors;
    }

	private @NotNull String getCsvInputFromFtpServer() {
        final String filePath = ftpProperties.getServerCca3CarbonConversionFactorMigrationDirectory() + FORWARD_SLASH
                + ftpProperties.getServerCca3CarbonConversionFactorMigrationSourceFile();
        
        final FtpFileDTOResult fileDTOResult = ftpFileService.fetchFile(filePath);
        if (fileDTOResult.getErrorReport() != null) {
            throw new FtpFileGenericException("Error fetching file from the FTP server: " + fileDTOResult.getErrorReport());
        }
        
        FileDTO fileDTO = fileDTOResult.getFileDTO();
        
        return new String(fileDTO.getFileContent(), StandardCharsets.UTF_8);
    }
}
