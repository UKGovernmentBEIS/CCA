package uk.gov.cca.api.migration.underlyingagreement.details;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.MigrationUtil;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementMigrationContainer;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementMigrationUtil;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class UnderlyingAgreementDetailsMigrationService {
    
    private final JdbcTemplate migrationJdbcTemplate;
    
    private static final String UNA_DETAILS_STMT = 
            "  SELECT tu.[tu_id], \r\n"
            + "    [agreement_version], \r\n"
            + "    tu.[agreement_activated_date], \r\n"
            + "    ( SELECT max(vr.agreement_activated_date) \r\n"
            + "        FROM tbl_variation vr \r\n"
            + "        WHERE vr.tu_id = tu.target_unit_pk AND vr.status = 2) AS max_var_agreement_activated_date, \r\n"
            + "    tu.[subsector_name], \r\n"
            + "    case when tu.[subsector_name] IS NULL then sec.[sect_target_type] else subsec.[target_type] end as sect_target_type, \r\n"
            + "    case when tu.[subsector_name] IS NULL then sec.[energy_carbon_units] else subsec.[energy_carbon_unit] end as sect_energy_carbon_unit, \r\n"
            + "    case when tu.[subsector_name] IS NULL then sec.[throughput_units] else subsec.[throughput_unit] end as sect_throughput_unit \r\n"
            + " FROM [tbl_target_units] tu \r\n"
            + " JOIN [tbl_sectors] sec on sec.[sect_pk] = tu.[sect_id] \r\n"
            + " LEFT JOIN [tbl_sub_sectors] subsec on subsec.[sub_sector_name] = tu.[subsector_name] \r\n"
            + " WHERE tu.[tu_status] = 2 AND tu.[currently_in_cca] = 1";
    
    public UnderlyingAgreementDetailsMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
		super();
		this.migrationJdbcTemplate = migrationJdbcTemplate;
	}

	public void populate(List<String> eligibleTargetUnitIds, Map<String, UnderlyingAgreementMigrationContainer> migrationContainerMap) {
        Map<String, UnderlyingAgreementDetailsVO> unaDetails = queryDetails(eligibleTargetUnitIds);

        unaDetails.forEach((targetUnitId, details) -> {
            migrationContainerMap.get(targetUnitId).setConsolidationNumber(details.getConsolidationNumber());
            migrationContainerMap.get(targetUnitId).setActivationDate(details.getActivationDate());
            MeasurementType sectorMeasurementType = MigrationUtil.getMeasurementType(details.getSectorEnergyOrCarbonUnit());
            migrationContainerMap.get(targetUnitId).getUnderlyingAgreementContainer().setSectorMeasurementType(sectorMeasurementType);
            migrationContainerMap.get(targetUnitId).getUnderlyingAgreementContainer().setSectorThroughputUnit(details.getSectorThroughputUnit());
        });
    }

    private Map<String, UnderlyingAgreementDetailsVO> queryDetails(List<String> eligibleTargetUnitIds) {
        String query = UnderlyingAgreementMigrationUtil.constructSectionQuery(UNA_DETAILS_STMT, eligibleTargetUnitIds);
        List<UnderlyingAgreementDetailsVO> details = executeQuery(query, eligibleTargetUnitIds);
        return details.stream().collect(Collectors.toMap(dtls -> MigrationUtil.convertLegacyToCcaBusinessId(dtls.getTargetUnitId()), dtls -> dtls));
    }
    
    private List<UnderlyingAgreementDetailsVO> executeQuery(String query, List<String> eligibleTargetUnitIds) {
        return migrationJdbcTemplate.query(query, new UnderlyingAgreementDetailsVORowMapper(),
                eligibleTargetUnitIds.isEmpty() ? new Object[] {} : eligibleTargetUnitIds.stream().map(MigrationUtil::convertCcaToLegacyBusinessId).toArray());
    }
}
