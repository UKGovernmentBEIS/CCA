package uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEnergyFuelDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@EnableAutoConfiguration
@EnableJpaAuditing
@ContextConfiguration(classes = PerformanceDataFacilityStatusRepository.class)
@DataJpaTest
@Import(ObjectMapper.class)
class PerformanceDataFacilityStatusRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
    private PerformanceDataFacilityStatusRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
    	
        flushAndClear();
    }

	@Test
    void updateVariationIndicatorByFacilityBusinessIdInAndTargetPeriodYear() {
        final Set<String> facilityBusinessIds = Set.of("facility1", "facility2", "facility3");
        final Year targetPeriodYear = Year.of(2026);
        
        createFacility("facility1");
        createFacility("facility2");
        createFacility("facility3");
        
        TargetPeriod targetPeriod = createTargetPeriod();
        
        PerformanceDataFacilityEntity pdfe1 = createPerformanceDataFacility(targetPeriodYear, targetPeriod);
        PerformanceDataFacilityEntity pdfe2 = createPerformanceDataFacility(targetPeriodYear.plusYears(1), targetPeriod);
        PerformanceDataFacilityEntity pdfe3 = createPerformanceDataFacility(targetPeriodYear.plusYears(2), targetPeriod);
        
        PerformanceDataFacilityStatus pdfs1 = PerformanceDataFacilityStatus.builder()
        		.facilityId(1L)
        		.targetPeriodYear(targetPeriodYear)
        		.targetPeriod(targetPeriod)
        		.lastPerformanceData(pdfe1)
        		.build();
        entityManager.persist(pdfs1);
        
        PerformanceDataFacilityStatus pdfs2 = PerformanceDataFacilityStatus.builder()
        		.facilityId(1L)
        		.targetPeriodYear(targetPeriodYear.plusYears(1))
        		.targetPeriod(targetPeriod)
        		.lastPerformanceData(pdfe2)
        		.build();
        entityManager.persist(pdfs2);
        
        PerformanceDataFacilityStatus pdfs3 = PerformanceDataFacilityStatus.builder()
        		.facilityId(100000L)
        		.targetPeriodYear(targetPeriodYear)
        		.targetPeriod(targetPeriod)
        		.lastPerformanceData(pdfe3)
        		.build();
        entityManager.persist(pdfs3);
        
        flushAndClear();

        // Invoke
        repository.updateVariationIndicatorByFacilityBusinessIdInAndTargetPeriodYear(
        		facilityBusinessIds, targetPeriodYear);

        PerformanceDataFacilityStatus updated1 =
                entityManager.find(PerformanceDataFacilityStatus.class, pdfs1.getId());
        PerformanceDataFacilityStatus updated2 =
                entityManager.find(PerformanceDataFacilityStatus.class, pdfs2.getId());
        PerformanceDataFacilityStatus updated3 =
                entityManager.find(PerformanceDataFacilityStatus.class, pdfs3.getId());
        
        // Verify
        assertThat(updated1.isVariationIndicator()).isTrue();
        assertThat(updated2.isVariationIndicator()).isFalse();
        assertThat(updated3.isVariationIndicator()).isFalse();
    }

	private PerformanceDataFacilityEntity createPerformanceDataFacility(final Year targetPeriodYear,
			TargetPeriod targetPeriod) {
		PerformanceDataFacilityEntity pdfe = PerformanceDataFacilityEntity.builder()
        		.facilityId(1L)
        		.reportVersion(10)
        		.targetPeriod(targetPeriod)
        		.targetPeriodYear(targetPeriodYear)
        		.data(PerformanceDataFacilityContainer.builder()
        				.energyFuelDetails(PerformanceDataFacilityEnergyFuelDetails.builder()
        						.atLeastSeventyPercentEnergyUsed(Boolean.FALSE)
        						.build())
        				.baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder().build())
        				.throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
        						.totalTargetVariableEnergy(BigDecimal.ONE)
        						.build())
        				.calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
        						.actualEnergyCarbon(BigDecimal.ONE)
        						.actualImprovement(BigDecimal.ONE)
        						.targetImprovement(BigDecimal.ONE)
        						.targetEnergyCarbon(BigDecimal.ONE)
        						.targetCo2Emissions(BigDecimal.ONE)
        						.energyCarbonDifference(BigDecimal.ONE)
        						.weightedConversionFactor(BigDecimal.ONE)
        						.co2EmissionsDifference(BigDecimal.ONE)
        						.actualCo2Emissions(BigDecimal.ONE)
        						.build())
        				.build())
        		.build();
        entityManager.persist(pdfe);
		return pdfe;
	}
	
	private FacilityData createFacility(String businessId) {
    	
    	FacilityAddress address = FacilityAddress.builder()
                .line1("123 Test Street")
                .city("Test City")
                .postcode("12345")
                .country("Test Country")
                .build();
        entityManager.persist(address);
        
    	FacilityData facility = FacilityData.builder()
    			.facilityBusinessId(businessId)
                .accountId(1L)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                .siteName("name")
                .address(address)
                .createdDate(LocalDateTime.now())
                .build();

        entityManager.persist(facility);

        return facility;
    }
	
	private TargetPeriod createTargetPeriod() {
        TargetPeriod targetPeriod = TargetPeriod.builder()
                .businessId(TargetPeriodType.TP7)
                .name(TargetPeriodType.TP7.name())
                .startDate(LocalDate.now())
                .endDate(LocalDate.of(2023, 11, 3))
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(TargetPeriodYear.builder()
                                .targetYear(Year.now())
                                .startDate(LocalDate.now())
                                .endDate(LocalDate.of(2023, 11, 3))
                                .reportingStartDate(LocalDate.now())
                                .build()))
                        .build())
                .buyOutStartDate(LocalDate.now())
                .buyOutPrimaryPaymentDeadline(LocalDate.now())
                .secondaryReportingStartDate(LocalDate.now())
                .schemeVersion(SchemeVersion.CCA_3)
                .build();
        entityManager.persist(targetPeriod);
        
        return targetPeriod;
    }
    
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
