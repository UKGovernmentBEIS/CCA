package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload extends BuyOutSurplusTransactionHistoryPayload {

    @NotNull
    private BuyOutSurplusPaymentStatus paymentStatus;

    @PastOrPresent
    private LocalDate paymentDate;
}
