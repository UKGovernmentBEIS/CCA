package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @BeforeEach
    void setUp() {
        BuyOutSurplusCalculation buyOutSurplusCalculation1 = createBuyOutSurplusCalculation(1L, "facilityBusinessId1", 1L, LocalDate.of(2027, 1, 1).atStartOfDay());
        entityManager.persist(buyOutSurplusCalculation1);
        BuyOutSurplusCalculation buyOutSurplusCalculation2 = createBuyOutSurplusCalculation(2L, "facilityBusinessId2", 2L, LocalDate.of(2027, 1, 1).atStartOfDay());
        entityManager.persist(buyOutSurplusCalculation2);
    }

    @Test
    void findAll() {
        List<BuyOutSurplusCalculation> calculations = repository.findAll();

        assertThat(calculations).hasSize(2);
    }

    @Test
    void findAllByAccountId() {
        List<BuyOutSurplusCalculation> calculations = repository.findAllByAccountId(1L);

        assertThat(calculations).hasSize(1);
    }

    @AfterEach
    void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private BuyOutSurplusCalculation createBuyOutSurplusCalculation(Long accountId, String facilityBusinessId, Long performanceDataId, LocalDateTime creationDate) {
        return BuyOutSurplusCalculation.builder()
                .facilityBusinessId(facilityBusinessId)
                .accountId(accountId)
                .performanceDataId(performanceDataId)
                .targetPeriodType(TargetPeriodType.TP7)
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
