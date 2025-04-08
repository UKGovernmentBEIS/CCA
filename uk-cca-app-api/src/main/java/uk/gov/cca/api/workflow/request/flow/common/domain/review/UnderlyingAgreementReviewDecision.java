package uk.gov.cca.api.workflow.request.flow.common.domain.review;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecision;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementReviewDecision extends CcaReviewDecision {

	@Valid
	private UnderlyingAgreementReviewDecisionDetails details;
}
