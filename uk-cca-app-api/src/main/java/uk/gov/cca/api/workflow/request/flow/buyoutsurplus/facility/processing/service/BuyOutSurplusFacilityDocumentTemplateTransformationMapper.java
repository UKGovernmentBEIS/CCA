package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityTransactionDetailTemplateData;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityTransactionTemplateData;
import uk.gov.netz.api.workflow.request.core.domain.Request;

@Component
public class BuyOutSurplusFacilityDocumentTemplateTransformationMapper {

    public Map<String, Object> constructTemplateParams(final Request request, 
            final TargetPeriodType targetPeriodType,
            final BuyOutSurplusChargeType chargeType,
            final Map<Long, FacilityAddressDTO> facilityAddressesById) {
        final BuyOutSurplusFacilityAccountProcessingRequestPayload payload = (BuyOutSurplusFacilityAccountProcessingRequestPayload) request.getPayload();
        final BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = (BuyOutSurplusFacilityAccountProcessingRequestMetadata) request.getMetadata();

        List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults = payload.getFacilityPerformanceResults().stream()
                .filter(res -> res.getData().getChargeType() == chargeType
                        && res.getTargetPeriodType().equals(targetPeriodType))
                .toList();
        
        List<BuyOutSurplusFacilityTransactionDetailTemplateData> transactionDetailTemplateDataList = new ArrayList<>(); 
        
        facilityPerformanceResults.forEach(dtl -> transactionDetailTemplateDataList.add(constructTransactionDetailTemplateData(dtl, facilityAddressesById.get(dtl.getFacilityId()))));
         
        
        final LocalDate dueDate = chargeType.equals(BuyOutSurplusChargeType.FEE)
                ? payload.getInvoicedPaymentDeadlinePerTP().get(targetPeriodType)
                : null;
        final String transactionCode = chargeType.equals(BuyOutSurplusChargeType.FEE)
                ? metadata.getBuyOutTransactionCodes().get(targetPeriodType)
                : metadata.getRefundTransactionCodes().get(targetPeriodType);
        BigDecimal invoicedBuyOutFee = transactionDetailTemplateDataList.stream()
                .map(BuyOutSurplusFacilityTransactionDetailTemplateData::getInvoicedBuyOutFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BuyOutSurplusFacilityTransactionTemplateData transactionTemplateData = BuyOutSurplusFacilityTransactionTemplateData.builder()
                .transactionCode(transactionCode)
                .invoicedBuyOutFee(invoicedBuyOutFee)
                .invoicedPaymentDeadline(dueDate)
                .targetPeriodType(targetPeriodType)
                .targetPeriodTypeNumber(targetPeriodType.getNumber())
                .build();

        return new HashMap<>(Map.of(
                "transaction", transactionTemplateData, 
                "transactionDetails", transactionDetailTemplateDataList));
    }
    
    private BuyOutSurplusFacilityTransactionDetailTemplateData constructTransactionDetailTemplateData(BuyOutSurplusFacilityPerformanceResult facilityResult, FacilityAddressDTO facilityAddressDTO) {
        
        return BuyOutSurplusFacilityTransactionDetailTemplateData.builder()
                .facilityBusinessId(facilityResult.getFacilityBusinessId())
                .siteName(facilityResult.getSiteName())
                .facilityAddress(constructFacilityAddressDTO(facilityAddressDTO))
                .buyOutRequired(facilityResult.getData().getBuyOutRequired())
                .surplusUsed(facilityResult.getData().getSurplusUsed())
                .invoicedBuyOutFee(facilityResult.getData().getInvoicedBuyOutFee())
                .performanceDataSubmissionDate(facilityResult.getPerformanceDataSubmissionDate())
                .previousPaidFees(facilityResult.getData().getPreviousPaidFees())
                .performanceDataBuyOutFee(facilityResult.getData().getBuyOutRequired().multiply(facilityResult.getTargetPeriodBuyOutCost()))
                .netShortFall(facilityResult.getData().getBuyOutRequired().subtract(facilityResult.getData().getSurplusUsed()))
                .surplusGained(facilityResult.getData().getSurplusGained())
                .build();
    }
    
    public String constructFacilityAddressDTO(FacilityAddressDTO address) {
        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(address.getLine1());
        Optional.ofNullable(address.getLine2())
                .ifPresent(line2 -> addressBuilder.append(", ").append(line2));
        addressBuilder.append(", ").append(address.getCity());
        Optional.ofNullable(address.getCounty())
                .ifPresent(county -> addressBuilder.append(", ").append(county));
        addressBuilder.append(", ").append(address.getPostcode());

        return addressBuilder.toString();
    }
}
