package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusResult;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionInfoDTO;
import uk.gov.cca.api.common.domain.TriFunction;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public class BuyOutSurplusCalculationUtil {

    public BuyOutSurplusResult initializePrimaryResult(PerformanceDataBuyOutSurplusDetailsDTO details, TargetPeriodDTO targetPeriodDetails) {
        BuyOutSurplusPaymentStatus paymentStatus = getPaymentStatusByFee(details.getPriBuyOutCost());
        BuyOutSurplusChargeType chargeType = findChargeTypeByPaymentStatus(paymentStatus);
        LocalDate invoicedPaymentDeadline = getPaymentDeadline(PerformanceDataSubmissionType.PRIMARY, details.getPriBuyOutCost(), targetPeriodDetails, null);

        // Round properly
        BigDecimal buyOutFee = details.getPriBuyOutCost().setScale(2, RoundingMode.HALF_UP);
        BigDecimal surplusGained = details.getSurplusGained().setScale(0, RoundingMode.HALF_UP);
        BigDecimal previousPaid = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return BuyOutSurplusResult.builder()
                .performanceDataId(details.getPerformanceDataId())
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .invoicedBuyOutFee(buyOutFee)
                        .invoicedSurplusGained(surplusGained)
                        .priBuyOutCost(buyOutFee)
                        .invoicedPreviousPaidFees(previousPaid)
                        .invoicedPaymentDeadline(invoicedPaymentDeadline)
                        .chargeType(chargeType)
                        .build())
                .paymentStatus(paymentStatus)
                .build();
    }

    public BuyOutSurplusResult initializeSecondaryResult(PerformanceDataBuyOutSurplusDetailsDTO details, List<BuyOutSurplusTransactionInfoDTO> previousPayments,
                                                         TargetPeriodDTO targetPeriodDetails, LocalDate secondaryDeadline) {
        // Secondary Buy-Out Fee = (A - S) x Cost -  B
        // A =  Total target period Buy-out required (tCO2e) (cell E128) which equivalent represents the amount by which the emissions for the target period exceed the target
        // B = Cumulative, marked as PAID, Buy-out fees from all previous Primary and Secondary TPR submissions, as calculated by the previous BUY-OUT Batch Runs related to the specific TP.
        // S = for TP6 the surplus previous used would always be 0
        BigDecimal previousPaid = TOTAL_PREVIOUS_PAID_FEES
                .apply(previousPayments);

        BigDecimal secondaryBuyOutFee = SECONDARY_BUY_OUT_FEE.apply(
                details.getTotalPriBuyOutCarbon(), details.getBankedSurplus(), targetPeriodDetails.getBusinessId().getCostPerCarbon());

        BigDecimal buyOutFee = secondaryBuyOutFee.subtract(previousPaid).setScale(2, RoundingMode.HALF_UP);
        BuyOutSurplusPaymentStatus paymentStatus = getPaymentStatusByFee(buyOutFee);
        BuyOutSurplusChargeType chargeType = findChargeTypeByPaymentStatus(paymentStatus);
        LocalDate invoicedPaymentDeadline = getPaymentDeadline(PerformanceDataSubmissionType.SECONDARY, buyOutFee, targetPeriodDetails, secondaryDeadline);

        // Round properly
        BigDecimal surplusGained = details.getSurplusGained().setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalBuyoutFee = details.getPriBuyOutCost().setScale(2, RoundingMode.HALF_UP);
        previousPaid = previousPaid.setScale(2, RoundingMode.HALF_UP);

        return BuyOutSurplusResult.builder()
                .performanceDataId(details.getPerformanceDataId())
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .invoicedBuyOutFee(buyOutFee.abs())
                        .invoicedSurplusGained(surplusGained)
                        .priBuyOutCost(totalBuyoutFee)
                        .invoicedPreviousPaidFees(previousPaid)
                        .invoicedPaymentDeadline(invoicedPaymentDeadline)
                        .chargeType(chargeType)
                        .build())
                .paymentStatus(paymentStatus)
                .build();
    }

    // Previous paid fees
    private final Function<List<BuyOutSurplusTransactionInfoDTO>, BigDecimal> TOTAL_PREVIOUS_PAID_FEES =
            previousPayments ->
                previousPayments.stream()
                        .filter(payment ->
                                BuyOutSurplusPaymentStatus.getCompletedPayments().contains(payment.getPaymentStatus()))
                        .map(payment ->
                                payment.getPaymentStatus().equals(BuyOutSurplusPaymentStatus.PAID)
                                        ? payment.getBuyOutFee()
                                        : payment.getBuyOutFee().negate()
                        ).reduce(BigDecimal.ZERO, BigDecimal::add);

    // (A - S) x Cost
    private final TriFunction<BigDecimal, BigDecimal, BigDecimal, BigDecimal> SECONDARY_BUY_OUT_FEE =
            (totalPriBuyOutCarbon, bankedSurplus, targetPeriodCost) ->
                    (totalPriBuyOutCarbon.subtract(bankedSurplus, MathContext.DECIMAL128))
                            .multiply(targetPeriodCost, MathContext.DECIMAL128);

    private BuyOutSurplusPaymentStatus getPaymentStatusByFee(BigDecimal buyOutFee) {
        if(buyOutFee.compareTo(BigDecimal.ZERO) > 0) {
            return BuyOutSurplusPaymentStatus.AWAITING_PAYMENT;
        } else if(buyOutFee.compareTo(BigDecimal.ZERO) < 0) {
            return BuyOutSurplusPaymentStatus.AWAITING_REFUND;
        } else {
            return null;
        }
    }

    private LocalDate getPaymentDeadline(PerformanceDataSubmissionType submissionType, BigDecimal buyOutFee,
                                         TargetPeriodDTO targetPeriodDetails, LocalDate secondaryDeadline) {
        if(buyOutFee.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        else if(submissionType.equals(PerformanceDataSubmissionType.PRIMARY)) {
            // For a primary Buy-out fee, the payment deadline is a predefined/fixed date corresponding to the "Buy-out fee payment deadline"
            return targetPeriodDetails.getBuyOutEndDate();
        }
        else {
            // For a secondary Buy-out fee the payment deadline is dynamically calculated - 30 working days after the notice is sent.
            return secondaryDeadline;
        }
    }

    private BuyOutSurplusChargeType findChargeTypeByPaymentStatus(BuyOutSurplusPaymentStatus paymentStatus) {
        if(ObjectUtils.isEmpty(paymentStatus)) {
            return null;
        }

        return switch (paymentStatus) {
            case AWAITING_PAYMENT -> BuyOutSurplusChargeType.FEE;
            case AWAITING_REFUND -> BuyOutSurplusChargeType.REFUND;
            default -> null;
        };
    }
}
