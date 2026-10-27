package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.transform;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityTransactionSummary;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityPerformanceDetail;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BuyOutSurplusFacilityAccountProcessingMapper {

	@Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)")
	@Mapping(target = "runId", source = "metadata.parentRequestId")
	@Mapping(target = "summary", expression = "java(toTransactionsSummary(payload))")
	@Mapping(target = "facilities", expression = "java(toFacilities(payload, metadata))")
	BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload toSubmittedAction(
			BuyOutSurplusFacilityAccountProcessingRequestPayload payload,
			BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata);

	default List<BuyOutSurplusFacilityTransactionSummary> toTransactionsSummary(
	        BuyOutSurplusFacilityAccountProcessingRequestPayload payload) {

	    Map<TargetPeriodType, Map<BuyOutSurplusChargeType, List<BuyOutSurplusFacilityPerformanceResult>>> resultsPerTargetPeriodAndChargeType =
	        payload.getFacilityPerformanceResults().stream()
	            .filter(result -> result.getData().getChargeType() != null)
	            .collect(Collectors.groupingBy(
	                BuyOutSurplusFacilityPerformanceResult::getTargetPeriodType,
	                Collectors.groupingBy(
	                        res -> res.getData().getChargeType())));
	    
	    if (resultsPerTargetPeriodAndChargeType.isEmpty()) {
	        return Collections.emptyList();
	    }

        return payload.getTargetPeriodsDetails().stream()
                .filter(targetPeriod -> resultsPerTargetPeriodAndChargeType.containsKey(targetPeriod.getBusinessId()))
                .flatMap(targetPeriod -> toTransactionsSummaryForTargetPeriod(targetPeriod, resultsPerTargetPeriodAndChargeType, payload).stream())
                .toList();
	}

	default List<BuyOutSurplusFacilityTransactionSummary> toTransactionsSummaryForTargetPeriod(
	        TargetPeriodInfoDTO targetPeriod,
	        Map<TargetPeriodType, Map<BuyOutSurplusChargeType, List<BuyOutSurplusFacilityPerformanceResult>>> resultsPerTargetPeriodAndChargeType,
	        BuyOutSurplusFacilityAccountProcessingRequestPayload payload) {

	    Map<BuyOutSurplusChargeType, List<BuyOutSurplusFacilityPerformanceResult>> byChargeType =
	        resultsPerTargetPeriodAndChargeType.getOrDefault(targetPeriod.getBusinessId(), Collections.emptyMap());

	    return byChargeType.entrySet().stream()
	        .map(entry -> toTransactionSummary(targetPeriod, entry.getKey(), entry.getValue(), payload))
	        .toList();
	}
	
	default BuyOutSurplusFacilityTransactionSummary toTransactionSummary(
	        TargetPeriodInfoDTO targetPeriod,
	        BuyOutSurplusChargeType chargeType,
	        List<BuyOutSurplusFacilityPerformanceResult> results,
	        BuyOutSurplusFacilityAccountProcessingRequestPayload payload) {

	    BigDecimal invoicedBuyOutFee = results.stream()
	        .map(res -> res.getData().getInvoicedBuyOutFee())
	        .filter(Objects::nonNull)
	        .reduce(BigDecimal.ZERO, BigDecimal::add);

	    return BuyOutSurplusFacilityTransactionSummary.builder()
	        .targetPeriodType(targetPeriod.getBusinessId())
	        .chargeType(chargeType)
	        .targetPeriodBuyOutCost(targetPeriod.getBuyOutCost())
	        .totalFacilities((long) results.size())
	        .invoicedBuyOutFee(invoicedBuyOutFee)
	        .officialNotice(getOfficialNotice(targetPeriod.getBusinessId(), chargeType, payload))
	        .dueDate(BuyOutSurplusChargeType.FEE.equals(chargeType) ? getDueDate(targetPeriod.getBusinessId(), payload) : null)
	        .build();
	}
	
	default FileInfoDTO getOfficialNotice(
	        TargetPeriodType targetPeriod,
	        BuyOutSurplusChargeType chargeType,
	        BuyOutSurplusFacilityAccountProcessingRequestPayload payload) {

	    return switch (chargeType) {
	        case FEE -> payload.getBuyOutOfficialNotices().get(targetPeriod);
	        case REFUND -> payload.getRefundOfficialNotices().get(targetPeriod);
	    };
	}

	default LocalDate getDueDate(TargetPeriodType targetPeriod, BuyOutSurplusFacilityAccountProcessingRequestPayload payload) {
	    return payload.getInvoicedPaymentDeadlinePerTP().get(targetPeriod);
	}
	
	default List<BuyOutSurplusFacilityPerformanceDetail> toFacilities(
	        BuyOutSurplusFacilityAccountProcessingRequestPayload payload,
	        BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata) {

	    return payload.getFacilityPerformanceResults().stream()
	        .map(result -> toFacilityPerformanceDetails(
	            result, getTransactionCode(result, metadata)))
	        .toList();
	}
	
	@Mapping(target = "transactionCode", source = "transactionCode")
	BuyOutSurplusFacilityPerformanceDetail toFacilityPerformanceDetails(
	        BuyOutSurplusFacilityPerformanceResult result, String transactionCode);
	
	default String getTransactionCode(
		    BuyOutSurplusFacilityPerformanceResult result,
		    BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata) {
		
		if (result.getData().getChargeType() == null) {
	        return null;
	    }
		
		return switch (result.getData().getChargeType()) {
		        case FEE -> metadata.getBuyOutTransactionCodes().get(result.getTargetPeriodType());
		        case REFUND -> metadata.getRefundTransactionCodes().get(result.getTargetPeriodType());
		    };
		}
	
	BuyOutSurplusCalculationDetail toBuyOutSurplusCalculationDetail(BuyOutSurplusFacilityPerformanceResult result, Long accountId);
	
	BuyOutSurplusCalculationDataContainer copyData(BuyOutSurplusCalculationDataContainer dataContainer);
	
	BuyOutSurplusTransactionDetailInfoDTO toBuyOutSurplusTransactionDetailSummary(BuyOutSurplusTransactionDetailSummary summary);
}
