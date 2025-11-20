package uk.gov.cca.api.migration.cca3sectorassociation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
	private DataValidator<Cca3SectorAssociationVO> validator;

	@Test
	void validate() {
		List<String> failedEntries = new ArrayList<>();
		final LocalDate umaDate = java.time.LocalDate.of(2023, 11, 28);
		final Cca3SectorAssociationVO vo = Cca3SectorAssociationVO.builder()
				.sectorAcronym("ADS_1")
				.subsectorName("")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.valueOf(0.23))
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(umaDate)
				.build();

		when(sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme(vo.getSectorAcronym(), SchemeVersion.CCA_3))
				.thenReturn(Optional.of(SectorAssociation.builder().build()));
		when(validator.validate(vo)).thenReturn(Optional.empty());

		// Invoke
		validationService.validate(List.of(vo),  failedEntries);

		// Verify
		assertThat(failedEntries).isEmpty();
		verify(sectorAssociationQueryService, times(1))
				.findSectorAssociationByAcronymAndScheme(vo.getSectorAcronym(), SchemeVersion.CCA_3);
		verify(validator, times(1)).validate(vo);
	}

	@Test
	void validate_data_error() {
		List<String> failedEntries = new ArrayList<>();
		final LocalDate umaDate = java.time.LocalDate.of(2023, 11, 28);
		final Cca3SectorAssociationVO vo = Cca3SectorAssociationVO.builder()
				.sectorAcronym("ADS_1")
				.subsectorName("")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.valueOf(0.23))
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(umaDate)
				.build();

		when(sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme(vo.getSectorAcronym(), SchemeVersion.CCA_3))
				.thenReturn(Optional.of(SectorAssociation.builder().build()));
		when(validator.validate(vo)).thenReturn(Optional.of(new BusinessViolation("section", "Data error")));

		// Invoke
		validationService.validate(List.of(vo),  failedEntries);

		// Verify
		assertThat(failedEntries).containsExactly("ADS_1| : Data error");
		verify(sectorAssociationQueryService, times(1))
				.findSectorAssociationByAcronymAndScheme(vo.getSectorAcronym(), SchemeVersion.CCA_3);
		verify(validator, times(1)).validate(vo);
	}

	@Test
	void validation_fails_SectorDoesNotExist() {
		List<String> failedEntries = new ArrayList<>();
		final LocalDate umaDate = LocalDate.of(2023, 11, 28);
		final Cca3SectorAssociationVO vo = Cca3SectorAssociationVO.builder()
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
		when(validator.validate(vo)).thenReturn(Optional.empty());

		// Invoke
		validationService.validate(List.of(vo), failedEntries);

		// Verify
		assertThat(failedEntries).containsExactly("UNKNOWN_SECTOR|ANY_SUB : Sector does not exist");
		verify(sectorAssociationQueryService, times(1))
				.findSectorAssociationByAcronymAndScheme(vo.getSectorAcronym(), SchemeVersion.CCA_3);
		verify(validator, times(1)).validate(vo);
	}

	@Test
	void validation_fails_SectorHasNoSubsectorsAndSubsectorIsProvided() {
		List<String> failedEntries = new ArrayList<>();
		final LocalDate umaDate = LocalDate.of(2023, 11, 28);
		final Cca3SectorAssociationVO vo = Cca3SectorAssociationVO.builder()
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
		when(validator.validate(vo)).thenReturn(Optional.empty());

		// Invoke
		validationService.validate(List.of(vo), failedEntries);

		// Verify
		assertThat(failedEntries).containsExactly("SEC|SUB : Subsector not valid for sector");
		verify(sectorAssociationQueryService, times(1))
				.findSectorAssociationByAcronymAndScheme(vo.getSectorAcronym(), SchemeVersion.CCA_3);
		verify(validator, times(1)).validate(vo);
	}

	@Test
	void validation_fails_SubsectorIsBlankButRequired() {
		List<String> failedEntries = new ArrayList<>();
		final LocalDate umaDate = LocalDate.of(2023, 11, 28);
		final Cca3SectorAssociationVO vo = Cca3SectorAssociationVO.builder()
				.sectorAcronym("SEC")
				.subsectorName("") // blank
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.TEN)
				.targetPeriod8Improvement(BigDecimal.TEN)
				.targetPeriod9Improvement(BigDecimal.TEN)
				.umaDate(umaDate)
				.build();

		final SectorAssociation sectorAssociation = SectorAssociation.builder()
				.subsectorAssociations(List.of(SubsectorAssociation.builder().name("SUB1").build()))
				.build();

		when(sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme("SEC", SchemeVersion.CCA_3))
				.thenReturn(Optional.of(sectorAssociation));
		when(validator.validate(vo)).thenReturn(Optional.empty());

		// Invoke
		validationService.validate(List.of(vo), failedEntries);

		// Verify
		assertThat(failedEntries).containsExactly("SEC| : Subsector cannot be blank for this sector");
		verify(sectorAssociationQueryService, times(1))
				.findSectorAssociationByAcronymAndScheme(vo.getSectorAcronym(), SchemeVersion.CCA_3);
		verify(validator, times(1)).validate(vo);
	}

	@Test
	void validation_fails_SubsectorDoesNotExistInSector() {
		List<String> failedEntries = new ArrayList<>();
		final LocalDate umaDate = LocalDate.of(2023, 11, 28);
		final Cca3SectorAssociationVO vo = Cca3SectorAssociationVO.builder()
				.sectorAcronym("SEC")
				.subsectorName("INVALID_SUB")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.TEN)
				.targetPeriod8Improvement(BigDecimal.TEN)
				.targetPeriod9Improvement(BigDecimal.TEN)
				.umaDate(umaDate)
				.build();

		final SectorAssociation sectorAssociation = SectorAssociation.builder()
				.subsectorAssociations(List.of(SubsectorAssociation.builder().name("SUB1").build()))
				.build();

		when(sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme("SEC", SchemeVersion.CCA_3))
				.thenReturn(Optional.of(sectorAssociation));
		when(validator.validate(vo)).thenReturn(Optional.empty());

		// Invoke
		validationService.validate(List.of(vo), failedEntries);

		// Verify
		assertThat(failedEntries).containsExactly("SEC|INVALID_SUB : Subsector does not exist");
		verify(sectorAssociationQueryService, times(1))
				.findSectorAssociationByAcronymAndScheme(vo.getSectorAcronym(), SchemeVersion.CCA_3);
		verify(validator, times(1)).validate(vo);
	}

	@Test
	void validation_fails_Subsector_SectorDefinition_error() {
		List<String> failedEntries = new ArrayList<>();
		final LocalDate umaDate = java.time.LocalDate.of(2023, 11, 28);
		final String sectorAcronym = "ADS_53";
		final Cca3SectorAssociationVO vo1 = Cca3SectorAssociationVO.builder()
				.sectorAcronym(sectorAcronym)
				.subsectorName("sub1")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.valueOf(0.23))
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(umaDate)
				.sectorDefinition("definition")
				.build();
		final Cca3SectorAssociationVO vo2 = Cca3SectorAssociationVO.builder()
				.sectorAcronym(sectorAcronym)
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

		when(sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme(sectorAcronym, SchemeVersion.CCA_3))
				.thenReturn(Optional.of(sectorAssociation));
		when(validator.validate(vo1)).thenReturn(Optional.empty());
		when(validator.validate(vo2)).thenReturn(Optional.empty());

		// Invoke
		validationService.validate(List.of(vo1, vo2),  failedEntries);

		// Verify
		assertThat(failedEntries).containsExactly("Different sector definitions have been declared for the same sector 'ADS_53'");
		verify(sectorAssociationQueryService, times(2))
				.findSectorAssociationByAcronymAndScheme(sectorAcronym, SchemeVersion.CCA_3);
		verify(validator, times(1)).validate(vo1);
		verify(validator, times(1)).validate(vo2);
	}

	@Test
	void validation_passes_same_SectorDefinition() {
		List<String> failedEntries = new ArrayList<>();
		final LocalDate umaDate = java.time.LocalDate.of(2023, 11, 28);
		final String sectorAcronym = "ADS_53";
		final Cca3SectorAssociationVO vo1 = Cca3SectorAssociationVO.builder()
				.sectorAcronym(sectorAcronym)
				.subsectorName("sub1")
				.measurementType(MeasurementType.ENERGY_KWH)
				.targetPeriod7Improvement(BigDecimal.ONE)
				.targetPeriod8Improvement(BigDecimal.valueOf(0.23))
				.targetPeriod9Improvement(BigDecimal.ONE)
				.umaDate(umaDate)
				.sectorDefinition("definition")
				.build();
		final Cca3SectorAssociationVO vo2 = Cca3SectorAssociationVO.builder()
				.sectorAcronym(sectorAcronym)
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

		when(sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme(sectorAcronym, SchemeVersion.CCA_3))
				.thenReturn(Optional.of(sectorAssociation));
		when(validator.validate(vo1)).thenReturn(Optional.empty());
		when(validator.validate(vo2)).thenReturn(Optional.empty());

		// Invoke
		validationService.validate(List.of(vo1, vo2),  failedEntries);

		// Verify
		assertThat(failedEntries).isEmpty();
		verify(sectorAssociationQueryService, times(2))
				.findSectorAssociationByAcronymAndScheme(sectorAcronym, SchemeVersion.CCA_3);
		verify(validator, times(1)).validate(vo1);
		verify(validator, times(1)).validate(vo2);
	}
}