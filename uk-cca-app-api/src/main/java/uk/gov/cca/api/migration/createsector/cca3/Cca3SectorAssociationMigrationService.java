package uk.gov.cca.api.migration.createsector.cca3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.utils.CsvUtils;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.createsector.common.SectorAssociationCreationMigrationService;
import uk.gov.cca.api.migration.createsector.common.SectorAssociationDTOBuilder;
import uk.gov.cca.api.migration.createsector.common.SectorAssociationMigrationHelper;
import uk.gov.cca.api.migration.ftp.FtpFileDTOResult;
import uk.gov.cca.api.migration.ftp.FtpFileGenericException;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class Cca3SectorAssociationMigrationService extends MigrationBaseService {

	private final FtpProperties ftpProperties;
    private final FtpFileService ftpFileService;
    private final Cca3SectorAssociationMigrationValidationService cca3SectorAssociationMigrationValidationService;
    private final SectorAssociationCreationMigrationService creationService;
    private final Validator validator;
    private final SectorAssociationDTOBuilder sectorDTOBuilder;
    
    private static final String FORWARD_SLASH = "/";
    
    @Override
    public String getResource() {
        return "cca3-create-sector-association";
    }
    
    @Override
    public List<String> migrate(String ids) {

    	List<String> errors = new ArrayList<>();

		// Get csv from server
		FileDTO fileDTO = getCsvInputFromFtpServer();

		// Parse CSV
		List<Cca3SectorAssociationVO> cca3SectorAssociationVOList = CsvUtils
				.convertToModel(fileDTO, Cca3SectorAssociationVO.class, true, errors);
		
		// Validate csv data
		cca3SectorAssociationMigrationValidationService.validateCsvData(cca3SectorAssociationVOList, errors);
		
		if (errors.isEmpty()) {
		    for (Cca3SectorAssociationVO sectorVO : cca3SectorAssociationVOList) {
		        // Construct sector DTOs
		        SectorAssociationDTO sectorDTO = sectorDTOBuilder.constructSectorAssociation(sectorVO);
		        SectorAssociationSchemeDTO sectorSchemeDTO = sectorDTOBuilder.constructSectorAssociationScheme(sectorVO, false, SchemeVersion.CCA_3);

		        // DTO validations
		        Set<ConstraintViolation<SectorAssociationDTO>> sectorViolations = validator.validate(sectorDTO);
		        Set<ConstraintViolation<SectorAssociationSchemeDTO>> sectorSchemeViolations = validator.validate(sectorSchemeDTO);

		        if (!sectorViolations.isEmpty() || !sectorSchemeViolations.isEmpty()) {
		            sectorViolations.forEach(v ->
		                    errors.add(SectorAssociationMigrationHelper.constructErrorMessage(sectorVO, v.getMessage(), 
		                    		v.getPropertyPath().iterator().next().getName() + ":" + v.getInvalidValue())));
		            
		            sectorSchemeViolations.forEach(v ->
		                    errors.add(SectorAssociationMigrationHelper.constructErrorMessage(sectorVO, v.getMessage(),
		                    		v.getPropertyPath() + ":" + v.getInvalidValue())));
		            
		            continue;
		        }
		        
		        try {
		        	// Create new sectors
		            creationService.createMigratedSectorAssociation(sectorDTO, sectorSchemeDTO, List.of());
		            errors.add(SectorAssociationMigrationHelper.constructSuccessMessage(sectorVO));
		        } catch (Exception ex) {
		            log.error("migration of sector association : {} failed with {}",
		                    sectorVO.getAcronym(), ExceptionUtils.getRootCause(ex).getMessage());
		            
		            errors.add(SectorAssociationMigrationHelper.constructErrorMessage(sectorVO,
		                    ExceptionUtils.getRootCause(ex).getMessage(), null));
		        }
		    }
		}
		
		return errors;
    }

    private FileDTO getCsvInputFromFtpServer() {
		final String filePath = ftpProperties.getServerCca3SectorAssociationMigrationDirectory() + FORWARD_SLASH
				+ ftpProperties.getServerCca3CreateSectorAssociationMigrationSourceFile();

		final FtpFileDTOResult fileDTOResult = ftpFileService.fetchFile(filePath);
		if (fileDTOResult.getErrorReport() != null) {
			throw new FtpFileGenericException("Error fetching file from the FTP server: " + fileDTOResult.getErrorReport());
		}

		return fileDTOResult.getFileDTO();
	}
}
