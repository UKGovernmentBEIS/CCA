package uk.gov.cca.api.migration.account;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class TargetUnitAccountVORowMapper implements RowMapper<TargetUnitAccountVO>{

    @Override
    public TargetUnitAccountVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        TargetUnitAccountContactVO responsiblePerson = TargetUnitAccountContactVO.builder()
                .firstName(rs.getString("rp_forename"))
                .lastName(rs.getString("rp_surname"))
                .email(rs.getString("rp_email"))
                .role(rs.getString("rp_role"))
                .addressLine1(rs.getString("rp_add_line1"))
                .addressLine2(rs.getString("rp_add_line2"))
                .city(rs.getString("rp_add_city"))
                .county(rs.getString("rp_add_county"))
                .postcode(rs.getString("rp_add_postcode"))
                .country(rs.getLong("rp_add_country"))
                .build();
        
        TargetUnitAccountContactVO administrativeContact = TargetUnitAccountContactVO.builder()
                .firstName(rs.getString("ac_forename"))
                .lastName(rs.getString("ac_surname"))
                .email(rs.getString("ac_email"))
                .role(rs.getString("ac_role"))
                .phoneNumber(rs.getString("ac_telephone"))
                .addressLine1(rs.getString("ac_add_line1"))
                .addressLine2(rs.getString("ac_add_line2"))
                .city(rs.getString("ac_add_city"))
                .county(rs.getString("ac_add_county"))
                .postcode(rs.getString("ac_add_postcode"))
                .country(rs.getLong("ac_add_country"))
                .build();
        
        return TargetUnitAccountVO.builder()
                .originalTuPk(rs.getLong("target_unit_pk"))
                .sectorAcronym(rs.getString("sect_id"))
                .subsectorName(rs.getString("sub_sector_name"))
                .targetUnitId(rs.getString("tu_id"))
                .operatorName(rs.getString("operator_name"))
                .companyRegistrationNumber(rs.getString("company_registration_number"))
                .sicCode(rs.getLong("sic_code") != 0L ? String.valueOf(rs.getLong("sic_code")) : null)
                .addressLine1(rs.getString("tu_add_line1"))
                .addressLine2(rs.getString("tu_add_line2"))
                .city(rs.getString("tu_add_city"))
                .county(rs.getString("tu_add_county"))
                .postcode(rs.getString("tu_postcode"))
                .country(rs.getLong("tu_country"))
                .responsiblePerson(responsiblePerson)
                .administrativeContact(administrativeContact)
                .operatorType(rs.getString("operator_type"))
                .financiallyIndependent(rs.getBoolean("financially_independent"))
                .build();
                        
    }

}
