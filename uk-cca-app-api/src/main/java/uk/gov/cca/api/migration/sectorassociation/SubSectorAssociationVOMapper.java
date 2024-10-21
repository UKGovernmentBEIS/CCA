package uk.gov.cca.api.migration.sectorassociation;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SubSectorAssociationVOMapper implements RowMapper<SubSectorAssociationVO> {

    @Override
    public SubSectorAssociationVO mapRow(ResultSet rs, int i) throws SQLException { 	
        
        TargetSetVO targetSetVO = TargetSetVO.builder()
                .targetType(rs.getString("target_type"))
                .throughputUnit(rs.getString("throughput_unit"))
                .energyCarbonUnit(rs.getString("energy_carbon_unit"))
                .tp1SectorCommitment(rs.getBigDecimal("tp1_sector_commitment"))
                .tp2SectorCommitment(rs.getBigDecimal("tp2_sector_commitment"))
                .tp3SectorCommitment(rs.getBigDecimal("tp3_sector_commitment"))
                .tp4SectorCommitment(rs.getBigDecimal("tp4_sector_commitment"))
                .tp5SectorCommitment(rs.getBigDecimal("tp5_sector_commitment"))
                .tp6SectorCommitment(rs.getBigDecimal("tp6_sector_commitment")).build();
        
        return SubSectorAssociationVO.builder()
            .originalSubSectorId(rs.getLong("sub_sector_pk"))
            .name(rs.getString("sub_sector_name"))
            .targetSet(targetSetVO)
            .build();
    }
}