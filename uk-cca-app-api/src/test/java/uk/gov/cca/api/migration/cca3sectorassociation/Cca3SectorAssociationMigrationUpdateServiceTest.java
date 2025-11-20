package uk.gov.cca.api.migration.cca3sectorassociation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class Cca3SectorAssociationMigrationUpdateServiceTest {

	@InjectMocks
	private Cca3SectorAssociationMigrationUpdateService updateService;

	@Mock
	private Cca3SectorSubsectorSchemeMigrationService sectorSubsectorMigrationService;

	@Test
	void updateSectorAssociationDataList() {
		final Cca3SectorAssociationVO vo1 = Cca3SectorAssociationVO.builder()
				.sectorAcronym("ADS_53")
				.subsectorName("SUBSECTOR_3")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.valueOf(70))
				.targetPeriod8Improvement(BigDecimal.valueOf(80))
				.targetPeriod9Improvement(BigDecimal.valueOf(90))
				.umaDate(LocalDate.of(2023, 11, 28))
				.build();

		final Cca3SectorAssociationVO vo2 = Cca3SectorAssociationVO.builder()
				.sectorAcronym("ADS_1")
				.measurementType(MeasurementType.ENERGY_MWH)
				.targetPeriod7Improvement(BigDecimal.valueOf(77))
				.targetPeriod8Improvement(BigDecimal.valueOf(78))
				.targetPeriod9Improvement(BigDecimal.valueOf(79))
				.umaDate(LocalDate.of(2023, 11, 25))
				.build();

		final UpdateSectorAssociationSchemeVO updateVo1 = UpdateSectorAssociationSchemeVO.builder()
				.sectorAcronym("ADS_53")
				.subsectorName("SUBSECTOR_3")
				.measurementType(MeasurementType.ENERGY_KWH)
				.improvementTargetsByPeriod(Map.of(
						"TP7 (2026)", BigDecimal.valueOf(0.7).setScale(7, RoundingMode.HALF_DOWN),
						"TP8 (2027-2028)", BigDecimal.valueOf(0.8).setScale(7, RoundingMode.HALF_DOWN),
						"TP9 (2029-2030)", BigDecimal.valueOf(0.9).setScale(7, RoundingMode.HALF_DOWN)
				))
				.umaDate(LocalDate.of(2023, 11, 28))
				.build();
		final UpdateSectorAssociationSchemeVO updateVo2 = UpdateSectorAssociationSchemeVO.builder()
				.sectorAcronym("ADS_1")
				.measurementType(MeasurementType.ENERGY_MWH)
				.improvementTargetsByPeriod(Map.of(
						"TP7 (2026)", BigDecimal.valueOf(0.77).setScale(7, RoundingMode.HALF_DOWN),
						"TP8 (2027-2028)", BigDecimal.valueOf(0.78).setScale(7, RoundingMode.HALF_DOWN),
						"TP9 (2029-2030)", BigDecimal.valueOf(0.79).setScale(7, RoundingMode.HALF_DOWN)
				))
				.umaDate(LocalDate.of(2023, 11, 25))
				.build();

		updateService.updateSectorAssociationDataList(List.of(vo1, vo2));

		verify(sectorSubsectorMigrationService, times(1))
				.migrateSectorAssociationToCca3Scheme(updateVo2);
		verify(sectorSubsectorMigrationService, times(1))
				.migrateSubSectorAssociationToCca3Scheme(updateVo1);
		verifyNoMoreInteractions(sectorSubsectorMigrationService);
	}
}