package uk.gov.cca.api.migration.underlyingagreement.facilities;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class FacilityCreatedDateRowMapper implements RowMapper<FacilityCreatedDateVO> {

    @Override
    public FacilityCreatedDateVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        return FacilityCreatedDateVO.builder()
                .targetUnitId(rs.getString("tu_id"))
                .facilityBusinessId(rs.getString("facility_id"))
                .createdDate(rs.getTimestamp("facility_entry_date") != null
                        ? rs.getTimestamp("facility_entry_date").toLocalDateTime()
                        : null)
                .build();
    }
}
