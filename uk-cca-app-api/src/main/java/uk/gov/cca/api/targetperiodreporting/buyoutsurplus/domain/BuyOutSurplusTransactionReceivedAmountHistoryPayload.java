package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@SpELExpression(expression = "{#adjustmentAmount == null || (#adjustmentAmount.compareTo(0) < 0) || (#adjustmentAmount.compareTo(0) > 0) == (#paymentDate != null)}",
        message = "buyOutSurplus.transaction.paymentDate")
@SpELExpression(expression = "{#adjustmentAmount == null || (#adjustmentAmount.compareTo(0) != 0)}",
        message = "buyOutSurplus.transaction.adjustmentAmount")
public class BuyOutSurplusTransactionReceivedAmountHistoryPayload extends BuyOutSurplusTransactionHistoryPayload {

    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @NotNull
    private BigDecimal adjustmentAmount;

    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @NotNull
    private BigDecimal previousReceivedAmount;

    @PastOrPresent
    private LocalDate paymentDate;
}
