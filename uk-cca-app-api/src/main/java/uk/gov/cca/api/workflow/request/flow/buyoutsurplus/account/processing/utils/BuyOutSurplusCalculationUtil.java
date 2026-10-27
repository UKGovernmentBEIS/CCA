package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.common.domain.TriFunction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.utils.BuyOutSurplusCalculationCommonUtil;

@UtilityClass
public class BuyOutSurplusCalculationUtil {

    public BuyOutSurplusResult initializePrimaryResult(PerformanceDataBuyOutSurplusDetailsDTO details, LocalDate invoicedPaymentDeadline) {
        BuyOutSurplusPaymentStatus paymentStatus = BuyOutSurplusCalculationCommonUtil.getPaymentStatusByFee(details.getPriBuyOutCost());
        BuyOutSurplusChargeType chargeType = BuyOutSurplusCalculationCommonUtil.findChargeTypeByPaymentStatus(paymentStatus);

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
                        .build())
                .invoicedPaymentDeadline(BuyOutSurplusChargeType.FEE.equals(chargeType) ? invoicedPaymentDeadline : null)
                .chargeType(chargeType)
                .paymentStatus(paymentStatus)
                .build();
    }

    public BuyOutSurplusResult initializeSecondaryResult(PerformanceDataBuyOutSurplusDetailsDTO details, List<BuyOutSurplusTransactionDetailInfoDTO> previousPayments,
                                                         TargetPeriodInfoDTO targetPeriodDetails, LocalDate secondaryDeadline) {
        // Secondary Buy-Out Fee = (A - S) x Cost -  B
        // A =  Total target period Buy-out required (tCO2e) (cell E128) which equivalent represents the amount by which the emissions for the target period exceed the target
        // B = Cumulative, marked as PAID, Buy-out fees from all previous Primary and Secondary TPR submissions, as calculated by the previous BUY-OUT Batch Runs related to the specific TP.
        // S = for TP6 the surplus previous used would always be 0
        BigDecimal previousPaid = TOTAL_PREVIOUS_PAID_FEES
                .apply(previousPayments);

        BigDecimal secondaryBuyOutFee = SECONDARY_BUY_OUT_FEE.apply(
                details.getTotalPriBuyOutCarbon(), details.getBankedSurplus(), targetPeriodDetails.getBuyOutCost());

        BigDecimal buyOutFee = secondaryBuyOutFee.subtract(previousPaid).setScale(2, RoundingMode.HALF_UP);
        BuyOutSurplusPaymentStatus paymentStatus = BuyOutSurplusCalculationCommonUtil.getPaymentStatusByFee(buyOutFee);
        BuyOutSurplusChargeType chargeType = BuyOutSurplusCalculationCommonUtil.findChargeTypeByPaymentStatus(paymentStatus);

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
                        .build())
                .invoicedPaymentDeadline(BuyOutSurplusChargeType.FEE.equals(chargeType) ? secondaryDeadline : null)
                .chargeType(chargeType)
                .paymentStatus(paymentStatus)
                .build();
    }

    // Previous paid fees
    private final Function<List<BuyOutSurplusTransactionDetailInfoDTO>, BigDecimal> TOTAL_PREVIOUS_PAID_FEES =
            previousPayments ->
                BuyOutSurplusCalculationCommonUtil.calculatePreviousPayments(
                        previousPayments,
                        BuyOutSurplusTransactionDetailInfoDTO::getPaymentStatus,
                        BuyOutSurplusTransactionDetailInfoDTO::getBuyOutFee
                );

    // (A - S) x Cost
    private final TriFunction<BigDecimal, BigDecimal, BigDecimal, BigDecimal> SECONDARY_BUY_OUT_FEE =
            (totalPriBuyOutCarbon, bankedSurplus, targetPeriodCost) ->
                    (totalPriBuyOutCarbon.subtract(bankedSurplus, MathContext.DECIMAL128))
                            .multiply(targetPeriodCost, MathContext.DECIMAL128);
}
