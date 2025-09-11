package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.validation;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationApplicationReasonDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;

@Service
public class ProposedUnderlyingAgreementVariationApplicationReasonDataValidator extends UnderlyingAgreementVariationApplicationReasonDataValidator<UnderlyingAgreementVariationReviewRequestTaskPayload> {
    @Override
    public UnderlyingAgreementVariationPayload getUnderlyingAgreementPayload(UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload) {
        return taskPayload.getUnderlyingAgreementProposed();
    }

    @Override
    public String getPayloadType() {
        return UnderlyingAgreementPayloadType.PROPOSED.toString();
    }
}
