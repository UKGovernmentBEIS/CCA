package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.utils;

import java.math.BigDecimal;
import java.util.List;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.utils.BuyOutSurplusCalculationCommonUtil;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityProcessingException;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.validation.BuyOutSurplusFacilityViolation;

@UtilityClass
public class BuyOutSurplusFacilityCalculationUtil {


	public void calculatePrimary(FacilityPerformanceDataWithBuyOutProcessedStatus performanceData, 
			TargetPeriodInfoDTO targetPeriodInfo, 
			BigDecimal surplusBucket,
			BuyOutSurplusFacilityPerformanceResult result) throws BuyOutSurplusFacilityProcessingException {

        BigDecimal buyOutRequired = performanceData.getBuyOutRequired();
        BigDecimal surplusGained = performanceData.getSurplusGained();
        
        BigDecimal surplusUsed = BigDecimal.ZERO;
        BigDecimal newSurplusBucket;
        BigDecimal invoicedBuyOutFee = BigDecimal.ZERO;
        BuyOutSurplusPaymentStatus paymentStatus = null;

        switch (performanceData.getPerformanceOutcome()) {
            case TARGET_MET:
            	newSurplusBucket = surplusBucket.add(surplusGained);
                break;
            case TARGET_NOT_MET:
				surplusUsed = buyOutRequired.compareTo(surplusBucket) > 0 ? surplusBucket : buyOutRequired;
				invoicedBuyOutFee = buyOutRequired.subtract(surplusUsed).multiply(targetPeriodInfo.getBuyOutCost());
				newSurplusBucket = surplusBucket.subtract(surplusUsed);
				paymentStatus = BuyOutSurplusCalculationCommonUtil.getPaymentStatusByFee(invoicedBuyOutFee);
                break;
            default:
            	throw new BuyOutSurplusFacilityProcessingException(
                        BuyOutSurplusFacilityViolation
                                .BuyOutSurplusFacilityViolationMessage
                                .INVALID_DATA
                                .getMessage());
        }

        BuyOutSurplusFacilityResultsUtil.updateSucceededResultForPrimaryPeriod(
                surplusUsed,
                newSurplusBucket,
                paymentStatus,
                invoicedBuyOutFee,
                result);
    }

    public void calculateSecondary(
            FacilityPerformanceDataWithBuyOutProcessedStatus performanceData,
            TargetPeriodInfoDTO targetPeriodInfo,
            List<BuyOutSurplusTransactionDetailSummary> transactions,
            BuyOutSurplusCalculationDetail previousBuyOutCalculation,
            BigDecimal availableSurplusBucket,
            BuyOutSurplusFacilityPerformanceResult result) throws BuyOutSurplusFacilityProcessingException {
    	
        BigDecimal previousPaidFees = calculatePreviousPayments(transactions);

        if (previousPaidFees.compareTo(BigDecimal.ZERO) < 0) {
            throw new BuyOutSurplusFacilityProcessingException(
                    BuyOutSurplusFacilityViolation
                            .BuyOutSurplusFacilityViolationMessage
                            .AMOUNT_REFUNDED_GREATER_THAN_AMOUNT_RECEIVED
                            .getMessage(targetPeriodInfo.getBusinessId()));
        }
        
        BigDecimal buyOutRequired = performanceData.getBuyOutRequired();
        BigDecimal surplusGained = performanceData.getSurplusGained();
        BigDecimal invoicedBuyOutFee = BigDecimal.ZERO;
        BuyOutSurplusPaymentStatus paymentStatus = null;
        
        boolean hasPreviousBuyOutCalculation = (previousBuyOutCalculation != null);
        BigDecimal previousSurplusGained =
        		hasPreviousBuyOutCalculation
                        ? previousBuyOutCalculation.getData().getSurplusGained()
                        : BigDecimal.ZERO;
        BigDecimal previousSurplusUsed =
        		hasPreviousBuyOutCalculation
                        ? previousBuyOutCalculation.getData().getSurplusUsed()
                        : BigDecimal.ZERO;
        BigDecimal newSurplusBucket = availableSurplusBucket
        		.add(surplusGained)
                .add(previousSurplusUsed)
                .subtract(previousSurplusGained);

        switch (performanceData.getPerformanceOutcome()) {
            case TARGET_MET:
                if (previousPaidFees.compareTo(BigDecimal.ZERO) > 0) {
                    paymentStatus = BuyOutSurplusPaymentStatus.AWAITING_REFUND;
                    invoicedBuyOutFee = previousPaidFees;
                }
                break;
            case TARGET_NOT_MET:
                BigDecimal buyOutFee = buyOutRequired.multiply(targetPeriodInfo.getBuyOutCost());

				if (buyOutFee.compareTo(previousPaidFees) > 0) {
					paymentStatus = BuyOutSurplusPaymentStatus.AWAITING_PAYMENT;
					invoicedBuyOutFee = buyOutFee.subtract(previousPaidFees);
				} else if (buyOutFee.compareTo(previousPaidFees) < 0) {
					paymentStatus = BuyOutSurplusPaymentStatus.AWAITING_REFUND;
					invoicedBuyOutFee = previousPaidFees.subtract(buyOutFee);
				}
                break;
            default:
            	throw new BuyOutSurplusFacilityProcessingException(
                        BuyOutSurplusFacilityViolation
                                .BuyOutSurplusFacilityViolationMessage
                                .INVALID_DATA
                                .getMessage());
        }

        BuyOutSurplusFacilityResultsUtil.updateSucceededResultForSecondaryPeriod(
                previousPaidFees,
                previousSurplusUsed,
                previousSurplusGained,
                newSurplusBucket,
                paymentStatus,
                invoicedBuyOutFee,
                result);
    }

	private BigDecimal calculatePreviousPayments(List<BuyOutSurplusTransactionDetailSummary> transactions) {
	    return BuyOutSurplusCalculationCommonUtil.calculatePreviousPayments(
	            transactions,
	            BuyOutSurplusTransactionDetailSummary::getPaymentStatus,
	            BuyOutSurplusTransactionDetailSummary::getBuyOutFee
	    );
	}
}
