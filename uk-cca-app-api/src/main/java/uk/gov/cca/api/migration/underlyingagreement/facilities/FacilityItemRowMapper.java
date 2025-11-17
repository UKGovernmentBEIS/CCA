package uk.gov.cca.api.migration.underlyingagreement.facilities;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class FacilityItemRowMapper implements RowMapper<FacilityItemVO> {

    @Override
    public FacilityItemVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        AccountAddressVO facilityAddressVO = AccountAddressVO.builder()
                .line1(rs.getString("facility_add_line1"))
                .line2(rs.getString("facility_add_line2"))
                .city(rs.getString("facility_add_city"))
                .county(rs.getString("facility_add_county"))
                .postcode(rs.getString("facility_add_postcode"))
                .country(rs.getLong("facility_add_country"))
                .build();
        
        AccountAddressVO facilityContactAddressVO = AccountAddressVO.builder()
                .line1(rs.getString("contact_add_line1"))
                .line2(rs.getString("contact_add_line2"))
                .city(rs.getString("contact_add_city"))
                .county(rs.getString("contact_add_county"))
                .postcode(rs.getString("contact_add_postcode"))
                .country(rs.getLong("contact_add_country"))
                .build();
        
        return FacilityItemVO.builder()
                .businessId(rs.getString("tu_id"))
                .facilityBusinessId(rs.getString("facility_id"))
                .createdDate(rs.getTimestamp("facility_entry_date") != null
                        ? rs.getTimestamp("facility_entry_date").toLocalDateTime()
                        : null)
                .name(rs.getString("facility_name"))
                .uketsId(rs.getString("uk_ets_ids"))
                .applicationReason(rs.getString("fef_application_reason"))
                .previousFacilityId(rs.getString("previous_fac_id"))
                .facilityAddress(facilityAddressVO)
                .email(rs.getString("contact_email"))
                .firstName(rs.getString("contact_forename"))
                .lastName(rs.getString("contact_surname"))
                .jobTitle(rs.getString("contact_role"))
                .address(facilityContactAddressVO)
                .phoneNumber(rs.getString("contact_telephone"))
                .adjacentFacilityId(rs.getString("connected_facility"))
                .agreementType(rs.getString("agreement_type"))
                .erpAuthorisationExists(rs.getBoolean("epr_held"))
                .authorisationNumber(rs.getString("epr_authorisations"))
                .regulatorName(rs.getString("epr_regulator"))
                .energyConsumed(rs.getBigDecimal("installation_energy"))
                .energyConsumedProvision(rs.getBigDecimal("3_ 7ths_provision"))
                .build();
    }
}
