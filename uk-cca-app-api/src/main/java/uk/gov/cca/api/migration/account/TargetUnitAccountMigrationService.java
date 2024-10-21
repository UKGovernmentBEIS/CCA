package uk.gov.cca.api.migration.account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationService;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service.TargetUnitAccountCreationValidationService;
import uk.gov.netz.api.common.utils.ExceptionUtils;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class TargetUnitAccountMigrationService extends MigrationBaseService {
    
    private final JdbcTemplate migrationJdbcTemplate;
    private final Validator validator;
    private final TargetUnitAccountDTOBuilder targetUnitAccountDTOBuilder;
    private final TargetUnitAccountCreationValidationService targetUnitAccountCreationValidationService;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    private final SubsectorAssociationService subsectorAssociationService;
    private final TargetUnitAccountCreationMigrationService targetUnitAccountCreationMigrationService;
    
   List<Pair<Long, String>> legacyCountries = new ArrayList<>();
    
    private static final String LIVE_TARGET_UNITS_QUERY_STMT = 
            "SELECT tu.[target_unit_pk]\r\n"
            + "      ,tu.[tu_id]\r\n"
            + "      ,sec.[sect_id]\r\n"
            + "      ,subsec.[sub_sector_name]\r\n"
            + "      ,tu.[operator_name]\r\n"
            + "      ,tu.[company_registration_number]\r\n"
            + "      ,tu.[sic_code]\r\n"
            + "      ,tu.[tu_add_line1]\r\n"
            + "      ,tu.[tu_add_line2]\r\n"
            + "      ,tu.[tu_add_county]\r\n"
            + "      ,tu.[tu_add_city]\r\n"
            + "      ,tu.[tu_postcode]\r\n"
            + "      ,tu.[tu_country]\r\n"
            + "      ,tu.[rp_forename]\r\n"
            + "      ,tu.[rp_surname]\r\n"
            + "      ,tu.[rp_email]\r\n"
            + "      ,tu.[rp_role]\r\n"
            + "      ,tu.[rp_add_line1]\r\n"
            + "      ,tu.[rp_add_line2]\r\n"
            + "      ,tu.[rp_add_city]\r\n"
            + "      ,tu.[rp_add_county]\r\n"
            + "      ,tu.[rp_add_postcode]\r\n"
            + "      ,tu.[rp_add_country]\r\n"
            + "      ,tu.[ac_title]\r\n"
            + "      ,tu.[ac_forename]\r\n"
            + "      ,tu.[ac_surname]\r\n"
            + "      ,tu.[ac_email]\r\n"
            + "      ,tu.[ac_role]\r\n"
            + "      ,tu.[ac_add_line1]\r\n"
            + "      ,tu.[ac_add_line2]\r\n"
            + "      ,tu.[ac_add_city]\r\n"
            + "      ,tu.[ac_add_county]\r\n"
            + "      ,tu.[ac_add_postcode]\r\n"
            + "      ,tu.[ac_add_country]\r\n"
            + "      ,tu.[ac_telephone]\r\n"
            + "      ,tu.[currently_in_cca]\r\n"
            + "      ,tu.[financially_independent]\r\n"
            + "      ,tu.[agreement_version]\r\n"
            + "      ,tu.[tu_status]\r\n"
            + "      ,sec.[sect_id]\r\n"
            + "      ,subsec.[sub_sector_name]\r\n"
            + "  FROM [tbl_target_units] tu\r\n"
            + "  join [tbl_sectors] sec on sec.sect_pk = tu.sect_id\r\n"
            + "  left join tbl_sub_sectors subsec on subsec.sub_sector_name = tu.subsector_name\r\n"
            + "  WHERE tu_status = 2 AND tu.currently_in_cca = 1\r\n";
    
    private static final String COUNTRIES_QUERY_STMT = 
            "SELECT\r\n"
            + "  [country_id]\r\n"
            + " ,[country]\r\n"
            + "FROM [tbl_country]\r\n"
            + "WHERE 1 = 1";

    @Override
    public String getResource() {
        return "target-unit-accounts";
    }
    
    private List<Pair<Long, String>> getCountries() {
        return migrationJdbcTemplate.query(COUNTRIES_QUERY_STMT, (rs, rowNum) -> {
            return Pair.of(rs.getLong("country_id"), rs.getString("country"));
        });
    }

    @Override
    public List<String> migrate(String businessIds) {
        final String query = TargetUnitAccountHelper.constructQuery(LIVE_TARGET_UNITS_QUERY_STMT, businessIds);

        List<String> results = new ArrayList<>();
        AtomicInteger failedCounter = new AtomicInteger(0);
        
        legacyCountries = getCountries();
        
        List<TargetUnitAccountVO> targetUnits = migrationJdbcTemplate.query(query, new TargetUnitAccountVORowMapper());
        
        List<TargetUnitAccountVO> sortedTargetUnits = targetUnits.stream()
                .sorted(Comparator.comparingLong(TargetUnitAccountVO::getOriginalTuPk))
                .toList();
        
        for (TargetUnitAccountVO targetUnit: sortedTargetUnits) {
            List<String> migrationResults = migrateTargetUnitAccount(targetUnit, failedCounter);
            results.addAll(migrationResults);
        }

        results.add("migration of target units results: " + failedCounter + "/" + targetUnits.size() + " failed");
        return results;
    }
    
    public List<String> migrateTargetUnitAccount(TargetUnitAccountVO targetUnitVO, AtomicInteger failedCounter) {
        List<String> results = new ArrayList<>();
        
        if (StringUtils.isBlank(targetUnitVO.getSectorAcronym())) {
            results.add(TargetUnitAccountHelper.constructErrorMessage(targetUnitVO, "Sector is empty",
                    targetUnitVO.getSectorAcronym()));
            failedCounter.incrementAndGet();
            return results;
        }
        
        try {

            Optional<Long> sectorIdOptional = sectorAssociationQueryService.getSectorAssociationIdByAcronym(targetUnitVO.getSectorAcronym());
            if(sectorIdOptional.isEmpty()) {
                results.add(TargetUnitAccountHelper.constructErrorMessage(targetUnitVO, "Sector not found",
                        targetUnitVO.getSectorAcronym()));
                failedCounter.incrementAndGet();
                return results;
            }

            Long subSectorId = null;
            if(StringUtils.isNotBlank(targetUnitVO.getSubsectorName())) {
                subSectorId = subsectorAssociationService.getIdByName(targetUnitVO.getSubsectorName());
            }

            TargetUnitAccountDTO targetUnitDTO = targetUnitAccountDTOBuilder
                    .constructTargetUnitAccountDTO(targetUnitVO, sectorIdOptional.get(), subSectorId, legacyCountries);

            Set<ConstraintViolation<TargetUnitAccountDTO>> accountViolations = validator.validate(targetUnitDTO);

            if (!accountViolations.isEmpty()) {
                accountViolations.forEach(v -> results.add(TargetUnitAccountHelper.constructErrorMessage(targetUnitVO,
                        v.getMessage(), v.getPropertyPath() + ":" + v.getInvalidValue())));
                failedCounter.incrementAndGet();
                return results;
            }

            targetUnitAccountCreationValidationService.validate(targetUnitDTO);

            targetUnitAccountCreationMigrationService.createMigratedTargetUnitAccount(targetUnitDTO);

            results.add(TargetUnitAccountHelper.constructSuccessMessage(targetUnitVO));
            
        } catch (Exception ex) {
            failedCounter.incrementAndGet();
            
            log.error("migration of target unit: {} failed with {}",
                    targetUnitVO.getTuId(), ExceptionUtils.getRootCause(ex).getMessage());
            
            results.add(TargetUnitAccountHelper.constructErrorMessage(targetUnitVO,
                    ExceptionUtils.getRootCause(ex).getMessage(), null));
        }
        return results;
    }
}
