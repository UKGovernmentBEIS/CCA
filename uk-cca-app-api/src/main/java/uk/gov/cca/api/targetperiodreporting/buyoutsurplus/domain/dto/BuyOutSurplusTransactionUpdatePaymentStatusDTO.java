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
@SpELExpression(expression = "{!((#status == 'UNDER_APPEAL' || #status == 'NOT_REQUIRED') && #comments == null)}",
		message = "buyOutSurplusTransaction.comments.notEmpty")
@SpELExpression(expression = "{!((#status == 'PAID') == (#paymentDate == null))}",
		message = "buyOutSurplusTransaction.paymentDate")
public class BuyOutSurplusTransactionUpdatePaymentStatusDTO {

	@NotNull
	private BuyOutSurplusPaymentStatus status;

	@Size(max = 10000)
	private String comments;

	@Builder.Default
	private Map<UUID, String> evidenceFiles = new HashMap<>();

	@PastOrPresent
	private LocalDate paymentDate;
}
