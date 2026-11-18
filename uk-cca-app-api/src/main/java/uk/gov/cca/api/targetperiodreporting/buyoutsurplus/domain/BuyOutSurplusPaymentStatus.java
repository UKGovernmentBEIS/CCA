package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum BuyOutSurplusPaymentStatus {

    AWAITING_PAYMENT("Awaiting payment"),
    AWAITING_REFUND("Awaiting refund"),
    PAID("Paid"),
    REFUNDED("Refunded"),
    NOT_REQUIRED("Not Required"),
    UNDER_APPEAL("Under appeal"),
    TERMINATED("Terminated");

    private final String description;

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

    private static Set<BuyOutSurplusPaymentStatus> getPaymentStatusesForBuyOutFee() {
        return Set.of(AWAITING_PAYMENT, PAID, NOT_REQUIRED, UNDER_APPEAL);
    }

    private static Set<BuyOutSurplusPaymentStatus> getPaymentStatusesForBuyOutOverpayment() {
        return Set.of(AWAITING_REFUND, REFUNDED, NOT_REQUIRED);
    }

}
