package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusCalculationDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusCalculationRepository;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusCalculationServiceTest {

    @InjectMocks
    private BuyOutSurplusCalculationService service;

    @Mock
    private BuyOutSurplusCalculationRepository buyOutSurplusCalculationRepository;

    @Test
    void getLatestBuyOutCalculationsPerFacility() {
        final Long accountId = 1L;
        final List<BuyOutSurplusCalculation> buyOutSurplusCalculations = List.of(
                BuyOutSurplusCalculation.builder()
                        .facilityBusinessId("facilityBusinessId1")
                        .accountId(1L)
                        .performanceDataId(1L)
                        .targetPeriodType(TargetPeriodType.TP7)
                        .creationDate(LocalDate.of(2027, 1, 1).atStartOfDay())
                        .data(BuyOutSurplusCalculationDataContainer.builder()
                                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                                .buyOutRequired(BigDecimal.valueOf(252))
                                .surplusGained(BigDecimal.ZERO)
                                .invoicedBuyOutFee(BigDecimal.ZERO)
                                .previousSurplusGained(BigDecimal.ZERO)
                                .previousSurplusUsed(BigDecimal.ZERO)
                                .surplusUsed(BigDecimal.valueOf(100))
                                .previousPaidFees(BigDecimal.ZERO)
                                .schemeTotalSurplus(BigDecimal.ZERO)
                                .chargeType(null)
                                .build())
                        .build(),
                BuyOutSurplusCalculation.builder()
                        .facilityBusinessId("facilityBusinessId1")
                        .accountId(1L)
                        .performanceDataId(2L)
                        .targetPeriodType(TargetPeriodType.TP7)
                        .creationDate(LocalDate.of(2027, 1, 4).atStartOfDay())
                        .data(BuyOutSurplusCalculationDataContainer.builder()
                                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                                .buyOutRequired(BigDecimal.valueOf(252))
                                .surplusGained(BigDecimal.ZERO)
                                .invoicedBuyOutFee(BigDecimal.ZERO)
                                .previousSurplusGained(BigDecimal.ZERO)
                                .previousSurplusUsed(BigDecimal.ZERO)
                                .surplusUsed(BigDecimal.valueOf(100))
                                .previousPaidFees(BigDecimal.ZERO)
                                .schemeTotalSurplus(BigDecimal.ZERO)
                                .chargeType(null)
                                .build())
                        .build(),
                BuyOutSurplusCalculation.builder()
                        .facilityBusinessId("facilityBusinessId2")
                        .accountId(1L)
                        .performanceDataId(3L)
                        .targetPeriodType(TargetPeriodType.TP7)
                        .creationDate(LocalDate.of(2027, 1, 1).atStartOfDay())
                        .data(BuyOutSurplusCalculationDataContainer.builder()
                                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                                .buyOutRequired(BigDecimal.valueOf(252))
                                .surplusGained(BigDecimal.ZERO)
                                .invoicedBuyOutFee(BigDecimal.ZERO)
                                .previousSurplusGained(BigDecimal.ZERO)
                                .previousSurplusUsed(BigDecimal.ZERO)
                                .surplusUsed(BigDecimal.valueOf(100))
                                .previousPaidFees(BigDecimal.ZERO)
                                .schemeTotalSurplus(BigDecimal.ZERO)
                                .chargeType(null)
                                .build())
                        .build());

        final BuyOutSurplusCalculationDTO mapValue1 = BuyOutSurplusCalculationDTO.builder()
                .facilityBusinessId("facilityBusinessId1")
                .accountId(1L)
                .performanceDataId(2L)
                .targetPeriodType(TargetPeriodType.TP7)
                .creationDate(LocalDate.of(2027, 1, 4).atStartOfDay())
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .buyOutRequired(BigDecimal.valueOf(252))
                .surplusGained(BigDecimal.ZERO)
                .invoicedBuyOutFee(BigDecimal.ZERO)
                .previousSurplusGained(BigDecimal.ZERO)
                .previousSurplusUsed(BigDecimal.ZERO)
                .surplusUsed(BigDecimal.valueOf(100))
                .previousPaidFees(BigDecimal.ZERO)
                .schemeTotalSurplus(BigDecimal.ZERO)
                .chargeType(null)
                .build();

        final BuyOutSurplusCalculationDTO mapValue2 = BuyOutSurplusCalculationDTO.builder()
                .facilityBusinessId("facilityBusinessId2")
                .accountId(1L)
                .performanceDataId(3L)
                .targetPeriodType(TargetPeriodType.TP7)
                .creationDate(LocalDate.of(2027, 1, 1).atStartOfDay())
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .buyOutRequired(BigDecimal.valueOf(252))
                .surplusGained(BigDecimal.ZERO)
                .invoicedBuyOutFee(BigDecimal.ZERO)
                .previousSurplusGained(BigDecimal.ZERO)
                .previousSurplusUsed(BigDecimal.ZERO)
                .surplusUsed(BigDecimal.valueOf(100))
                .previousPaidFees(BigDecimal.ZERO)
                .schemeTotalSurplus(BigDecimal.ZERO)
                .chargeType(null)
                .build();

        when(buyOutSurplusCalculationRepository.findAllByAccountId(accountId)).thenReturn(buyOutSurplusCalculations);

        // invoke
        Map<String, Map<TargetPeriodType, BuyOutSurplusCalculationDTO>> result = service.getLatestBuyOutCalculationsPerFacility(accountId);

        // verify
        verify(buyOutSurplusCalculationRepository, times(1)).findAllByAccountId(accountId);
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "facilityBusinessId1", Map.of(TargetPeriodType.TP7, mapValue1),
                "facilityBusinessId2", Map.of(TargetPeriodType.TP7, mapValue2))
        );
    }
}
