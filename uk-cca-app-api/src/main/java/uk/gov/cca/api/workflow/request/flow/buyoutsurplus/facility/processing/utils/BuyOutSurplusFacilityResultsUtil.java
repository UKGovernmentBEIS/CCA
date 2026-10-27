package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.utils.BuyOutSurplusCalculationCommonUtil;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;

@UtilityClass
public class BuyOutSurplusFacilityResultsUtil {
	
    public void markTPRsAsFailed(String facilityBusinessId,
            Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprs, 
            String message,
            List<BuyOutSurplusFacilityPerformanceResult> results) {

        tprs.forEach((targetPeriod, tpr) -> {
            results.removeIf(result -> Objects.equals(result.getFacilityBusinessId(), facilityBusinessId)
                    && result.getTargetPeriodType() == targetPeriod);

            results.add(BuyOutSurplusFacilityPerformanceResult.builder()
                    .facilityBusinessId(facilityBusinessId)
                    .targetPeriodType(targetPeriod)
                    .performanceDataReportVersion(tpr.getReportVersion())
                    .performanceDataId(tpr.getPerformanceDataId())
                    .succeeded(false)
                    .errors(List.of(message))
                    .build());
        });
    }
    
    public BuyOutSurplusFacilityPerformanceResult initializeFacilityPerformanceResult(
            FacilityPerformanceDataWithBuyOutProcessedStatus details, 
            BigDecimal targetPeriodBuyOutCost) {

        return BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityId(details.getFacilityId())
                .facilityBusinessId(details.getFacilityBusinessId())
                .siteName(details.getSiteName())
                .targetPeriodId(details.getTargetPeriodId())
                .targetPeriodType(details.getTargetPeriodType())
                .performanceDataReportVersion(details.getReportVersion())
                .performanceDataId(details.getPerformanceDataId())
                .performanceDataSubmissionDate(details.getSubmissionDate().toLocalDate())
                .submissionType(details.getSubmissionType())
                .processed(details.isProcessed())
                .targetPeriodBuyOutCost(targetPeriodBuyOutCost)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(details.getPerformanceOutcome())
                        .buyOutRequired(details.getBuyOutRequired())
                        .surplusGained(details.getSurplusGained())
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .build())
                .build();
    }
    
    public void updateSucceededResultForPrimaryPeriod (
            BigDecimal surplusUsed,
            BigDecimal surplusBucket,
            BuyOutSurplusPaymentStatus paymentStatus,
            BigDecimal invoicedBuyOutFee,
            BuyOutSurplusFacilityPerformanceResult result) {

        result.setSucceeded(true);
        result.setPaymentStatus(paymentStatus);
        result.getData().setChargeType(BuyOutSurplusCalculationCommonUtil.findChargeTypeByPaymentStatus(paymentStatus));
        result.getData().setSurplusUsed(surplusUsed);
        result.getData().setInvoicedBuyOutFee(invoicedBuyOutFee);
        result.getData().setSchemeTotalSurplus(surplusBucket);
        result.setPrimaryRulesApplied(true);
    }
    
    public void updateSucceededResultForSecondaryPeriod(
            BigDecimal previousPaidFees,
            BigDecimal previousSurplusUsed,
            BigDecimal previousSurplusGained,
            BigDecimal surplusBucket,
            BuyOutSurplusPaymentStatus paymentStatus,
            BigDecimal invoicedBuyOutFee,
            BuyOutSurplusFacilityPerformanceResult result) {
        
        result.setSucceeded(true);
        result.setPaymentStatus(paymentStatus);
        result.getData().setChargeType(BuyOutSurplusCalculationCommonUtil.findChargeTypeByPaymentStatus(paymentStatus));
        result.getData().setPreviousPaidFees(previousPaidFees);
        result.getData().setPreviousSurplusUsed(previousSurplusUsed);
        result.getData().setPreviousSurplusGained(previousSurplusGained);
        result.getData().setInvoicedBuyOutFee(invoicedBuyOutFee);
        result.getData().setSchemeTotalSurplus(surplusBucket);
    }
    
    public Map<TargetPeriodType, List<BuyOutSurplusFacilityPerformanceResult>> getBuyOutResultsPerTP(
            List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults) {
        return getResultsByChargeType(facilityPerformanceResults, BuyOutSurplusChargeType.FEE);
    }

    public Map<TargetPeriodType, List<BuyOutSurplusFacilityPerformanceResult>> getRefundResultsPerTP(
            List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults) {
        return getResultsByChargeType(facilityPerformanceResults, BuyOutSurplusChargeType.REFUND);
    }

    private Map<TargetPeriodType, List<BuyOutSurplusFacilityPerformanceResult>> getResultsByChargeType(
            List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults,
            BuyOutSurplusChargeType chargeType) {
        return facilityPerformanceResults.stream()
                .filter(result -> chargeType.equals(result.getData().getChargeType()))
                .collect(Collectors.groupingBy(BuyOutSurplusFacilityPerformanceResult::getTargetPeriodType));
    }
    
    public boolean isTransactionRequired(BuyOutSurplusFacilityPerformanceResult result) {
        return result.getData().getInvoicedBuyOutFee().compareTo(BigDecimal.ZERO) != 0;
    }
    
    public boolean requiresTransaction(List<BuyOutSurplusFacilityPerformanceResult> results) {
        return results.stream().anyMatch(BuyOutSurplusFacilityResultsUtil::isTransactionRequired);
    }
    
    public boolean shouldBankSurplus(BuyOutSurplusFacilityPerformanceResult result) {
        BuyOutSurplusCalculationDataContainer data = result.getData();
        return data.getPerformanceOutcome() == PerformanceDataFacilityTargetPeriodResultType.TARGET_MET
                || data.getSurplusUsed().compareTo(BigDecimal.ZERO) > 0
                || data.getPreviousSurplusUsed().compareTo(BigDecimal.ZERO) > 0
                || data.getPreviousSurplusGained().compareTo(BigDecimal.ZERO) > 0;
    }
}
