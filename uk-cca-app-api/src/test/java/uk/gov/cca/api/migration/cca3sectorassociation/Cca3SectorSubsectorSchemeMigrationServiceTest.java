package uk.gov.cca.api.migration.cca3sectorassociation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.TargetCommitment;
import uk.gov.cca.api.sectorassociation.domain.TargetSet;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationSchemeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3SectorSubsectorSchemeMigrationServiceTest {

	@InjectMocks
	private Cca3SectorSubsectorSchemeMigrationService service;

	@Mock
	private SectorAssociationSchemeRepository sectorAssociationSchemeRepository;

	@Mock
	private SubsectorAssociationSchemeRepository subsectorAssociationSchemeRepository;


	@Test
	void migrateSectorAssociationToCca3Scheme() {
		LocalDate umaDate = LocalDate.of(2024, 12, 31);

		Map<String, BigDecimal> improvementTargetsByPeriod = new HashMap<>();
		improvementTargetsByPeriod.put("TP7 (2026)", BigDecimal.valueOf(0.7));
		improvementTargetsByPeriod.put("TP8 (2027-2028)", BigDecimal.valueOf(0.8));
		improvementTargetsByPeriod.put("TP9 (2029-2030)", BigDecimal.valueOf(0.9));
		UpdateSectorAssociationSchemeVO input = UpdateSectorAssociationSchemeVO.builder()
				.sectorAcronym("SEC")
				.subsectorName("")
				.measurementType(MeasurementType.ENERGY_KWH)
				.improvementTargetsByPeriod(improvementTargetsByPeriod)
				.umaDate(umaDate)
				.build();

		TargetCommitment tp7 = new TargetCommitment();
		tp7.setTargetPeriod("TP7 (2026)");

		TargetCommitment tp8 = new TargetCommitment();
		tp8.setTargetPeriod("TP8 (2027-2028)");

		TargetCommitment tp9 = new TargetCommitment();
		tp9.setTargetPeriod("TP9 (2029-2030)");

		TargetSet targetSet = new TargetSet();
		targetSet.setTargetCommitments(List.of(tp7, tp8, tp9));

		SectorAssociationScheme scheme = new SectorAssociationScheme();
		scheme.setTargetSet(targetSet);

		when(sectorAssociationSchemeRepository.findBySchemeVersionAndSectorAssociation_Acronym(SchemeVersion.CCA_3, "SEC"))
				.thenReturn(scheme);

		service.migrateSectorAssociationToCca3Scheme(input);

		verify(sectorAssociationSchemeRepository, times(1))
				.findBySchemeVersionAndSectorAssociation_Acronym(SchemeVersion.CCA_3, "SEC");
		assertEquals(umaDate, scheme.getUmaDate());
		assertEquals(MeasurementType.ENERGY_KWH.getUnit(), scheme.getTargetSet().getEnergyOrCarbonUnit());
		assertEquals(BigDecimal.valueOf(0.7), tp7.getTargetImprovement());
		assertEquals(BigDecimal.valueOf(0.8), tp8.getTargetImprovement());
		assertEquals(BigDecimal.valueOf(0.9), tp9.getTargetImprovement());
	}

	@Test
	void migrateSubSectorAssociationToCca3Scheme() {
		LocalDate umaDate = LocalDate.of(2024, 12, 31);
		Map<String, BigDecimal> improvementTargetsByPeriod = new HashMap<>();
		improvementTargetsByPeriod.put("TP7 (2026)", BigDecimal.valueOf(0.7));
		improvementTargetsByPeriod.put("TP8 (2027-2028)", BigDecimal.valueOf(0.8));
		improvementTargetsByPeriod.put("TP9 (2029-2030)", BigDecimal.valueOf(0.9));
		UpdateSectorAssociationSchemeVO input = UpdateSectorAssociationSchemeVO.builder()
				.sectorAcronym("SECTOR")
				.subsectorName("SUBSECTOR")
				.measurementType(MeasurementType.ENERGY_KWH)
				.improvementTargetsByPeriod(improvementTargetsByPeriod)
				.umaDate(umaDate)
				.build();

		TargetCommitment tp7 = new TargetCommitment();
		tp7.setTargetPeriod("TP7 (2026)");

		TargetCommitment tp8 = new TargetCommitment();
		tp8.setTargetPeriod("TP8 (2027-2028)");

		TargetCommitment tp9 = new TargetCommitment();
		tp9.setTargetPeriod("TP9 (2029-2030)");

		TargetSet targetSet = new TargetSet();
		targetSet.setTargetCommitments(List.of(tp7, tp8, tp9));

		SubsectorAssociationScheme scheme = new SubsectorAssociationScheme();
		scheme.setTargetSet(targetSet);

		when(subsectorAssociationSchemeRepository
				.findBySchemeVersionAndSubsectorAssociation_NameAndSubsectorAssociation_SectorAssociation_Acronym(
						SchemeVersion.CCA_3, "SUBSECTOR", "SECTOR"
				)
		)
				.thenReturn(scheme);

		service.migrateSubSectorAssociationToCca3Scheme(input);

		verify(subsectorAssociationSchemeRepository)
				.findBySchemeVersionAndSubsectorAssociation_NameAndSubsectorAssociation_SectorAssociation_Acronym(
				SchemeVersion.CCA_3, "SUBSECTOR", "SECTOR");
		verify(sectorAssociationSchemeRepository, times(1))
				.updateUmaDate(SchemeVersion.CCA_3, input.getSectorAcronym(), umaDate);
		assertEquals(MeasurementType.ENERGY_KWH.getUnit(), scheme.getTargetSet().getEnergyOrCarbonUnit());
		assertEquals(BigDecimal.valueOf(0.7), tp7.getTargetImprovement());
		assertEquals(BigDecimal.valueOf(0.8), tp8.getTargetImprovement());
		assertEquals(BigDecimal.valueOf(0.9), tp9.getTargetImprovement());
	}
}