package uk.gov.cca.api.buyoutsurplus.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusContainer {

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal invoicedBuyOutFee;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal invoicedSurplusGained;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal invoicedPreviousPaidFees;

    private LocalDate invoicedPaymentDeadline;
}
