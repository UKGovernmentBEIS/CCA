package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;


import jakarta.validation.constraints.NotEmpty;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SpELExpression(expression = "{!(#status == 'TERMINATED')}",
		message = "buyOutSurplus.transaction.status.invalid")
@SpELExpression(expression = "{!((#status == 'UNDER_APPEAL' || #status == 'NOT_REQUIRED') && #comments == null)}",
		message = "buyOutSurplus.transaction.comments.notEmpty")
@SpELExpression(expression = "{!((#status == 'PAID') == (#paymentDate == null))}",
		message = "buyOutSurplus.transaction.paymentDate")
@SpELExpression(expression = "{!((#status == 'UNDER_APPEAL') == (#dateOfAppeal == null))}",
		message = "buyOutSurplus.transaction.dateOfAppeal")
public class BuyOutSurplusTransactionUpdatePaymentStatusDTO {

	@NotNull
	private BuyOutSurplusPaymentStatus status;

	@Size(max = 10000)
	private String comments;

	@Builder.Default
	private Map<UUID, String> evidenceFiles = new HashMap<>();

	@PastOrPresent
	private LocalDate paymentDate;

	@PastOrPresent
	private LocalDate dateOfAppeal;

	@Builder.Default
	@NotEmpty
	private Set<Long> detailIds = new HashSet<>();
}
