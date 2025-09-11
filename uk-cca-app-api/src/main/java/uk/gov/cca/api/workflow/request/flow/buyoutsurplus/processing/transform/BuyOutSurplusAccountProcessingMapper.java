package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionCreateDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutCalculatedDetails;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusDetails;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.SurplusCalculatedDetails;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.TP6BuyOutSurplusAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {PerformanceDataSubmissionType.class})
public interface BuyOutSurplusAccountProcessingMapper {

    default TP6BuyOutSurplusAccountProcessingSubmittedRequestActionPayload toSubmittedAction(BuyOutSurplusAccountProcessingRequestPayload requestPayload,
                                                                                             BuyOutSurplusAccountProcessingRequestMetadata metadata) {
        return switch (requestPayload.getPerformanceData().getTpOutcome()) {
            case BUY_OUT_REQUIRED -> toTP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload(requestPayload, metadata);
            case TARGET_MET -> toTP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload(requestPayload, metadata);
            default -> null;
        };
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)")
    @Mapping(target = "details", expression = "java(toBuyOutSurplusDetails(requestPayload, metadata))")
    @Mapping(target = "buyOutCalculatedDetails", expression = "java(toBuyOutCalculatedDetails(requestPayload))")
    TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload toTP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload(
            BuyOutSurplusAccountProcessingRequestPayload requestPayload, BuyOutSurplusAccountProcessingRequestMetadata metadata);

    @Mapping(target = "priBuyOutCarbon", expression = "java(requestPayload.getPerformanceData().getPriBuyOutCarbon().setScale(2, java.math.RoundingMode.HALF_UP))")
    @Mapping(target = "priBuyOutCost", source = "requestPayload.buyOutSurplus.buyOutSurplusContainer.priBuyOutCost")
    @Mapping(target = "previousPaidFees", source = "requestPayload.buyOutSurplus.buyOutSurplusContainer.invoicedPreviousPaidFees")
    @Mapping(target = "buyOutFee", source = "requestPayload.buyOutSurplus.buyOutSurplusContainer.invoicedBuyOutFee")
    BuyOutCalculatedDetails toBuyOutCalculatedDetails(BuyOutSurplusAccountProcessingRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)")
    @Mapping(target = "details", expression = "java(toBuyOutSurplusDetails(requestPayload, metadata))")
    @Mapping(target = "surplusCalculatedDetails", expression = "java(toSurplusCalculatedDetails(requestPayload))")
    TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload toTP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload(
            BuyOutSurplusAccountProcessingRequestPayload requestPayload, BuyOutSurplusAccountProcessingRequestMetadata metadata);

    @Mapping(target = "surplusGained", source = "requestPayload.buyOutSurplus.buyOutSurplusContainer.invoicedSurplusGained")
    @Mapping(target = "previousPaidFees", source = "requestPayload.buyOutSurplus.buyOutSurplusContainer.invoicedPreviousPaidFees")
    @Mapping(target = "overPaymentFee", source = "requestPayload.buyOutSurplus.buyOutSurplusContainer.invoicedBuyOutFee")
    SurplusCalculatedDetails toSurplusCalculatedDetails(BuyOutSurplusAccountProcessingRequestPayload requestPayload);

    @Mapping(target = "targetPeriodType", source = "requestPayload.targetPeriodDetails.businessId")
    @Mapping(target = "performanceDataReportVersion", source = "requestPayload.performanceData.reportVersion")
    @Mapping(target = "submissionType", source = "requestPayload.performanceData.submissionType")
    @Mapping(target = "tpOutcome", source = "requestPayload.performanceData.tpOutcome")
    @Mapping(target = "paymentStatus", source = "requestPayload.buyOutSurplus.paymentStatus")
    @Mapping(target = "transactionCode", source = "requestPayload.buyOutSurplus.transactionCode")
    @Mapping(target = "officialNotice", source = "requestPayload.officialNotice")
    @Mapping(target = "dueDate", source = "requestPayload.buyOutSurplus.buyOutSurplusContainer.invoicedPaymentDeadline")
    @Mapping(target = "runId", source = "metadata.parentRequestId")
    BuyOutSurplusDetails toBuyOutSurplusDetails(
            BuyOutSurplusAccountProcessingRequestPayload requestPayload, BuyOutSurplusAccountProcessingRequestMetadata metadata);

    @Mapping(target = "buyOutFee", source = "buyOutSurplusContainer.invoicedBuyOutFee")
    BuyOutSurplusTransactionCreateDTO toBuyOutSurplusTransactionCreateDTO(BuyOutSurplusResult result);
}
