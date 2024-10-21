package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationRejectedRequestActionPayload extends UnderlyingAgreementVariationReviewedRequestActionPayload {
}
