package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusTransactionDetailCreate {
    @NotNull
    private Long performanceDataId;

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal buyOutFee;
    
    private Long calculationId;

    @NotNull
    private BuyOutSurplusPaymentStatus paymentStatus;

    @NotNull
    @Valid
    private BuyOutSurplusContainer buyOutSurplusContainer;
}
