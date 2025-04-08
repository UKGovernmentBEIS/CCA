package uk.gov.cca.api.buyoutsurplus.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BuyOutPaymentStatus {

    AWAITING_PAYMENT("Awaiting payment"),
    AWAITING_REFUND("Awaiting refund"),
    PAID("Paid"),
    REFUNDED("Refunded"),
    NOT_REQUIRED("Not Required"),
    UNDER_APPEAL("Under appeal"),
    TERMINATED("Terminated");

    private final String description;
}
