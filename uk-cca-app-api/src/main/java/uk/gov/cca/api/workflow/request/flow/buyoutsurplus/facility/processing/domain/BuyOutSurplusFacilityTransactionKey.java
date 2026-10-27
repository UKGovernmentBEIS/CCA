package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain;

import java.util.Objects;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

public record BuyOutSurplusFacilityTransactionKey(TargetPeriodType targetPeriodType,
        BuyOutSurplusChargeType chargeType) {
    public BuyOutSurplusFacilityTransactionKey {
        Objects.requireNonNull(targetPeriodType, "targetPeriodType");
        Objects.requireNonNull(chargeType, "chargeType");
    }
}