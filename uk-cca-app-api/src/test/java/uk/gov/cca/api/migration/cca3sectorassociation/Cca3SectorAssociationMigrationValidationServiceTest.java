package uk.gov.cca.api.migration.cca3sectorassociation;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3SectorAssociationMigrationValidationServiceTest {

	@InjectMocks
	private Cca3SectorAssociationMigrationValidationService validationService;

	@Mock
	private SectorAssociationQueryService sectorAssociationQueryService;

	@Mock
	private Validator validator;

	@Test
	void validate() {

		final LocalDate umaDate = java.time.LocalDate.of(2023, 11, 28);
		final Cca3SectorAssociationVO vo1 = Cca3SectorAssociationVO.builder()
				.rowNumber(2L)
				.sectorAcronym("ADS_1")
				.subsectorName("")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.valueOf(0.23))
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(umaDate)
				.build();

		List<String> failedEntries = new ArrayList<>();

		when( sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme(vo1.getSectorAcronym(), SchemeVersion.CCA_3))
				.thenReturn(Optional.of(SectorAssociation.builder().build()));

		validationService.validate(List.of(vo1),  failedEntries);

		assertTrue(failedEntries.isEmpty());
		verify(sectorAssociationQueryService, times(1))
				.findSectorAssociationByAcronymAndScheme(vo1.getSectorAcronym(), SchemeVersion.CCA_3);
		verify(validator, times(1)).validate(vo1);

	}

	@Test
	void validation_fails_SectorDoesNotExist() {
		final LocalDate umaDate = LocalDate.of(2023, 11, 28);
		Cca3SectorAssociationVO input = Cca3SectorAssociationVO.builder()
				.rowNumber(5L)
				.sectorAcronym("UNKNOWN_SECTOR")
				.subsectorName("ANY_SUB")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.ONE)
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(umaDate)
				.build();

		when(sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme("UNKNOWN_SECTOR", SchemeVersion.CCA_3))
				.thenReturn(Optional.empty());

		when(validator.validate(input)).thenReturn(Set.of());

		List<String> failedEntries = new ArrayList<>();
		validationService.validate(List.of(input), failedEntries);

		assertEquals(1, failedEntries.size());
		assertTrue(failedEntries.getFirst().contains("Invalid sector/sector does not exist"));
	}

	@Test
	void validation_fails_SectorHasNoSubsectorsAndSubsectorIsProvided() {
		final LocalDate umaDate = LocalDate.of(2023, 11, 28);
		Cca3SectorAssociationVO input = Cca3SectorAssociationVO.builder()
				.rowNumber(1L)
				.sectorAcronym("SEC")
				.subsectorName("SUB")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.TEN)
				.targetPeriod9Improvement(BigDecimal.TEN)
				.umaDate(umaDate)
				.build();

		SectorAssociation sectorAssociation = mock(SectorAssociation.class);
		when(sectorAssociation.getSubsectorAssociations()).thenReturn(List.of());

		when(sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme("SEC", SchemeVersion.CCA_3))
				.thenReturn(Optional.of(sectorAssociation));
		when(validator.validate(input)).thenReturn(Set.of());

		List<String> failedEntries = new ArrayList<>();
		validationService.validate(List.of(input), failedEntries);

		assertEquals(1, failedEntries.size());
		assertTrue(failedEntries.getFirst().contains("Subsector \"SUB\" not valid for sector"));
	}

	@Test
	void validation_fails_SubsectorIsBlankButRequired() {
		final LocalDate umaDate = LocalDate.of(2023, 11, 28);
		Cca3SectorAssociationVO input = Cca3SectorAssociationVO.builder()
				.rowNumber(2L)
				.sectorAcronym("SEC")
				.subsectorName("") // blank
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.TEN)
				.targetPeriod8Improvement(BigDecimal.TEN)
				.targetPeriod9Improvement(BigDecimal.TEN)
				.umaDate(umaDate)
				.build();

		SubsectorAssociation sub1 = mock(SubsectorAssociation.class);
		when(sub1.getName()).thenReturn("SUB1");

		SectorAssociation sectorAssociation = mock(SectorAssociation.class);
		when(sectorAssociation.getSubsectorAssociations()).thenReturn(List.of(sub1));

		when(sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme("SEC", SchemeVersion.CCA_3))
				.thenReturn(Optional.of(sectorAssociation));
		when(validator.validate(input)).thenReturn(Set.of());

		List<String> failedEntries = new ArrayList<>();
		validationService.validate(List.of(input), failedEntries);

		assertEquals(1, failedEntries.size());
		assertTrue(failedEntries.getFirst().contains("Subsector cannot be blank for this sector"));
	}

	@Test
	void validation_fails_SubsectorDoesNotExistInSector() {
		final LocalDate umaDate = LocalDate.of(2023, 11, 28);
		Cca3SectorAssociationVO input = Cca3SectorAssociationVO.builder()
				.rowNumber(3L)
				.sectorAcronym("SEC")
				.subsectorName("INVALID_SUB")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.TEN)
				.targetPeriod8Improvement(BigDecimal.TEN)
				.targetPeriod9Improvement(BigDecimal.TEN)
				.umaDate(umaDate)
				.build();

		SubsectorAssociation sub1 = mock(SubsectorAssociation.class);
		when(sub1.getName()).thenReturn("SUB1");

		SectorAssociation sectorAssociation = mock(SectorAssociation.class);
		when(sectorAssociation.getSubsectorAssociations()).thenReturn(List.of(sub1));

		when(sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme("SEC", SchemeVersion.CCA_3))
				.thenReturn(Optional.of(sectorAssociation));
		when(validator.validate(input)).thenReturn(Set.of());

		List<String> failedEntries = new ArrayList<>();
		validationService.validate(List.of(input), failedEntries);

		assertEquals(1, failedEntries.size());
		assertTrue(failedEntries.getFirst().contains("Subsector does not exist"));
	}

	@Test
	void validation_fails_Subsector_SectorDefinition_error() {

		final LocalDate umaDate = java.time.LocalDate.of(2023, 11, 28);
		final Cca3SectorAssociationVO vo1 = Cca3SectorAssociationVO.builder()
				.rowNumber(2L)
				.sectorAcronym("ADS_53")
				.subsectorName("sub1")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.valueOf(0.23))
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(umaDate)
				.sectorDefinition("definition")
				.build();
		final Cca3SectorAssociationVO vo2 = Cca3SectorAssociationVO.builder()
				.rowNumber(3L)
				.sectorAcronym("ADS_53")
				.subsectorName("sub2")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.valueOf(0.23))
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(umaDate)
				.sectorDefinition("different definition")
				.build();
		final SectorAssociation sectorAssociation = SectorAssociation.builder()
				.subsectorAssociations(List.of(
						SubsectorAssociation.builder()
								.name("sub1")
								.build(),
						SubsectorAssociation.builder()
								.name("sub2")
								.build()
				))
				.build();

		List<String> failedEntries = new ArrayList<>();

		when( sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme(vo1.getSectorAcronym(), SchemeVersion.CCA_3))
				.thenReturn(Optional.of(sectorAssociation));

		validationService.validate(List.of(vo1, vo2),  failedEntries);

		assertEquals(1, failedEntries.size());
		verify(validator, times(1)).validate(vo1);
		assertEquals(failedEntries.getFirst(),
				Cca3ErrorMessageUtil.constructSectorDefinitionError("ADS_53")
		);
	}

	@Test
	void validation_passes_same_SectorDefinition() {

		final LocalDate umaDate = java.time.LocalDate.of(2023, 11, 28);
		final Cca3SectorAssociationVO vo1 = Cca3SectorAssociationVO.builder()
				.rowNumber(2L)
				.sectorAcronym("ADS_53")
				.subsectorName("sub1")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.valueOf(0.23))
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(umaDate)
				.sectorDefinition("definition")
				.build();
		final Cca3SectorAssociationVO vo2 = Cca3SectorAssociationVO.builder()
				.rowNumber(3L)
				.sectorAcronym("ADS_53")
				.subsectorName("sub2")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.valueOf(0.23))
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(umaDate)
				.sectorDefinition("definition")
				.build();

		final SectorAssociation sectorAssociation = SectorAssociation.builder()
				.subsectorAssociations(List.of(
						SubsectorAssociation.builder()
								.name("sub1")
								.build(),
						SubsectorAssociation.builder()
								.name("sub2")
								.build()
				))
				.build();

		List<String> failedEntries = new ArrayList<>();

		when( sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme(vo1.getSectorAcronym(), SchemeVersion.CCA_3))
				.thenReturn(Optional.of(sectorAssociation));

		validationService.validate(List.of(vo1, vo2),  failedEntries);

		assertTrue(failedEntries.isEmpty());
		verify(validator, times(1)).validate(vo1);
	}
}