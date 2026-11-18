package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusTransactionCreateDTO {

    @NotNull
    private Long performanceDataId;

    @NotBlank
    private String transactionCode;

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal buyOutFee;

    @NotNull
    private BuyOutSurplusPaymentStatus paymentStatus;

    @NotNull
    @Valid
    private BuyOutSurplusContainer buyOutSurplusContainer;

    @NotBlank
    private String fileDocumentUuid;
}
