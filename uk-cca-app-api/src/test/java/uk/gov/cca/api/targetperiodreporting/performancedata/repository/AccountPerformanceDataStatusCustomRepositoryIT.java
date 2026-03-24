package uk.gov.cca.api.targetperiodreporting.performancedata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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

import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaAccountContactType;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataInfo;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.ActualPerformance;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceResult;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.SurplusBuyOutDetermination;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetsPreviousPerformance;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@EnableAutoConfiguration
@EnableJpaAuditing
@ContextConfiguration(classes = AccountPerformanceDataStatusCustomRepository.class)
@DataJpaTest
@Import(ObjectMapper.class)
class AccountPerformanceDataStatusCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AccountPerformanceDataStatusCustomRepository cut;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        TargetPeriod targetPeriod = createTargetPeriod(TargetPeriodType.TP6);
        TargetPeriod targetPeriodAnother = createTargetPeriod(TargetPeriodType.TP5);

        TargetUnitAccount account1 = createAccount(-1L, TargetUnitAccountStatus.LIVE);
        PerformanceDataEntity performanceDataEntity1 = createPerformanceDataEntity(targetPeriod, account1.getId());
        createAccountPerformanceDataStatus(targetPeriod, account1.getId(), true, performanceDataEntity1);

        TargetUnitAccount account2 = createAccount(-2L, TargetUnitAccountStatus.TERMINATED);
        PerformanceDataEntity performanceDataEntity2 = createPerformanceDataEntity(targetPeriod, account2.getId());
        createAccountPerformanceDataStatus(targetPeriod, account2.getId(), true, performanceDataEntity2);

        // With no performance data
        createAccount(-3L, TargetUnitAccountStatus.LIVE);

        // With no performance data status
        TargetUnitAccount account4 = createAccount(-4L, TargetUnitAccountStatus.LIVE);
        createPerformanceDataEntity(targetPeriod, account4.getId());

        // With other target period
        TargetUnitAccount account6 = createAccount(-5L, TargetUnitAccountStatus.LIVE);
        PerformanceDataEntity performanceDataEntity6 = createPerformanceDataEntity(targetPeriodAnother, account6.getId());
        createAccountPerformanceDataStatus(targetPeriodAnother, account6.getId(), true, performanceDataEntity6);

        flushAndClear();
    }

    @Test
    void findAccountsWithPerformanceDataByTargetPeriod() {
        // Invoke
        List<AccountPerformanceDataInfo> result = cut
                .findAccountsWithPerformanceDataByTargetPeriod(TargetPeriodType.TP6);

        // Verify
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAccountId()).isEqualTo(-1L);
    }

    @Test
    void findAccountsWithPerformanceDataByTargetPeriodAndAccountIdIn() {
        final Set<Long> accountIds = Set.of(-1L, 2L, 3L);

        // Invoke
        List<AccountPerformanceDataInfo> result = cut
                .findAccountsWithPerformanceDataByTargetPeriodAndAccountIdIn(TargetPeriodType.TP6, accountIds);

        // Verify
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAccountId()).isEqualTo(-1L);
    }

    private TargetUnitAccount createAccount(Long id, TargetUnitAccountStatus status) {
        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(id)
                .businessId("businessId" + id)
                .name("name" + id)
                .sectorAssociationId(1L)
                .status(status)
                .acceptedDate(LocalDate.of(2023, 11, 3).atStartOfDay())
                .terminatedDate(null)
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

    private TargetPeriod createTargetPeriod(TargetPeriodType type) {
        TargetPeriod targetPeriod = TargetPeriod.builder()
                .businessId(type)
                .name(type.name())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(TargetPeriodYear.builder()
                                .targetYear(Year.now())
                                .startDate(LocalDate.now())
                                .endDate(LocalDate.now())
                                .reportingStartDate(LocalDate.now())
                                .build()))
                        .build())
                .buyOutStartDate(LocalDate.now())
                .buyOutPrimaryPaymentDeadline(LocalDate.now())
                .secondaryReportingStartDate(LocalDate.now())
                .build();
        entityManager.persist(targetPeriod);
        return targetPeriod;
    }

    private PerformanceDataEntity createPerformanceDataEntity(TargetPeriod targetPeriod, Long accountId) {
        PerformanceDataEntity performanceDataEntity = PerformanceDataEntity.builder()
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
                .targetPeriod(targetPeriod)
                .accountId(accountId)
                .reportVersion(1)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .build();
        entityManager.persist(performanceDataEntity);
        return performanceDataEntity;
    }

    private void createAccountPerformanceDataStatus(TargetPeriod targetPeriod, Long accountId, boolean locked, PerformanceDataEntity lastPerformanceData) {
        AccountPerformanceDataStatus accountPerformanceDataStatus = AccountPerformanceDataStatus.builder()
                .targetPeriod(targetPeriod)
                .accountId(accountId)
                .locked(locked)
                .lastPerformanceData(lastPerformanceData)
                .build();
        entityManager.persist(accountPerformanceDataStatus);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
