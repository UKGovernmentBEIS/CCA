package uk.gov.cca.api.migration.sectorassociation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemesDTO;
import uk.gov.netz.api.common.utils.ExceptionUtils;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class SectorAssociationMigrationService extends MigrationBaseService {
	
    private final JdbcTemplate migrationJdbcTemplate;
    private final Validator validator;
    private final SectorAssociationCreationMigrationService creationService;
    private final SectorAssociationDTOBuilder sectorDTOBuilder;
    
    private static final String SECTORS_QUERY = 
    		"SELECT \r\n"
    		+ "  [sect_pk] \r\n"
    		+ " ,[sect_id] \r\n"
    		+ " ,[short_name] \r\n"
    		+ " ,[legal_name] \r\n"
    		+ " ,[ei_epr] \r\n"
    		+ " ,[address_line1] \r\n"
    		+ " ,[address_line2] \r\n"
    		+ " ,[address_city] \r\n"
    		+ " ,[address_county] \r\n"
    		+ " ,[address_postcode] \r\n"
    		+ " ,[contact_title] \r\n"
    		+ " ,[contact_name] \r\n"
    		+ " ,[contact_surname] \r\n"
    		+ " ,[contact_telephone] \r\n"
    		+ " ,[ac_email] \r\n"
    		+ " ,[contact_address_line1] \r\n"
    		+ " ,[contact_address_line2] \r\n"
    		+ " ,[contact_address_city] \r\n"
    		+ " ,[contact_address_county] \r\n"
    		+ " ,[contact_address_postcode] \r\n"
    		+ " ,[sect_target_type] \r\n"
    		+ " ,[throughput_units] \r\n"
    		+ " ,[energy_carbon_units] \r\n"
    		+ " ,[tp1_sector_commitment] \r\n"
    		+ " ,[tp2_sector_commitment] \r\n"
    		+ " ,[tp3_sector_commitment] \r\n"
    		+ " ,[tp4_sector_commitment] \r\n"
    		+ " ,[tp5_sector_commitment] \r\n"
    		+ " ,[tp6_sector_commitment] \r\n"
    		+ " ,[sector_definition] \r\n"
    		+ " ,[uma_date] \r\n"
    		+ " ,(SELECT COUNT(*) FROM tbl_sub_sectors subsec WHERE subsec.sector_id = sect.sect_pk) subsectors_cnt \r\n"
    		+ "FROM [tbl_sectors] sect \r\n"
    		+ "WHERE 1 = 1";
    
    private static final String SUBSECTORS_BY_SECTOR_ID_QUERY = 
    		"SELECT \r\n"
            + "  [sub_sector_pk] \r\n"
    		+ " ,[sub_sector_name] \r\n"
    		+ " ,[target_type] \r\n"
    		+ " ,[throughput_unit] \r\n"
    		+ " ,[energy_carbon_unit] \r\n"
    		+ " ,[tp1_sector_commitment] \r\n"
    		+ " ,[tp2_sector_commitment] \r\n"
    		+ " ,[tp3_sector_commitment] \r\n"
    		+ " ,[tp4_sector_commitment] \r\n"
    		+ " ,[tp5_sector_commitment] \r\n"
    		+ " ,[tp6_sector_commitment] \r\n"
    		+ "FROM [tbl_sub_sectors] \r\n"
    		+ "WHERE 1 = 1";
    
    public SectorAssociationMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate, Validator validator,
			SectorAssociationCreationMigrationService creationService, SectorAssociationDTOBuilder sectorDTOBuilder) {
		super();
		this.migrationJdbcTemplate = migrationJdbcTemplate;
		this.validator = validator;
		this.creationService = creationService;
		this.sectorDTOBuilder = sectorDTOBuilder;
	}

	@Override
    public String getResource() {
        return "sector-associations";
    }
	
    @Override
    public List<String> migrate(String ids) {
        List<String> results = new ArrayList<>();
        AtomicInteger failedCounter = new AtomicInteger(0);

        Map<String, List<SectorAssociationVO>> vos = getSectorAssociations(ids);
        for (Map.Entry<String, List<SectorAssociationVO>> entry : vos.entrySet()) {
            List<String> migrationResults = migrateSectorAssociation(entry.getValue(), failedCounter);
            results.addAll(migrationResults);
        }
        results.add("migration of sectors results: " + failedCounter.get() + "/" + vos.size() + " failed");
        return results;
    }
	
    private Map<String, List<SectorAssociationVO>> getSectorAssociations(String ids) {
        String sql = SectorAssociationMigrationHelper.constructQuery(SECTORS_QUERY, ids);

        return migrationJdbcTemplate.query(sql, new SectorAssociationVORowMapper()).stream()
                .collect(Collectors.groupingBy(SectorAssociationVO::getAcronym));
    }

    private List<SubSectorAssociationVO> getSubSectorsBySectorId(Long sectorId) {
        String sql = String.format(SUBSECTORS_BY_SECTOR_ID_QUERY + " and sector_id = %s", sectorId);

        return migrationJdbcTemplate.query(sql, new SubSectorAssociationVORowMapper()).stream().toList();
    }
	
    private List<String> migrateSectorAssociation(List<SectorAssociationVO> sectorVOs, AtomicInteger failedCounter) {
        List<String> results = new ArrayList<>();

        if (sectorVOs.size() != 1) {
            results.add(SectorAssociationMigrationHelper.createErrorMessageForDuplicateAcronym(sectorVOs));
            failedCounter.incrementAndGet();
            return results;
        }

        SectorAssociationVO sectorVO = sectorVOs.get(0);
        SectorAssociationDTO sectorDTO = sectorDTOBuilder.constructSectorAssociation(sectorVO);
        boolean hasSubSectors = sectorVO.getSubsectorsCounter() != 0;

        SectorAssociationSchemeDTO sectorSchemeDTO = sectorDTOBuilder.constructSectorAssociationScheme(sectorVO, hasSubSectors);
        List<SubsectorAssociationSchemesDTO> subSectorSchemeDTOs = new ArrayList<>();
        
        if (hasSubSectors) {
            List<SubSectorAssociationVO> subSectorVOs = getSubSectorsBySectorId(sectorVO.getOriginalSectorId());
            subSectorVOs.forEach(subSectorVO -> subSectorSchemeDTOs.add(sectorDTOBuilder.constructSubsectorAssociationSchemeDTO(subSectorVO)));
        }

        Set<ConstraintViolation<SectorAssociationDTO>> sectorViolations  = validator.validate(sectorDTO);
        Set<ConstraintViolation<SectorAssociationSchemeDTO>> sectorSchemeViolations  = validator.validate(sectorSchemeDTO);
        
        Set<ConstraintViolation<SubsectorAssociationSchemesDTO>> subSectorViolations  = new HashSet<>();
        if (hasSubSectors) {
            subSectorSchemeDTOs.forEach(dto -> subSectorViolations.addAll(validator.validate(dto)));
        }
        
        if(sectorSchemeDTO.getUmaDate() == null) {
            results.add(SectorAssociationMigrationHelper.constructErrorMessage(sectorVOs.get(0), "Uma date is empty", null));
        }
        
        if(StringUtils.isEmpty(sectorSchemeDTO.getSectorDefinition())) {
            results.add(SectorAssociationMigrationHelper.constructErrorMessage(sectorVOs.get(0), "Sector definition is empty", null));
        }
        
        if (!sectorViolations.isEmpty() || !sectorSchemeViolations.isEmpty() || !subSectorViolations.isEmpty() || !results.isEmpty()) {
            failedCounter.incrementAndGet();
            
            sectorViolations.forEach(v -> results.add(SectorAssociationMigrationHelper.constructErrorMessage(sectorVO,
                    v.getMessage(), v.getPropertyPath().iterator().next().getName() + ":" + v.getInvalidValue())));
            sectorSchemeViolations.forEach(v -> results.add(SectorAssociationMigrationHelper.constructErrorMessage(sectorVO,
                    v.getMessage(), v.getPropertyPath() + ":" + v.getInvalidValue())));
            subSectorViolations.forEach(v -> results.add(SectorAssociationMigrationHelper.constructErrorMessage(sectorVO,
                    v.getMessage(), v.getPropertyPath().iterator().next().getName() + ":" + v.getInvalidValue())));
            
            return results;
        }

        try {
            creationService.createMigratedSectorAssociation(sectorDTO, sectorSchemeDTO, subSectorSchemeDTOs);
            results.add(SectorAssociationMigrationHelper.constructSuccessMessage(sectorVO));
        } catch (Exception ex) {
            failedCounter.incrementAndGet();
            
            log.error("migration of sector association : {} failed with {}",
                    sectorVO.getAcronym(), ExceptionUtils.getRootCause(ex).getMessage());
            
            results.add(SectorAssociationMigrationHelper.constructErrorMessage(sectorVO,
                    ExceptionUtils.getRootCause(ex).getMessage(), null));
        }
        
        return results;
    }
}
