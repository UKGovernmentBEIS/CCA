package uk.gov.cca.api.migration.cca3sectorassociation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3SectorAssociationMigrationUpdateServiceTest {

	@InjectMocks
	private Cca3SectorAssociationMigrationUpdateService updateService;

	@Mock
	private Cca3SectorAssociationMigrationMapper mapper;

	@Mock
	private Cca3SectorSubsectorSchemeMigrationService sectorSubsectorMigrationService;

	@Test
	void updateSectorAssociationDataList() {

		final LocalDate umaDate = LocalDate.of(2023, 11, 28);
		final Cca3SectorAssociationVO vo1 = Cca3SectorAssociationVO.builder()
				.rowNumber(2L)
				.sectorAcronym("ADS_53")
				.subsectorName("SUBSECTOR_3")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.valueOf(0.23))
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(umaDate)
				.build();

		final Cca3SectorAssociationVO vo2 = Cca3SectorAssociationVO.builder()
				.rowNumber(3L)
				.sectorAcronym("ADS_1")
				.subsectorName("")
				.measurementType(MeasurementType.ENERGY_MWH)
				.targetPeriod7Improvement(BigDecimal.ZERO)
				.targetPeriod8Improvement(BigDecimal.ZERO)
				.targetPeriod9Improvement(BigDecimal.ZERO)
				.umaDate(LocalDate.of(2023, 11, 25))
				.build();

		Map<String, BigDecimal> improvementTargetsByPeriod1 = new HashMap<>();
		improvementTargetsByPeriod1.put("TP7 (2026)", BigDecimal.ONE);
		improvementTargetsByPeriod1.put("TP8 (2027-2028)", BigDecimal.valueOf(0.23));
		improvementTargetsByPeriod1.put("TP9 (2029-2030)", BigDecimal.ONE);
		final UpdateSectorAssociationSchemeVO updateVo1 = UpdateSectorAssociationSchemeVO.builder()
				.rowNumber(2L)
				.sectorAcronym("ADS_53")
				.subsectorName("SUBSECTOR_3")
				.measurementType(MeasurementType.ENERGY_KWH)
				.improvementTargetsByPeriod(improvementTargetsByPeriod1)
				.umaDate(umaDate)
				.build();
		Map<String, BigDecimal> improvementTargetsByPeriod2 = new HashMap<>();
		improvementTargetsByPeriod2.put("TP7 (2026)", BigDecimal.ZERO);
		improvementTargetsByPeriod2.put("TP8 (2027-2028)", BigDecimal.ZERO);
		improvementTargetsByPeriod2.put("TP9 (2029-2030)", BigDecimal.ZERO);
		final UpdateSectorAssociationSchemeVO updateVo2 = UpdateSectorAssociationSchemeVO.builder()
				.rowNumber(3L)
				.sectorAcronym("ADS_1")
				.subsectorName("")
				.measurementType(MeasurementType.ENERGY_MWH)
				.improvementTargetsByPeriod(improvementTargetsByPeriod2)
				.umaDate(LocalDate.of(2023, 11, 25))
				.build();

		when(mapper.toUpdateSectorAssociationSchemeDTO(vo1))
				.thenReturn(updateVo1);
		when(mapper.toUpdateSectorAssociationSchemeDTO(vo2))
				.thenReturn(updateVo2);

		updateService.updateSectorAssociationDataList(List.of(vo1, vo2));

		verify(sectorSubsectorMigrationService, times(1))
				.migrateSectorAssociationToCca3Scheme(updateVo2);
		verify(sectorSubsectorMigrationService, times(1))
				.migrateSubSectorAssociationToCca3Scheme(updateVo1);
	}
}