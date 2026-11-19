package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.transform;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunSummary;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunSummaryEntry;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.domain.BuyOutSurplusFacilityRunCompletedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BuyOutSurplusFacilityRunMapper {

	default BuyOutSurplusFacilityRunSummary toBuyOutSurplusFacilityRunSummary(Map<Long, BuyOutSurplusFacilityAccountState> accountStates, List<TargetPeriodInfoDTO> targetPeriods) {
		long totalAccounts = accountStates.size();

	    long failedAccounts = accountStates.values().stream()
	            .filter(accountState -> !accountState.isSucceeded())
	            .count();

	    Map<String, List<BuyOutSurplusFacilityPerformanceResult>> resultsByFacility =
	            accountStates.values().stream()
	                    .flatMap(accountState -> accountState.getFacilityPerformanceResults().stream())
	                    .collect(Collectors.groupingBy(BuyOutSurplusFacilityPerformanceResult::getFacilityBusinessId));

	    long totalFacilities = resultsByFacility.size();

	    long failedFacilities = resultsByFacility.values().stream()
	            .filter(results -> results.stream().allMatch(result -> !result.isSucceeded()))
	            .count();

	    return BuyOutSurplusFacilityRunSummary.builder()
	            .totalAccounts(totalAccounts)
	            .failedAccounts(failedAccounts)
	            .totalFacilities(totalFacilities)
	            .failedFacilities(failedFacilities)
	            .runSummaryEntries(buildRunSummaryEntries(accountStates, targetPeriods))
	            .build();
	}
	
	default List<BuyOutSurplusFacilityRunSummaryEntry> buildRunSummaryEntries(Map<Long, BuyOutSurplusFacilityAccountState> accountStates, List<TargetPeriodInfoDTO> targetPeriods) {

		return targetPeriods.stream()
	            .map(targetPeriod -> buildRunSummaryEntry(targetPeriod, accountStates))
	            .filter(Objects::nonNull)
	            .toList();
	}

	default BuyOutSurplusFacilityRunSummaryEntry buildRunSummaryEntry(
	        TargetPeriodInfoDTO targetPeriod,
	        Map<Long, BuyOutSurplusFacilityAccountState> accountStates) {

	    List<Map.Entry<Long, BuyOutSurplusFacilityPerformanceResult>> periodResults =
	            accountStates.entrySet().stream()
	                    .flatMap(entry ->
	                            entry.getValue().getFacilityPerformanceResults().stream()
	                                    .filter(result -> result.getTargetPeriodType() == targetPeriod.getBusinessId() && result.isSucceeded() == true)
	                                    .map(result -> Map.entry(entry.getKey(), result)))
	                    .toList();
	    
	    if(periodResults.isEmpty()) {
	        return null;
	    }

	    long distinctFacilityCount = periodResults.stream()
	            .map(Map.Entry::getValue)
	            .map(BuyOutSurplusFacilityPerformanceResult::getFacilityBusinessId)
	            .filter(Objects::nonNull)
	            .distinct()
	            .count();

        long accountsAwaitingPayment = periodResults.stream()
                .filter(entry -> entry.getValue().getPaymentStatus() == BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .map(Map.Entry::getKey)
                .distinct()
                .count();

	    long distinctAwaitingPaymentFacilityCount = periodResults.stream()
	            .map(Map.Entry::getValue)
	            .filter(result -> result.getPaymentStatus() == BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
	            .map(BuyOutSurplusFacilityPerformanceResult::getFacilityBusinessId)
	            .filter(Objects::nonNull)
	            .distinct()
	            .count();

        long accountsAwaitingRefund = periodResults.stream()
                .filter(entry -> entry.getValue().getPaymentStatus() == BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .map(Map.Entry::getKey)
                .distinct()
                .count();

	    long distinctAwaitingRefundFacilityCount = periodResults.stream()
	            .map(Map.Entry::getValue)
	            .filter(result -> result.getPaymentStatus() == BuyOutSurplusPaymentStatus.AWAITING_REFUND)
	            .map(BuyOutSurplusFacilityPerformanceResult::getFacilityBusinessId)
	            .filter(Objects::nonNull)
	            .distinct()
	            .count();
	    
	    return BuyOutSurplusFacilityRunSummaryEntry.builder()
	            .targetPeriod(targetPeriod.getBusinessId())
	            .buyOutCost(targetPeriod.getBuyOutCost())
	            .totalFacilities(distinctFacilityCount)
	            .totalBuyOutTransactions(accountsAwaitingPayment)
	            .totalBuyOutFacilities(distinctAwaitingPaymentFacilityCount)
	            .totalRefundTransactions(accountsAwaitingRefund)
	            .totalRefundFacilities(distinctAwaitingRefundFacilityCount)
	            .build();
	}

	@Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED_PAYLOAD)")
	BuyOutSurplusFacilityRunCompletedRequestActionPayload toBuyOutSurplusFacilityRunCompletedRequestActionPayload(
			BuyOutSurplusFacilityRunRequestPayload requestPayload);

}
