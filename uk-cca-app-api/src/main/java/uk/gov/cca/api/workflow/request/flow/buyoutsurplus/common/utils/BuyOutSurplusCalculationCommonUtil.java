package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.ObjectUtils;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;

@UtilityClass
public class BuyOutSurplusCalculationCommonUtil {
	
	public BuyOutSurplusPaymentStatus getPaymentStatusByFee(BigDecimal buyOutFee) {
	    if(buyOutFee.compareTo(BigDecimal.ZERO) > 0) {
	        return BuyOutSurplusPaymentStatus.AWAITING_PAYMENT;
	    } else if(buyOutFee.compareTo(BigDecimal.ZERO) < 0) {
	        return BuyOutSurplusPaymentStatus.AWAITING_REFUND;
	    } else {
	        return null;
	    }
	}
	
	public BuyOutSurplusChargeType findChargeTypeByPaymentStatus(BuyOutSurplusPaymentStatus paymentStatus) {
	    if(ObjectUtils.isEmpty(paymentStatus)) {
	        return null;
	    }
	
	    return switch (paymentStatus) {
	        case AWAITING_PAYMENT -> BuyOutSurplusChargeType.FEE;
	        case AWAITING_REFUND -> BuyOutSurplusChargeType.REFUND;
	        default -> null;
	    };
	}
	
    public static <T> BigDecimal calculatePreviousPayments(List<T> payments,
            Function<T, BuyOutSurplusPaymentStatus> statusExtractor, 
            Function<T, BigDecimal> feeExtractor) {

        return payments.stream()
                .filter(payment -> BuyOutSurplusPaymentStatus.getCompletedPayments().contains(statusExtractor.apply(payment)))
                .map(payment -> {
                    BigDecimal fee = feeExtractor.apply(payment);
                    return statusExtractor.apply(payment).equals(BuyOutSurplusPaymentStatus.PAID) ? fee : fee.negate();
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
