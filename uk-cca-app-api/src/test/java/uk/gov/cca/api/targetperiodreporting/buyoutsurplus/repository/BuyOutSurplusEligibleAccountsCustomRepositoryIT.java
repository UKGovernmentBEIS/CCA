package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaAccountContactType;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.ActualPerformance;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceResult;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.SurplusBuyOutDetermination;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetsPreviousPerformance;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEnergyFuelDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@EnableAutoConfiguration
@EnableJpaAuditing
@ContextConfiguration(classes = BuyOutSurplusEligibleAccountsCustomRepository.class)
@DataJpaTest
class BuyOutSurplusEligibleAccountsCustomRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
	private BuyOutSurplusEligibleAccountsCustomRepository repository;
	
	@Autowired
    private EntityManager entityManager;
	
	@Test
	void findAccountsWithPerformanceDataPendingBuyOut() {
	    TargetPeriod targetPeriod = createTargetPeriod(TargetPeriodType.TP6);

	    TargetUnitAccount account1 = createAccount(1L, "ACC1");
	    TargetUnitAccount account2 = createAccount(2L, "ACC2");

	    PerformanceDataEntity pd1 = createPerformanceData(targetPeriod, account1.getId());
	    PerformanceDataEntity pd2 = createPerformanceData(targetPeriod, account2.getId());

	    entityManager.persist(AccountPerformanceDataStatus.builder()
	            .accountId(account1.getId())
	            .targetPeriod(targetPeriod)
	            .lastPerformanceData(pd1)
	            .build());

	    entityManager.persist(AccountPerformanceDataStatus.builder()
	            .accountId(account2.getId())
	            .targetPeriod(targetPeriod)
	            .lastPerformanceData(pd2)
	            .build());

	    entityManager.persist(BuyOutSurplusProcessedData.builder()
	            .performanceDataId(pd2.getId())
	            .performanceDataResourceType(PerformanceDataResourceType.ACCOUNT)
	            .creationDate(LocalDateTime.now())
	            .build());

	    flushAndClear();

	    List<TargetUnitAccountBusinessInfoDTO> result =
	            repository.findAccountsWithPerformanceDataPendingBuyOut(TargetPeriodType.TP6);

	    assertThat(result)
	            .extracting(TargetUnitAccountBusinessInfoDTO::getBusinessId)
	            .containsExactly(account1.getBusinessId());
	}
	
	@Test
	void findAccountsWithFacilityPerformanceDataPendingBuyOut_returnsPendingAccountsOnly() {

	    TargetPeriod targetPeriod = createTargetPeriod(TargetPeriodType.TP7);

	    TargetUnitAccount account1 = createAccount(1L, "ACC1");
	    TargetUnitAccount account2 = createAccount(2L, "ACC2");

	    FacilityData facility1 = createFacility("F1", account1.getId());
	    FacilityData facility2 = createFacility("F2", account2.getId());

	    PerformanceDataFacilityEntity pd1 = createPerformanceDataFacility(
	    		Year.of(2026), targetPeriod, facility1.getId(), PerformanceDataSubmissionType.PRIMARY);

	    PerformanceDataFacilityEntity pd2 = createPerformanceDataFacility(
	    		Year.of(2026), targetPeriod, facility2.getId(), PerformanceDataSubmissionType.PRIMARY);

	    entityManager.persist(PerformanceDataFacilityStatus.builder()
	            .facilityId(facility1.getId())
	            .targetPeriod(targetPeriod)
	            .targetPeriodYear(Year.of(2026))
	            .lastPerformanceData(pd1)
	            .build());

	    entityManager.persist(PerformanceDataFacilityStatus.builder()
	            .facilityId(facility2.getId())
	            .targetPeriod(targetPeriod)
	            .targetPeriodYear(Year.of(2026))
	            .lastPerformanceData(pd2)
	            .build());

	    entityManager.persist(BuyOutSurplusProcessedData.builder()
	            .performanceDataId(pd2.getId())
	            .performanceDataResourceType(PerformanceDataResourceType.FACILITY)
	            .creationDate(LocalDateTime.now())
	            .build());

	    flushAndClear();

	    List<TargetUnitAccountBusinessInfoDTO> result =
	            repository.findAccountsWithFacilityPerformanceDataPendingBuyOut(
	                    Set.of(TargetPeriodType.TP7));

	    assertThat(result)
	            .extracting(TargetUnitAccountBusinessInfoDTO::getBusinessId)
	            .containsExactly(account1.getBusinessId());
	}
	
	@Test
	void findAccountsWithFacilityPerformanceDataPendingBuyOut_returnsAccountsWithFinalSubmissionOnly() {

	    TargetPeriod targetPeriod = createTargetPeriod(TargetPeriodType.TP7);

	    TargetUnitAccount account1 = createAccount(1L, "ACC1");
	    TargetUnitAccount account2 = createAccount(2L, "ACC2");

	    FacilityData facility1 = createFacility("F1", account1.getId());
	    FacilityData facility2 = createFacility("F2", account2.getId());

	    PerformanceDataFacilityEntity pd1 = createPerformanceDataFacility(
	    		Year.of(2026), targetPeriod, facility1.getId(), PerformanceDataSubmissionType.PRIMARY);

	    PerformanceDataFacilityEntity pd2 = createPerformanceDataFacility(Year.of(2026), targetPeriod, facility2.getId(), null);

	    entityManager.persist(PerformanceDataFacilityStatus.builder()
	            .facilityId(facility1.getId())
	            .targetPeriod(targetPeriod)
	            .targetPeriodYear(Year.of(2026))
	            .lastPerformanceData(pd1)
	            .build());

	    entityManager.persist(PerformanceDataFacilityStatus.builder()
	            .facilityId(facility2.getId())
	            .targetPeriod(targetPeriod)
	            .targetPeriodYear(Year.of(2026))
	            .lastPerformanceData(pd2)
	            .build());

	    flushAndClear();

	    List<TargetUnitAccountBusinessInfoDTO> result =
	            repository.findAccountsWithFacilityPerformanceDataPendingBuyOut(
	                    Set.of(TargetPeriodType.TP7));

	    assertThat(result)
	            .extracting(TargetUnitAccountBusinessInfoDTO::getBusinessId)
	            .containsExactly(account1.getBusinessId());
	}
	
	@Test
	void findAccountsWithFacilityPerformanceDataPendingBuyOut_returnsDistinctAccounts() {

	    TargetPeriod targetPeriod = createTargetPeriod(TargetPeriodType.TP7);

	    TargetUnitAccount account = createAccount(1L, "ACC1");

	    FacilityData facility1 = createFacility("F1", account.getId());
	    FacilityData facility2 = createFacility("F2", account.getId());

	    PerformanceDataFacilityEntity pd1 = createPerformanceDataFacility(
	    		Year.of(2026), targetPeriod, facility1.getId(), PerformanceDataSubmissionType.PRIMARY);

	    PerformanceDataFacilityEntity pd2 = createPerformanceDataFacility(
	    		Year.of(2026), targetPeriod, facility2.getId(), PerformanceDataSubmissionType.PRIMARY);

	    entityManager.persist(PerformanceDataFacilityStatus.builder()
	            .facilityId(facility1.getId())
	            .targetPeriod(targetPeriod)
	            .targetPeriodYear(Year.of(2026))
	            .lastPerformanceData(pd1)
	            .build());

	    entityManager.persist(PerformanceDataFacilityStatus.builder()
	            .facilityId(facility2.getId())
	            .targetPeriod(targetPeriod)
	            .targetPeriodYear(Year.of(2026))
	            .lastPerformanceData(pd2)
	            .build());

	    flushAndClear();

	    List<TargetUnitAccountBusinessInfoDTO> result =
	            repository.findAccountsWithFacilityPerformanceDataPendingBuyOut(
	                    Set.of(TargetPeriodType.TP7));

	    assertThat(result).hasSize(1);
	    assertThat(result.get(0).getBusinessId()).isEqualTo(account.getBusinessId());
	}
	
	@AfterEach
    void flushAndClear() {
    	entityManager.flush();
        entityManager.clear();
    }
	
	private TargetPeriod createTargetPeriod(TargetPeriodType type) {
        TargetPeriod targetPeriod = TargetPeriod.builder()
                .businessId(type)
                .name(type.name())
                .startDate(LocalDate.now())
                .endDate(LocalDate.of(2026, 11, 3))
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
	
	private TargetUnitAccount createAccount(Long id, String businessId) {
        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(id)
                .businessId(businessId)
                .name("name" + id)
                .sectorAssociationId(1L)
                .status(TargetUnitAccountStatus.LIVE)
                .acceptedDate(LocalDateTime.now())
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
	
	private FacilityData createFacility(String businessId, Long accountId) {
    	
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
                .createdDate(LocalDateTime.now())
                .build();

        entityManager.persist(facility);

        return facility;
    }
	
	private PerformanceDataFacilityEntity createPerformanceDataFacility(Year targetPeriodYear,
			TargetPeriod targetPeriod, Long facilityId, PerformanceDataSubmissionType type) {
		PerformanceDataFacilityEntity pdfe = PerformanceDataFacilityEntity.builder()
        		.facilityId(facilityId)
        		.reportVersion(10)
        		.targetPeriod(targetPeriod)
        		.targetPeriodYear(targetPeriodYear)
        		.submissionType(type)
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
	
	private PerformanceDataEntity createPerformanceData(TargetPeriod targetPeriod, Long accountId) {

	    PerformanceDataEntity entity = PerformanceDataEntity.builder()
	            .accountId(accountId)
	            .targetPeriod(targetPeriod)
	            .reportVersion(1)
	            .submissionType(PerformanceDataSubmissionType.PRIMARY)
	            .submissionDate(LocalDateTime.now())
	            .data(PerformanceDataContainer.builder()
                        .targetsPreviousPerformance(TargetsPreviousPerformance.builder()
                                .numOfFacilities(1)
                                .targetType(AgreementCompositionType.NOVEM)
                                .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                                .byStartDate(LocalDate.now())
                                .byEnergyCarbon(BigDecimal.ZERO)
                                .percentTarget(BigDecimal.ZERO)
                                .bankedSurplus(BigDecimal.ZERO)
                                .build())
                        .actualPerformance(ActualPerformance.builder()
                                .actualThroughput(BigDecimal.ZERO)
                                .tpEnergy(BigDecimal.ZERO)
                                .tpChpDeliveredElectricity(BigDecimal.ZERO)
                                .reportingThroughput(BigDecimal.ZERO)
                                .build())
                        .performanceResult(PerformanceResult.builder()
                                .tpPerformance(BigDecimal.ZERO)
                                .tpPerformancePercent(BigDecimal.ZERO)
                                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                                .build())
                        .surplusBuyOutDetermination(SurplusBuyOutDetermination.builder()
                                .tpCarbonFactor(BigDecimal.ZERO)
                                .energyCarbonUnderTarget(BigDecimal.ZERO)
                                .carbonUnderTarget(BigDecimal.ZERO)
                                .co2Emissions(BigDecimal.ZERO)
                                .surplusUsed(BigDecimal.ZERO)
                                .surplusGained(BigDecimal.ZERO)
                                .priBuyOutCarbon(BigDecimal.ZERO)
                                .priBuyOutCost(BigDecimal.ZERO)
                                .totalPriBuyOutCarbon(BigDecimal.ZERO)
                                .build())
                        .targetPeriodReport(FileInfoDTO.builder().build())
                        .build())
	            .build();

	    entityManager.persist(entity);

	    return entity;
	}
}
