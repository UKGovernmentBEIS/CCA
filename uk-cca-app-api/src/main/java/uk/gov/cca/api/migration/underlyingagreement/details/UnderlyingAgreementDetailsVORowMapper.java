package uk.gov.cca.api.migration.underlyingagreement.details;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class UnderlyingAgreementDetailsVORowMapper implements RowMapper<UnderlyingAgreementDetailsVO> {

    @Override
    public UnderlyingAgreementDetailsVO mapRow(ResultSet rs, int i) throws SQLException {  
        
        return UnderlyingAgreementDetailsVO.builder()
                .targetUnitId(rs.getString("tu_id"))
                .consolidationNumber(rs.getInt("agreement_version"))
                .activationDate(rs.getTimestamp("max_var_agreement_activated_date") != null
                        ? rs.getTimestamp("max_var_agreement_activated_date").toLocalDateTime()
                        : rs.getTimestamp("agreement_activated_date").toLocalDateTime())
                .sectorEnergyOrCarbonUnit(rs.getString("sect_energy_carbon_unit"))
                .sectorThroughputUnit(rs.getString("sect_throughput_unit"))
                .build();
    }
}