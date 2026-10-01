package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusCalculationDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusCalculationMapperTest {

    private final BuyOutSurplusCalculationMapper mapper = Mappers.getMapper(BuyOutSurplusCalculationMapper.class);

    @Test
    void toBuyOutSurplusCalculationDTO() {

        final BuyOutSurplusCalculation buyOutSurplusCalculation = BuyOutSurplusCalculation.builder()
                .facilityBusinessId("facilityBusinessId")
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
                .build();

        final BuyOutSurplusCalculationDTO expected = BuyOutSurplusCalculationDTO.builder()
                .facilityBusinessId("facilityBusinessId")
                .accountId(1L)
                .performanceDataId(1L)
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

        BuyOutSurplusCalculationDTO actual = mapper.toBuyOutSurplusCalculationDTO(buyOutSurplusCalculation);

        assertThat(actual).isEqualTo(expected);
    }
}
