package uk.gov.cca.api.migration.underlyingagreement.baselinetargets;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class TargetPeriod6DetailsRowMapper implements RowMapper<TargetPeriod6DetailsVO>{

    @Override
    public TargetPeriod6DetailsVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        return TargetPeriod6DetailsVO.builder()
                .targetUnitId(rs.getString("tu_id"))
                .measurementType(rs.getString("tp_energy_carbon_unit"))
                .agreementCompositionType(rs.getString("tp_target_type"))
                .throughputUnit(rs.getString("tp_throughput_unit"))
                .estimatedData(rs.getBoolean("tp_estimated_data"))
                .baselineDate(rs.getDate("tp_by_start") != null ? rs.getDate("tp_by_start").toLocalDate() : null)
                .usedReportingMechanism(rs.getBoolean("tp_srm"))
                .energy(rs.getBigDecimal("tp_by_energy_carbon"))
                .throughput(rs.getBigDecimal("tp_by_throughput"))
                .energyCarbonFactor(rs.getBigDecimal("tp_by_cef"))
                .improvement(rs.getBigDecimal("tp_target_percent"))
                .target(rs.getBigDecimal("tp_target_value"))
                .sectorAgreementCompositionType(rs.getString("sect_target_type"))
                .sectorMeasurementType(rs.getString("sect_energy_carbon_unit"))
                .sectorThroughputUnit(rs.getString("sect_throughput_unit"))
                .build();
    }
}
