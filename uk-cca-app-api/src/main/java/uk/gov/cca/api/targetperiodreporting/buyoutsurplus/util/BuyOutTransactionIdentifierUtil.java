package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.util;

import lombok.experimental.UtilityClass;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@UtilityClass
public class BuyOutTransactionIdentifierUtil {

    public String generate(TargetPeriodType targetPeriodType, Long sequenceNumber) {
        return String.format("CCA%02d%04d", targetPeriodType.getNumber(), sequenceNumber);
    }
}
