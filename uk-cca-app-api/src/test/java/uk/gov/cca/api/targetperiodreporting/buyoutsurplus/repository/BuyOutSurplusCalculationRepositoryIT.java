package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@EnableAutoConfiguration
@EnableJpaAuditing
@ContextConfiguration(classes = BuyOutSurplusCalculationRepository.class)
@DataJpaTest
class BuyOutSurplusCalculationRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private BuyOutSurplusCalculationRepository repository;

    @Autowired
    private EntityManager entityManager;
    
    private TargetPeriod targetPeriod7;
    private TargetPeriod targetPeriod8;

    @BeforeEach
    void setUp() {
        targetPeriod7 = createTargetPeriod(TargetPeriodType.TP7, SchemeVersion.CCA_3);
        targetPeriod8 = createTargetPeriod(TargetPeriodType.TP8, SchemeVersion.CCA_3);
        BuyOutSurplusCalculation buyOutSurplusCalculation1 = createBuyOutSurplusCalculation(1L, 1L, targetPeriod7, 1L, LocalDate.of(2027, 1, 1).atStartOfDay());
        entityManager.persist(buyOutSurplusCalculation1);
        BuyOutSurplusCalculation buyOutSurplusCalculation2 = createBuyOutSurplusCalculation(1L, 1L, targetPeriod8, 2L, LocalDate.of(2027, 1, 1).atStartOfDay());
        entityManager.persist(buyOutSurplusCalculation2);
        BuyOutSurplusCalculation buyOutSurplusCalculation3 = createBuyOutSurplusCalculation(3L, 2L, targetPeriod7, 3L, LocalDate.of(2027, 1, 1).atStartOfDay());
        entityManager.persist(buyOutSurplusCalculation3);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findAll() {
        List<BuyOutSurplusCalculation> calculations = repository.findAll();

        assertThat(calculations).hasSize(3);
    }

    @Test
    void findAllByAccountIdAndTargetPeriodIdIn() {
        List<Long> targetPeriodIds = List.of(targetPeriod7.getId(), targetPeriod8.getId());
        List<BuyOutSurplusCalculation> calculations = repository.findAllByAccountIdAndTargetPeriodIdIn(1L, targetPeriodIds);

        assertThat(calculations).hasSize(2);
    }
    
    private TargetPeriod createTargetPeriod(
            TargetPeriodType targetPeriodType,
            SchemeVersion schemeVersion) {

        TargetPeriod targetPeriod = TargetPeriod.builder()
                .businessId(targetPeriodType)
                .name(targetPeriodType.name())
                .startDate(LocalDate.of(2027, 1, 1))
                .endDate(LocalDate.of(2027, 12, 31))
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(
                                TargetPeriodYear.builder()
                                        .targetYear(Year.of(2027))
                                        .startDate(LocalDate.of(2027, 1, 1))
                                        .endDate(LocalDate.of(2027, 12, 31))
                                        .reportingStartDate(LocalDate.of(2027, 1, 1))
                                        .build()))
                        .build())
                .buyOutStartDate(LocalDate.of(2027, 1, 1))
                .buyOutPrimaryPaymentDeadline(LocalDate.of(2027, 12, 31))
                .secondaryReportingStartDate(LocalDate.of(2027, 1, 1))
                .schemeVersion(schemeVersion)
                .build();

        entityManager.persist(targetPeriod);
        return targetPeriod;
    }


    private BuyOutSurplusCalculation createBuyOutSurplusCalculation(Long accountId, Long facilityId, TargetPeriod targetPeriod, Long performanceDataId, LocalDateTime creationDate) {
        return BuyOutSurplusCalculation.builder()
                .facilityId(facilityId)
                .accountId(accountId)
                .performanceDataId(performanceDataId)
                .targetPeriod(targetPeriod)
                .creationDate(creationDate)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.ONE)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(null)
                        .build())
                .build();
    }
}
