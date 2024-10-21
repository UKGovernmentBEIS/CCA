package uk.gov.cca.api.underlyingagreement.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UnderlyingAgreementValidationHelper {

	public BigDecimal calculateTarget(BigDecimal baseline, BigDecimal improvement) {
    	return baseline.multiply(BigDecimal.ONE.subtract(improvement.divide(BigDecimal.valueOf(100)))).setScale(7, RoundingMode.HALF_UP);
    }
}
