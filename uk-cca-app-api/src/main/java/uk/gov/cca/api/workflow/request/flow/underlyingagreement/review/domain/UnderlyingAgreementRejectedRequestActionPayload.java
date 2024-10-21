package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UnderlyingAgreementRejectedRequestActionPayload extends UnderlyingAgreementReviewedRequestActionPayload {

}
