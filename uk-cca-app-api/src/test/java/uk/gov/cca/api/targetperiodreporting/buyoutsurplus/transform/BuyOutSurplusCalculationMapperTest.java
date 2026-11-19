package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusCalculationMapperTest {

    private final BuyOutSurplusCalculationMapper mapper = Mappers.getMapper(BuyOutSurplusCalculationMapper.class);

    @Test
    void toBuyOutSurplusCalculationDetail() {
        final BuyOutSurplusCalculation buyOutSurplusCalculation = BuyOutSurplusCalculation.builder()
                .facilityId(1L)
                .accountId(1L)
                .performanceDataId(1L)
                .targetPeriod(getTargetPeriodByBusinessId(TargetPeriodType.TP7))
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
                .build();

        final BuyOutSurplusCalculationDetail expected = BuyOutSurplusCalculationDetail.builder()
                .facilityId(1L)
                .accountId(1L)
                .performanceDataId(1L)
                .targetPeriodId(getTargetPeriodByBusinessId(TargetPeriodType.TP7).getId())
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
                .build();

        BuyOutSurplusCalculationDetail actual = mapper.toBuyOutSurplusCalculationDetail(buyOutSurplusCalculation);

        assertThat(actual).isEqualTo(expected);
    }
    
    static List<TargetPeriod> getTargetPeriods() {
        return List.of(
            TargetPeriod.builder()
                .id(1L)
                .businessId(TargetPeriodType.TP7)
                .buyOutCost(BigDecimal.valueOf(10))
                .startDate(LocalDate.of(2026, 1, 1))
                .schemeVersion(SchemeVersion.CCA_3)
                .build(),
                TargetPeriod.builder()
                .id(2L)
                .businessId(TargetPeriodType.TP8)
                .buyOutCost(BigDecimal.valueOf(100))
                .startDate(LocalDate.of(2027, 1, 1))
                .schemeVersion(SchemeVersion.CCA_3)
                .build()
        );
    }
    
    static TargetPeriod getTargetPeriodByBusinessId(TargetPeriodType businessId) {
        return getTargetPeriods().stream()
            .filter(dto -> dto.getBusinessId().equals(businessId))
            .findFirst()
            .orElse(null);
    }
}
