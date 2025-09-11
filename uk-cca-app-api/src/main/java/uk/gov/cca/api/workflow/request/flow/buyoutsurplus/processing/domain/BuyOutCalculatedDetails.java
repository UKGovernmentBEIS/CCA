package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutCalculatedDetails {

    // Performance data priBuyOutCarbon
    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal priBuyOutCarbon;

    // Performance data priBuyOutCost
    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal priBuyOutCost;

    // Calculated
    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal previousPaidFees;

    // Secondary buy out fee or secondary overpayment buy out fee
    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal buyOutFee;
}
