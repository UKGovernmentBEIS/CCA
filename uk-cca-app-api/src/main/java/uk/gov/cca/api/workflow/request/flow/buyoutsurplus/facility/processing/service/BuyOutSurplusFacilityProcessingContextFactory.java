package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityProcessingContext;

@Component
public class BuyOutSurplusFacilityProcessingContextFactory {

	public BuyOutSurplusFacilityProcessingContext create(String facilityBusinessId,
            List<TargetPeriodInfoDTO> targetPeriods,
            boolean applyPrimaryRules, 
			Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> performanceData,
			Map<TargetPeriodType, List<BuyOutSurplusTransactionDetailSummary>> paidOrRefundedTransactions,
			Map<TargetPeriodType, BuyOutSurplusCalculationDetail> calculations, 
			BigDecimal surplusBucket) {

		TargetPeriodInfoDTO currentTargetPeriod = targetPeriods.getLast();

		Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> missingTPRs = findMissingTPRs(performanceData);

		Optional<TargetPeriodType> earliestUnprocessedTP = findEarliestUnprocessedTargetPeriod(performanceData, targetPeriods);

		return BuyOutSurplusFacilityProcessingContext.builder()
				.facilityBusinessId(facilityBusinessId)
				.targetPeriods(targetPeriods)
				.currentTargetPeriod(currentTargetPeriod)
				.performanceData(performanceData)
				.paidOrRefundedTransactions(paidOrRefundedTransactions)
				.buyOutCalculations(calculations)
				.surplusBucket(ObjectUtils.defaultIfNull(surplusBucket, BigDecimal.ZERO))
				.applyPrimaryRules(applyPrimaryRules)
				.missingTPRs(missingTPRs)
				.earliestUnprocessedTargetPeriodType(earliestUnprocessedTP)
				.build();
	}

	private Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> findMissingTPRs(
			Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> performanceData) {

		return performanceData.entrySet().stream()
				.filter(entry -> !entry.getValue().isLocked() && entry.getValue().isProcessed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	// Find the earliest unprocessed TPR ~ targetPeriods is ordered from earliest to latest
	private Optional<TargetPeriodType> findEarliestUnprocessedTargetPeriod(
			Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> performanceData,
			List<TargetPeriodInfoDTO> targetPeriods) {

		return targetPeriods.stream()
				.map(TargetPeriodInfoDTO::getBusinessId)
				.map(performanceData::get)
				.filter(Objects::nonNull)
				.filter(tpr -> !tpr.isProcessed())
				.map(FacilityPerformanceDataWithBuyOutProcessedStatus::getTargetPeriodType)
				.findFirst();
	}
}
