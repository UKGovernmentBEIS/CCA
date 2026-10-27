package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum BuyOutSurplusPaymentStatus {

    UNDER_APPEAL("Under appeal", 1),
    AWAITING_PAYMENT("Awaiting payment", 2),
    AWAITING_REFUND("Awaiting refund", 3),
    PAID("Paid", 4),
    REFUNDED("Refunded", 5),
    NOT_REQUIRED("Not Required", 6),
    TERMINATED("Terminated", 7);

    private final String description;
    private final int priority;

    public static Set<BuyOutSurplusPaymentStatus> getCompletedPayments() {
        return Set.of(PAID, REFUNDED);
    }

    public static Set<BuyOutSurplusPaymentStatus> getAwaitingPayments() {
        return Set.of(AWAITING_PAYMENT, AWAITING_REFUND);
    }

    public static Set<BuyOutSurplusPaymentStatus> getPaymentStatusesByChargeType(BuyOutSurplusChargeType chargeType) {
        return (chargeType.equals(BuyOutSurplusChargeType.FEE)) ?
                getPaymentStatusesForBuyOutFee() : getPaymentStatusesForBuyOutOverpayment();
    }

    public static Set<BuyOutSurplusPaymentStatus> getExpectedPaymentStatusesByChargeType(BuyOutSurplusChargeType chargeType) {
        return chargeType.equals(BuyOutSurplusChargeType.FEE)
                ? Set.of(AWAITING_PAYMENT, PAID, UNDER_APPEAL)
                : Set.of(AWAITING_REFUND, REFUNDED);
    }

    private static Set<BuyOutSurplusPaymentStatus> getPaymentStatusesForBuyOutFee() {
        return Set.of(AWAITING_PAYMENT, PAID, NOT_REQUIRED, UNDER_APPEAL);
    }

    private static Set<BuyOutSurplusPaymentStatus> getPaymentStatusesForBuyOutOverpayment() {
        return Set.of(AWAITING_REFUND, REFUNDED, NOT_REQUIRED);
    }

}
