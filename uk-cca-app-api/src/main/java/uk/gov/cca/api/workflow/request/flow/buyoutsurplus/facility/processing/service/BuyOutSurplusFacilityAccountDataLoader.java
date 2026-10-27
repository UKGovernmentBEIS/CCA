package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusCalculationService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingData;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityAccountDataLoader {

	private final BuyOutSurplusQueryService buyOutSurplusQueryService;
	private final BuyOutSurplusCalculationService buyOutSurplusCalculationService;
	private final SurplusQueryService surplusQueryService;

	public BuyOutSurplusFacilityAccountProcessingData load(final Long accountId, final List<TargetPeriodInfoDTO> targetPeriods) {
	    
	    List<Long> targetPeriodIds = targetPeriods.stream()
	            .map(TargetPeriodInfoDTO::getId)
	            .toList();
	    SchemeVersion schemeVersion = targetPeriods.getFirst().getSchemeVersion();
		
		// Retrieve all facilities with their TPR
		Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> tprsByFacilityBusinessId = buyOutSurplusQueryService
				.getPerformanceDataWithBuyOutProcessedStatusPerFacility(accountId, targetPeriodIds);

		// Retrieve eligible transactions for each facility and target period
		Map<String, Map<TargetPeriodType, List<BuyOutSurplusTransactionDetailSummary>>> transactionsByFacilityBusinessId = getPaidOrRefundedTransactions(accountId, targetPeriodIds);

		// Retrieve latest calculations for each facility and target period
		Map<String, Map<TargetPeriodType, BuyOutSurplusCalculationDetail>> latestCalculationsByFacilityBusinessId = buyOutSurplusCalculationService
				.getLatestBuyOutCalculationsPerFacilityAndTargetPeriod(accountId, targetPeriodIds);

		// Retrieve surplus bucket per facility from DB
		Map<String, BigDecimal> surplusBucketsByFacilityBusinessId = getFacilitySurplusMap(getFacilityMap(tprsByFacilityBusinessId), schemeVersion);

		return BuyOutSurplusFacilityAccountProcessingData.builder()
				.performanceDataReports(tprsByFacilityBusinessId)
				.paidOrRefundedTransactions(transactionsByFacilityBusinessId)
				.buyOutSurplusCalculations(latestCalculationsByFacilityBusinessId)
				.surplusBuckets(surplusBucketsByFacilityBusinessId)
				.build();
	}
	
	private Map<Long, String> getFacilityMap(Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> tprsByFacilityBusinessId){
	    return tprsByFacilityBusinessId.values()
                .stream()
                .flatMap(dataMap -> dataMap.values().stream())
                .collect(Collectors.toMap(
                        FacilityPerformanceDataWithBuyOutProcessedStatus::getFacilityId,
                        FacilityPerformanceDataWithBuyOutProcessedStatus::getFacilityBusinessId,
                        (existing, replacement) -> existing
                ));
	}

    private Map<String, Map<TargetPeriodType, List<BuyOutSurplusTransactionDetailSummary>>> getPaidOrRefundedTransactions(Long accountId, List<Long> targetPeriodIds) {
        return buyOutSurplusQueryService.getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds)
                .stream()
                .filter(summary -> BuyOutSurplusPaymentStatus.getCompletedPayments().contains(summary.getPaymentStatus()))
                .sorted(Comparator.comparing(BuyOutSurplusTransactionDetailSummary::getCreationDate))
                .collect(Collectors.groupingBy(BuyOutSurplusTransactionDetailSummary::getFacilityBusinessId,
                        Collectors.groupingBy(BuyOutSurplusTransactionDetailSummary::getTargetPeriodType)));
    }

    private Map<String, BigDecimal> getFacilitySurplusMap(Map<Long, String> facilityMap, SchemeVersion schemeVersion) {
        Map<Long, BigDecimal> facilitySurplusList =  surplusQueryService.getFacilityPositiveSurplusMap(facilityMap.keySet().stream().toList(), schemeVersion);

        return facilitySurplusList.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> facilityMap.get(entry.getKey()),
                        Entry::getValue
                ));
    }
	
}
