package uk.gov.cca.api.migration.underlyingagreement.facilities;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.MigrationUtil;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementMigrationContainer;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementMigrationUtil;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class FacilityCreatedDateMigrationService {
    
    private final JdbcTemplate migrationJdbcTemplate;
    
    private static final String LIVE_FACILITIES_ENTRY_DATE_STMT = 
            "SELECT tf.[facility_pk] \r\n"
            + "      ,tf.[facility_id] \r\n"
            + "      ,tu.[tu_id] \r\n"
            + "      ,tf.[facility_entry_date] \r\n"
            + "  FROM [tbl_facility] tf \r\n"
            + "  inner join [tbl_target_units] tu on tf.tu_id = tu.target_unit_pk \r\n"
            + "  WHERE tf.[facility_status] = 2 AND tf.[facility_currently_in_cca] = 1 ";
    
    public FacilityCreatedDateMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
		super();
		this.migrationJdbcTemplate = migrationJdbcTemplate;
	}

	public void populate(List<String> eligibleTargetUnitIds, Map<String, UnderlyingAgreementMigrationContainer> migrationContainerMap) {
        Map<String, List<FacilityCreatedDateVO>> unaDetails = queryDetails(eligibleTargetUnitIds);

        unaDetails.forEach((targetUnitId, facilities) ->
            migrationContainerMap.get(targetUnitId)
                    .setFacilitiesCreatedDate(facilities.stream()
                            .collect(Collectors.toMap(
                                    fac -> MigrationUtil.convertLegacyToCcaBusinessId(fac.getFacilityId()),
                                    FacilityCreatedDateVO::getCreatedDate)))
            );
    }

    private Map<String, List<FacilityCreatedDateVO>> queryDetails(List<String> eligibleTargetUnitIds) {
        String query = UnderlyingAgreementMigrationUtil.constructSectionQuery(LIVE_FACILITIES_ENTRY_DATE_STMT, eligibleTargetUnitIds);
        List<FacilityCreatedDateVO> facilities = executeQuery(query, eligibleTargetUnitIds);
        return facilities.stream().collect(Collectors.groupingBy(fac -> MigrationUtil.convertLegacyToCcaBusinessId(fac.getTargetUnitId())));
    }
    
    private List<FacilityCreatedDateVO> executeQuery(String query, List<String> eligibleTargetUnitIds) {
        return migrationJdbcTemplate.query(query, new FacilityCreatedDateRowMapper(),
                eligibleTargetUnitIds.isEmpty() ? new Object[] {} : eligibleTargetUnitIds.stream().map(MigrationUtil::convertCcaToLegacyBusinessId).toArray());
    }

}
