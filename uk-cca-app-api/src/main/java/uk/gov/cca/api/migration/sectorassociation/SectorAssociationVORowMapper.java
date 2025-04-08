package uk.gov.cca.api.migration.sectorassociation;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SectorAssociationVORowMapper implements RowMapper<SectorAssociationVO> {

    @Override
    public SectorAssociationVO mapRow(ResultSet rs, int i) throws SQLException {   	
        
        TargetSetVO targetSetVO = TargetSetVO.builder()
                .targetType(rs.getString("sect_target_type"))
                .throughputUnit(rs.getString("throughput_units"))
                .energyCarbonUnit(rs.getString("energy_carbon_units"))
                .tp1SectorCommitment(rs.getBigDecimal("tp1_sector_commitment"))
                .tp2SectorCommitment(rs.getBigDecimal("tp2_sector_commitment"))
                .tp3SectorCommitment(rs.getBigDecimal("tp3_sector_commitment"))
                .tp4SectorCommitment(rs.getBigDecimal("tp4_sector_commitment"))
                .tp5SectorCommitment(rs.getBigDecimal("tp5_sector_commitment"))
                .tp6SectorCommitment(rs.getBigDecimal("tp6_sector_commitment")).build();
        
        return SectorAssociationVO.builder()
        	.originalSectorId(rs.getLong("sect_pk"))
            .acronym(rs.getString("sect_id"))
            .commonName(rs.getString("short_name"))
            .legalName(rs.getString("legal_name"))
            .energyIntensiveOrEPR(rs.getString("ei_epr"))
            .line1(rs.getString("address_line1"))
            .line2(rs.getString("address_line2"))
            .city(rs.getString("address_city"))
            .county(rs.getString("address_county"))
            .postcode(rs.getString("address_postcode"))
            .sectorContactTitle(rs.getString("contact_title"))
            .sectorContactFirstName(rs.getString("contact_name"))
            .sectorContactLastName(rs.getString("contact_surname"))
            .sectorContactAddressLine1(rs.getString("contact_address_line1"))
            .sectorContactAddressLine2(rs.getString("contact_address_line2"))
            .sectorContactAddressCity(rs.getString("contact_address_city"))
            .sectorContactAddressCounty(rs.getString("contact_address_county"))
            .sectorContactAddressPostcode(rs.getString("contact_address_postcode"))
            .sectorContactPhoneNumber(rs.getString("contact_telephone"))
            .sectorContactEmail(rs.getString("ac_email"))
                .umaDate(rs.getTimestamp("uma_date") != null
                        ? rs.getTimestamp("uma_date").toLocalDateTime().toLocalDate()
                        : null)
            .sectorDefinition(rs.getString("sector_definition"))
            .targetSet(targetSetVO)
            .subsectorsCounter(rs.getInt("subsectors_cnt"))
            .build();
    }
}