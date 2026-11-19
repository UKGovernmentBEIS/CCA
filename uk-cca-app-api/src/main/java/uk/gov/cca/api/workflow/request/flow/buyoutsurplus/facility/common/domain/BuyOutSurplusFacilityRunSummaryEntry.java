package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusFacilityRunSummaryEntry {

	@NotNull
    private TargetPeriodType targetPeriod;

    @NotNull
    private BigDecimal buyOutCost;
    
    @NotNull
    @PositiveOrZero
    private Long totalFacilities;
    
    @NotNull
    @PositiveOrZero
    private Long totalBuyOutTransactions;
    
    @NotNull
    @PositiveOrZero
    private Long totalBuyOutFacilities;

    @NotNull
    @PositiveOrZero
    private Long totalRefundTransactions;
    
    @NotNull
    @PositiveOrZero
    private Long totalRefundFacilities;
}
