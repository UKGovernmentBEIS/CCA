package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SpELExpression(expression = "{#status == null || #status == 'PAID' || #status == 'REFUNDED'}",
        message = "buyOutSurplus.transaction.status.invalid")
@SpELExpression(expression = "{!((#status == 'PAID') == (#paymentDate == null))}",
        message = "buyOutSurplus.transaction.paymentDate")
public class BuyOutSurplusTransactionMarkPaymentStatusDTO {

    @NotNull
    private BuyOutSurplusPaymentStatus status;

    @PastOrPresent
    private LocalDate paymentDate;

    @Size(max = 10000)
    private String comments;

    @Builder.Default
    private Map<UUID, String> evidenceFiles = new HashMap<>();
}
