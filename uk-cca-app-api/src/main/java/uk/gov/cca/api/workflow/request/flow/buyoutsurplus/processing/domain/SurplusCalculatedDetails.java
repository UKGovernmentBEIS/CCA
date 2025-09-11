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
public class SurplusCalculatedDetails {

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal surplusGained;

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal previousPaidFees;

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal overPaymentFee;
}
