package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{#adjustmentAmount == null || (#adjustmentAmount.compareTo(0) < 0) || (#adjustmentAmount.compareTo(0) > 0) == (#paymentDate != null)}",
        message = "buyOutSurplus.transaction.paymentDate")
@SpELExpression(expression = "{#adjustmentAmount == null || (#adjustmentAmount.compareTo(0) != 0)}",
        message = "buyOutSurplus.transaction.adjustmentAmount")
public class BuyOutSurplusTransactionUpdateReceivedAmountDTO {

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal adjustmentAmount;

    @PastOrPresent
    private LocalDate paymentDate;

    @NotBlank
    @Size(max = 10000)
    private String comments;

    @Builder.Default
    private Map<UUID, String> evidenceFiles = new HashMap<>();
}
