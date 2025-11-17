package uk.gov.cca.api.migration.cca3sectorassociation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3SectorAssociationMigrationServiceTest {

	@InjectMocks
	private Cca3SectorAssociationMigrationService migrationService;

	@Mock
	private Cca3SectorAssociationMigrationValidationService validationService;

	@Mock
	private Cca3SectorAssociationMigrationMapper mapper;

	@Mock
	private Cca3SectorAssociationMigrationUpdateService updateService;

	@Test
	void migrate() {
		final String input = "2|ADS_53|SUBSECTOR_3|Energy (kWh)|100|23|100|28/11/2023;" +
				"3|ADS_1||Energy (MWh)|0|0|0|28/11/2023;";
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

		List<String> failedEntries = new ArrayList<>();

		when(mapper.toSectorAssociationVOList(input, failedEntries))
				.thenReturn(List.of(vo1, vo2));

		List<String> result = migrationService.migrate(input);

		verify(validationService, times(1))
				.validate(List.of(vo1, vo2), failedEntries);
		verify(updateService, times(1))
				.updateSectorAssociationDataList(List.of(vo1, vo2));
		assertEquals(List.of(), result);
	}

	@Test
	void migrate_input_empty_fails() {
		final String input = "";

		List<String> result = migrationService.migrate(input);

		assertEquals(1, result.size());
		assertEquals("Please insert details for at least one sector association", result.getFirst());
	}

	@Test
	void migrate_throw_error() {
		final String input = "input";
		final Cca3SectorAssociationVO vo1 = Cca3SectorAssociationVO.builder()
				.rowNumber(2L)
				.sectorAcronym("ADS_53")
				.subsectorName("SUBSECTOR_3")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.valueOf(0.23))
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(LocalDate.of(2023, 11, 28))
				.build();
		List<Cca3SectorAssociationVO> voList = List.of(vo1);

		when(mapper.toSectorAssociationVOList(input, new ArrayList<>()))
				.thenReturn(voList);
		doThrow(new RuntimeException("Error exception"))
				.when(updateService).updateSectorAssociationDataList(voList);

		List<String> result = migrationService.migrate(input);

		verify(updateService, times(1))
				.updateSectorAssociationDataList(voList);
		assertEquals(1, result.size());
		assertEquals("Error exception", result.getFirst());

	}

	@Test
	void getResource() {

		String result = migrationService.getResource();
		assertEquals("cca3-sector-associations", result);
	}
}