package uk.gov.cca.api.buyoutsurplus.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.buyoutsurplus.domain.BuyOutPaymentStatus;
import uk.gov.cca.api.buyoutsurplus.domain.BuyOutSurplusContainer;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusDTO {

    @NotNull
    private Long performanceDataId;

    private String transactionId;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal buyOutFee;

    private BuyOutPaymentStatus paymentStatus;

    @NotNull
    @Valid
    private BuyOutSurplusContainer buyOutSurplusContainer;

    private String fileDocumentUuid;
}
