package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusCalculationDTO {

    private Long id;

    // Performance Data Details
    private TargetPeriodType targetPeriodType;
    private Long accountId;
    private String facilityBusinessId;
    private Long performanceDataId;
    private PerformanceDataFacilityTargetPeriodResultType performanceOutcome;
    private BigDecimal buyOutRequired;
    private BigDecimal surplusGained;

    // Calculated Fields
    private BigDecimal surplusUsed;
    private BigDecimal previousPaidFees;
    private BigDecimal previousSurplusGained;
    private BigDecimal previousSurplusUsed;

    // Final Result
    private BuyOutSurplusChargeType chargeType;
    private BigDecimal invoicedBuyOutFee;
    private BigDecimal schemeTotalSurplus;

    private LocalDateTime creationDate;
}
