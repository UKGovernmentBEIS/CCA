package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusFacilityAccountProcessingData {

	private Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> performanceDataReports;
	private Map<String, Map<TargetPeriodType, List<BuyOutSurplusTransactionDetailSummary>>> paidOrRefundedTransactions;
	private Map<String, Map<TargetPeriodType, BuyOutSurplusCalculationDetail>> buyOutSurplusCalculations;
	private Map<String, BigDecimal> surplusBuckets;
}
