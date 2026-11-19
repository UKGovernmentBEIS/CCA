package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusTransactionCreate {
    
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
    
    @NotEmpty
    List<@Valid @NotNull BuyOutSurplusTransactionDetailCreate> details;
}
