package uk.gov.cca.api.migration.cca3sectorassociation;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import uk.gov.netz.api.common.config.MapperConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface Cca3SectorAssociationMigrationMapper {

	UpdateSectorAssociationSchemeVO toUpdateSectorAssociationSchemeDTO(Cca3SectorAssociationVO vo);

	@AfterMapping
	default void setTargetPeriodImprovements(@MappingTarget UpdateSectorAssociationSchemeVO updateSectorAssociationSchemeVO, Cca3SectorAssociationVO vo) {
		Map<String, BigDecimal> targetPeriodImprovementMap = Map.of(
				"TP7 (2026)", vo.getTargetPeriod7Improvement().divide(new BigDecimal("100"), 7, RoundingMode.HALF_DOWN),
				"TP8 (2027-2028)", vo.getTargetPeriod8Improvement().divide(new BigDecimal("100"), 7, RoundingMode.HALF_DOWN),
				"TP9 (2029-2030)", vo.getTargetPeriod9Improvement().divide(new BigDecimal("100"), 7, RoundingMode.HALF_DOWN)
		);

		updateSectorAssociationSchemeVO.setImprovementTargetsByPeriod(targetPeriodImprovementMap);
	}
}
