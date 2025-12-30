package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewedRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationCompletedRequestActionPayload extends UnderlyingAgreementVariationReviewedRequestActionPayload {
}
