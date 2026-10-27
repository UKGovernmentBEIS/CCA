package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBuyOutSurplusTransactionCreate {

    @NotBlank
    private String transactionCode;

    @NotNull
    private Long accountId;

    @NotNull
    private TargetPeriodType targetPeriodType;

    @NotNull
    private BuyOutSurplusChargeType chargeType;

    private LocalDate invoicedPaymentDeadline;

    @NotBlank
    private String fileDocumentUuid;

    @NotNull
    private Long performanceDataId;

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal buyOutFee;

    @NotNull
    private BuyOutSurplusPaymentStatus paymentStatus;

    @NotNull
    @Valid
    private BuyOutSurplusContainer buyOutSurplusContainer;
}
