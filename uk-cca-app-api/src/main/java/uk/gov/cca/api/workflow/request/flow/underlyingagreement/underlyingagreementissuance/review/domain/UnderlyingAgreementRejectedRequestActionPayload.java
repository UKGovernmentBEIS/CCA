package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementReviewedRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UnderlyingAgreementRejectedRequestActionPayload extends UnderlyingAgreementReviewedRequestActionPayload {

}
