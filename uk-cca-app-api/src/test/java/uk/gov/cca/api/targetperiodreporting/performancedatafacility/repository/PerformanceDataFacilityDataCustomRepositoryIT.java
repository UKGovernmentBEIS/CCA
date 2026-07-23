package uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
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
import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaAccountContactType;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEnergyFuelDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@EnableAutoConfiguration
@EnableJpaAuditing
@ContextConfiguration(classes = PerformanceDataFacilityDataCustomRepository.class)
@DataJpaTest
@Import(ObjectMapper.class)
class PerformanceDataFacilityDataCustomRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
    private PerformanceDataFacilityDataCustomRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
    	
    	TargetPeriod targetPeriod = createTP7();
    	TargetPeriod targetPeriod8 = createTP8();
    	
    	TargetUnitAccount tu1 = createAccount(1L, "tuId", TargetUnitAccountStatus.LIVE, LocalDate.of(2026, 12, 31), null);
    	TargetUnitAccount tu2 = createAccount(2L, "other", TargetUnitAccountStatus.LIVE, LocalDate.of(2024, 12, 31), null);
        FacilityData facility1 = createFacility("FAC1", tu1.getId(), tu1.getAcceptedDate());
        FacilityData facility2 = createFacility("FAC2", tu2.getId(), tu2.getAcceptedDate());
        
        PerformanceDataFacilityEntity data1 = createPerformanceDataFacility(Year.of(2026), targetPeriod, facility1.getId());
        createPerformanceDataFacilityStatus(Year.of(2026), targetPeriod, data1, facility1.getId());
        PerformanceDataFacilityEntity data2 = createPerformanceDataFacility(Year.of(2026), targetPeriod, facility2.getId());
        createPerformanceDataFacilityStatus(Year.of(2026), targetPeriod, data2, facility2.getId());
        
        PerformanceDataFacilityEntity data3 = createPerformanceDataFacilityInterim(Year.of(2027), targetPeriod8, facility1.getId());
        createPerformanceDataFacilityStatus(Year.of(2027), targetPeriod8, data3, facility1.getId());
        PerformanceDataFacilityEntity data4 = createPerformanceDataFacilityInterim(Year.of(2027), targetPeriod8, facility2.getId());
        createPerformanceDataFacilityStatus(Year.of(2027), targetPeriod8, data4, facility2.getId());

        flushAndClear();
    }

    @Test
    void getSectorFacilityPerformanceDataReportListSubmittedBySearchCriteria() {
        final Long sectorAssociationId = 1L;
        final TargetPeriodYear targetPeriodYear = getTargetPeriodYear(TargetPeriodType.TP7, Year.of(2026));
        final SectorFacilityPerformanceDataReportSearchCriteria criteria =
        		SectorFacilityPerformanceDataReportSearchCriteria.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .targetPeriodReportType(PerformanceDataReportType.FINAL)
                        .paging(PagingRequest.builder().pageNumber(0).pageSize(10).build())
                        .build();
        // Invoke
        SectorFacilityPerformanceDataReportListDTO result = repository
        		.getSectorFacilityPerformanceDataReportListSubmittedBySearchCriteria(sectorAssociationId, criteria, targetPeriodYear);

        // Verify
        assertThat(result.getTotal()).isEqualTo(2L);
        assertThat(result.getPerformanceDataReportItems())
                .extracting(SectorFacilityPerformanceDataReportItemDTO::getFacilityBusinessId)
                .containsExactlyInAnyOrder("FAC1", "FAC2");
        
        SectorFacilityPerformanceDataReportItemDTO dto = result.getPerformanceDataReportItems().get(0);

        assertThat(dto.getSiteName()).isEqualTo("name");
        assertThat(dto.getReportVersion()).isEqualTo(10);
        assertThat(dto.getSubmissionDate()).isNotNull();
        assertThat(dto.getActualImprovement()).isEqualByComparingTo("0.99999");
        assertThat(dto.getActualEnergyCarbon()).isEqualByComparingTo("1.99999");
        assertThat(dto.getEnergyCarbonDifference()).isEqualByComparingTo("10.15");
        assertThat(dto.getCo2EmissionsDifference()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(dto.getSurplusGained()).isNotNull();
        assertThat(dto.getBuyOutRequired()).isNotNull();
    }
    
    @Test
    void getSectorFacilityPerformanceDataReportListSubmittedBySearchCriteria_withCriteria() {
        final Long sectorAssociationId = 1L;
        final TargetPeriodYear targetPeriodYear = getTargetPeriodYear(TargetPeriodType.TP7, Year.of(2026));
        final SectorFacilityPerformanceDataReportSearchCriteria criteria =
        		SectorFacilityPerformanceDataReportSearchCriteria.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .targetPeriodReportType(PerformanceDataReportType.FINAL)
                        .facilityOrTargetUnitAccountBusinessId("tuId")
                        .subType(PerformanceDataSubmissionType.PRIMARY)
                        .paging(PagingRequest.builder().pageNumber(0).pageSize(10).build())
                        .build();
        // Invoke
        SectorFacilityPerformanceDataReportListDTO result = repository
        		.getSectorFacilityPerformanceDataReportListSubmittedBySearchCriteria(sectorAssociationId, criteria, targetPeriodYear);

        // Verify
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getPerformanceDataReportItems())
                .extracting(SectorFacilityPerformanceDataReportItemDTO::getFacilityBusinessId)
                .containsOnly("FAC1");
    }
    
    @Test
    void getSectorFacilityPerformanceDataReportListOutstandingBySearchCriteria() {
    	TargetUnitAccount otherAccount = createAccount(100L, "otherId", TargetUnitAccountStatus.LIVE, LocalDate.of(2023, 11, 3), null);
    	createFacility("FAC3", otherAccount.getId(), otherAccount.getAcceptedDate());
    	
        final Long sectorAssociationId = 1L;
        final TargetPeriodYear targetPeriodYear = getTargetPeriodYear(TargetPeriodType.TP7, Year.of(2026));
        final SectorFacilityPerformanceDataReportSearchCriteria criteria =
        		SectorFacilityPerformanceDataReportSearchCriteria.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .targetPeriodReportType(PerformanceDataReportType.FINAL)
                        .paging(PagingRequest.builder().pageNumber(0).pageSize(10).build())
                        .build();
        // Invoke
        SectorFacilityPerformanceDataReportListDTO result = repository
        		.getSectorFacilityPerformanceDataReportListOutstandingBySearchCriteria(sectorAssociationId, criteria, targetPeriodYear);

        // Verify
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getPerformanceDataReportItems())
                .extracting(SectorFacilityPerformanceDataReportItemDTO::getFacilityBusinessId)
                .containsOnly("FAC3");
        
        SectorFacilityPerformanceDataReportItemDTO dto = result.getPerformanceDataReportItems().get(0);

        assertThat(dto.getSiteName()).isEqualTo("name");
        assertThat(dto.getReportVersion()).isZero();
        assertThat(dto.getSubmissionDate()).isNull();
        assertThat(dto.getActualImprovement()).isNull();
    }

    @Test
    void getSectorFacilityPerformanceDataReportListOfSubmittedBySearchCriteria_Interim() {    	
        final Long sectorAssociationId = 1L;
        final TargetPeriodYear targetPeriodYear = getTargetPeriodYear(TargetPeriodType.TP8, Year.of(2027));
        final SectorFacilityPerformanceDataReportSearchCriteria criteria =
        		SectorFacilityPerformanceDataReportSearchCriteria.builder()
                        .targetPeriodType(TargetPeriodType.TP8)
                        .targetPeriodReportType(PerformanceDataReportType.INTERIM)
                        .reportStatus(PerformanceDataFacilityTargetPeriodResultType.SUBMITTED)
                        .paging(PagingRequest.builder().pageNumber(0).pageSize(10).build())
                        .build();
        
        // Invoke
        SectorFacilityPerformanceDataReportListDTO result = repository
        		.getSectorFacilityPerformanceDataReportListSubmittedBySearchCriteria(sectorAssociationId, criteria, targetPeriodYear);


        // Verify
        assertThat(result.getTotal()).isEqualTo(2L);
        assertThat(result.getPerformanceDataReportItems())
                .extracting(SectorFacilityPerformanceDataReportItemDTO::getFacilityBusinessId)
                .containsExactlyInAnyOrder("FAC1", "FAC2");
        
        SectorFacilityPerformanceDataReportItemDTO dto = result.getPerformanceDataReportItems().get(0);

        assertThat(dto.getSiteName()).isEqualTo("name");
        assertThat(dto.getReportVersion()).isEqualTo(1);
        assertThat(dto.getSubmissionDate()).isNotNull();
        assertThat(dto.getActualImprovement()).isEqualByComparingTo("0.99999");
        assertThat(dto.getActualEnergyCarbon()).isEqualByComparingTo("1.99999");
        assertThat(dto.getEnergyCarbonDifference()).isEqualByComparingTo("10.15");
        assertThat(dto.getCo2EmissionsDifference()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void getSectorFacilityPerformanceDataReportListOfOutstandingBySearchCriteria_Interim() {
    	TargetUnitAccount otherAccount2 = createAccount(555L, "otherId555", TargetUnitAccountStatus.LIVE, LocalDate.of(2027, 11, 3), null);
    	createFacility("FAC10", otherAccount2.getId(), otherAccount2.getAcceptedDate());
    	
        final Long sectorAssociationId = 1L;
        final TargetPeriodYear targetPeriodYear = getTargetPeriodYear(TargetPeriodType.TP8, Year.of(2027));
        final SectorFacilityPerformanceDataReportSearchCriteria criteria =
        		SectorFacilityPerformanceDataReportSearchCriteria.builder()
                        .targetPeriodType(TargetPeriodType.TP8)
                        .targetPeriodReportType(PerformanceDataReportType.INTERIM)
                        .reportStatus(PerformanceDataFacilityTargetPeriodResultType.OUTSTANDING)
                        .paging(PagingRequest.builder().pageNumber(0).pageSize(10).build())
                        .build();
        
        // Invoke
        SectorFacilityPerformanceDataReportListDTO result = repository
        		.getSectorFacilityPerformanceDataReportListOutstandingBySearchCriteria(sectorAssociationId, criteria, targetPeriodYear);


        // Verify
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getPerformanceDataReportItems())
                .extracting(SectorFacilityPerformanceDataReportItemDTO::getFacilityBusinessId)
                .containsExactlyInAnyOrder("FAC10");
        
        SectorFacilityPerformanceDataReportItemDTO dto = result.getPerformanceDataReportItems().get(0);

        assertThat(dto.getSurplusGained()).isNull();
        assertThat(dto.getBuyOutRequired()).isNull();
    }

    private TargetPeriod createTP7() {
        TargetPeriod tp7 = getTP7();
        entityManager.persist(tp7);
        return tp7;
    }

	private TargetPeriod getTP7() {
		return TargetPeriod.builder()
                .businessId(TargetPeriodType.TP7)
                .name(TargetPeriodType.TP7.name())
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(TargetPeriodYear.builder()
                                .targetYear(Year.of(2026))
                                .startDate(LocalDate.of(2026, 1, 1))
                                .endDate(LocalDate.of(2026, 12, 31))
                                .reportingStartDate(LocalDate.of(2027, 1, 1))
                                .build()))
                        .build())
                .buyOutStartDate(LocalDate.now())
                .buyOutPrimaryPaymentDeadline(LocalDate.now())
                .secondaryReportingStartDate(LocalDate.now())
                .schemeVersion(SchemeVersion.CCA_3)
                .build();
	}

    private TargetPeriod createTP8() {
        TargetPeriod tp8 = getTP8();
        entityManager.persist(tp8);
        return tp8;
    }

	private TargetPeriodYear getTargetPeriodYear(TargetPeriodType targetPeriodType, Year targetYear) {
		TargetPeriod targetPeriod = getTP7();
		if(TargetPeriodType.TP8.equals(targetPeriodType)) {
			targetPeriod = getTP8();
		}
		return targetPeriod.getTargetPeriodYearsContainer().getTargetPeriodYear(targetYear).get();
	}

	private TargetPeriod getTP8() {
		return TargetPeriod.builder()
                .businessId(TargetPeriodType.TP8)
                .name(TargetPeriodType.TP8.name())
                .startDate(LocalDate.of(2027, 1, 1))
                .endDate(LocalDate.of(2028, 12, 31))
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(TargetPeriodYear.builder()
                                .targetYear(Year.of(2027))
                                .startDate(LocalDate.of(2027, 1, 1))
                                .endDate(LocalDate.of(2027, 12, 31))
                                .reportingStartDate(LocalDate.of(2028, 1, 1))
                                .reportingEndDate(LocalDate.of(2028, 12, 31))
                                .build(),
                                TargetPeriodYear.builder()
                                .targetYear(Year.of(2028))
                                .startDate(LocalDate.of(2028, 1, 1))
                                .endDate(LocalDate.of(2028, 12, 31))
                                .reportingStartDate(LocalDate.of(2029, 1, 1))
                                .build()
                                ))
                        .build())
                .buyOutStartDate(LocalDate.now())
                .buyOutPrimaryPaymentDeadline(LocalDate.now())
                .secondaryReportingStartDate(LocalDate.now())
                .schemeVersion(SchemeVersion.CCA_3)
                .build();
	}

    private TargetUnitAccount createAccount(Long id, String businessId, TargetUnitAccountStatus status, LocalDate acceptedDate, LocalDate terminatedDate) {
        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(id)
                .businessId(businessId)
                .name("name" + id)
                .sectorAssociationId(1L)
                .status(status)
                .acceptedDate(acceptedDate.atTime(13, 0))
                .terminatedDate(terminatedDate == null ? null : terminatedDate.atStartOfDay())
                .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .createdBy("user1")
                .address(createAddress())
                .contacts(Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"))
                .build();
        entityManager.persist(account);
        return account;
    }

    private AccountAddress createAddress() {
        AccountAddress address = AccountAddress.builder().line1("123 Test Street").city("Test City").postcode("12345")
                .country("Test Country").build();
        entityManager.persist(address);
        return address;
    }
    
    private FacilityData createFacility(String businessId, Long accountId, LocalDateTime creationDate) {
    	
    	FacilityAddress address = FacilityAddress.builder()
                .line1("123 Test Street")
                .city("Test City")
                .postcode("12345")
                .country("Test Country")
                .build();
        entityManager.persist(address);
        
    	FacilityData facility = FacilityData.builder()
    			.facilityBusinessId(businessId)
                .accountId(accountId)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                .siteName("name")
                .address(address)
                .createdDate(creationDate)
                .build();

        entityManager.persist(facility);

        return facility;
    }
    
    private PerformanceDataFacilityEntity createPerformanceDataFacility(Year targetPeriodYear,
			TargetPeriod targetPeriod, Long facilityId) {
		PerformanceDataFacilityEntity pdfe = PerformanceDataFacilityEntity.builder()
        		.facilityId(facilityId)
        		.reportVersion(10)
        		.targetPeriod(targetPeriod)
        		.targetPeriodYear(targetPeriodYear)
        		.submissionType(PerformanceDataSubmissionType.PRIMARY)
        		.data(PerformanceDataFacilityContainer.builder()
        				.energyFuelDetails(PerformanceDataFacilityEnergyFuelDetails.builder()
        						.atLeastSeventyPercentEnergyUsed(Boolean.FALSE)
        						.build())
        				.baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder().build())
        				.throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
        						.totalTargetVariableEnergy(BigDecimal.ONE)
        						.build())
        				.calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
        						.actualEnergyCarbon(BigDecimal.valueOf(1.99999))
        						.actualImprovement(BigDecimal.valueOf(0.99999))
        						.targetImprovement(BigDecimal.ONE)
        						.targetEnergyCarbon(BigDecimal.ONE)
        						.targetCo2Emissions(BigDecimal.ONE)
        						.energyCarbonDifference(BigDecimal.valueOf(10.15))
        						.weightedConversionFactor(BigDecimal.ONE)
        						.co2EmissionsDifference(BigDecimal.ONE)
        						.actualCo2Emissions(BigDecimal.ONE)
        						.targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
        						.buyOutRequired(BigDecimal.ZERO)
        						.surplusGained(BigDecimal.valueOf(5))
        						.build())
        				.build())
        		.build();
        entityManager.persist(pdfe);
		return pdfe;
	}
    
    private PerformanceDataFacilityEntity createPerformanceDataFacilityInterim(Year targetPeriodYear,
			TargetPeriod targetPeriod, Long facilityId) {
		PerformanceDataFacilityEntity pdfe = PerformanceDataFacilityEntity.builder()
        		.facilityId(facilityId)
        		.reportVersion(1)
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
        						.actualEnergyCarbon(BigDecimal.valueOf(1.99999))
        						.actualImprovement(BigDecimal.valueOf(0.99999))
        						.targetImprovement(BigDecimal.ONE)
        						.targetEnergyCarbon(BigDecimal.ONE)
        						.targetCo2Emissions(BigDecimal.ONE)
        						.energyCarbonDifference(BigDecimal.valueOf(10.15))
        						.weightedConversionFactor(BigDecimal.ONE)
        						.co2EmissionsDifference(BigDecimal.ONE)
        						.actualCo2Emissions(BigDecimal.ONE)
        						.build())
        				.build())
        		.build();
        entityManager.persist(pdfe);
		return pdfe;
	}
    
    private PerformanceDataFacilityStatus createPerformanceDataFacilityStatus(Year targetPeriodYear,
			TargetPeriod targetPeriod, PerformanceDataFacilityEntity data, Long facilityId) {
    	PerformanceDataFacilityStatus pdfs1 = PerformanceDataFacilityStatus.builder()
        		.facilityId(facilityId)
        		.targetPeriodYear(targetPeriodYear)
        		.targetPeriod(targetPeriod)
        		.lastPerformanceData(data)
        		.build();
        entityManager.persist(pdfs1);
        return pdfs1;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}