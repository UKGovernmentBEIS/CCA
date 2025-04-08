package uk.gov.cca.api.buyoutsurplus.util;

import lombok.experimental.UtilityClass;

import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;

@UtilityClass
public class BuyOutTransactionIdentifierUtil {

    public String generate(TargetPeriodType targetPeriodType, Long sequenceNumber) {
        return String.format("CCA%02d%04d", targetPeriodType.getNumber(), sequenceNumber);
    }
}
