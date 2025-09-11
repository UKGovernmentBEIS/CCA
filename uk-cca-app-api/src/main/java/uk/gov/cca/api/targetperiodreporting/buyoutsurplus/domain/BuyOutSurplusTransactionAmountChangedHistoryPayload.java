package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BuyOutSurplusTransactionAmountChangedHistoryPayload extends BuyOutSurplusTransactionHistoryPayload {

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal amount;
}
