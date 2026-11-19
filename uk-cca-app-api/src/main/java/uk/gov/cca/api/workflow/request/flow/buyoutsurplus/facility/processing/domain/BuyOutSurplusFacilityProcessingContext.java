package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusFacilityProcessingContext {

	private boolean applyPrimaryRules;
	private List<TargetPeriodInfoDTO> targetPeriods;
	private TargetPeriodInfoDTO currentTargetPeriod;
	private String facilityBusinessId;
	private Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> performanceData;
	private Map<TargetPeriodType, List<BuyOutSurplusTransactionDetailSummary>> paidOrRefundedTransactions;
	private Map<TargetPeriodType, BuyOutSurplusCalculationDetail> buyOutCalculations;
	private BigDecimal surplusBucket;
	private Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> missingTPRs;
	private Optional<TargetPeriodType> earliestUnprocessedTargetPeriodType;

    public boolean hasPreviousUnprocessedTPRs() {
        return getEarliestUnprocessedTargetPeriodType().isPresent()
                && !getEarliestUnprocessedTargetPeriodType().get().equals(currentTargetPeriod.getBusinessId());
    }

	public boolean isPrimaryPeriodWithMissingTPRs() {
		return applyPrimaryRules && !getMissingTPRs().isEmpty();
	}

	public boolean isSecondaryPeriodWithMissingTPRs() {
		return !applyPrimaryRules && !getMissingTPRs().isEmpty();
	}

}
