package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#type eq 'ACCEPTED') == (#changeStartDate != null)}", 
		message = "underlyingagreement.review.changeStartDate")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#changeStartDate) == (#startDate != null)}", 
		message = "underlyingagreement.review.startDate")
public class UnderlyingAgreementFacilityReviewDecision extends UnderlyingAgreementReviewDecision {

	private Boolean changeStartDate;
	
	private LocalDate startDate;
}
