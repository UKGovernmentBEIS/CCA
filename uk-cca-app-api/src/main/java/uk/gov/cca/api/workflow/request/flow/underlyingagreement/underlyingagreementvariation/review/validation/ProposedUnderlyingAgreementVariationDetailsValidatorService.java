package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.validation;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;

@Service
public class ProposedUnderlyingAgreementVariationDetailsValidatorService extends UnderlyingAgreementVariationDetailsValidatorService<UnderlyingAgreementVariationReviewRequestTaskPayload> {

    public ProposedUnderlyingAgreementVariationDetailsValidatorService(DataValidator<UnderlyingAgreementVariationDetails> validator) {
        super(validator);
    }

    @Override
    public UnderlyingAgreementVariationPayload getUnderlyingAgreementPayload(UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload) {
        return taskPayload.getProposedUnderlyingAgreement();
    }

    @Override
    public String getPayloadType() {
        return UnderlyingAgreementPayloadType.PROPOSED.toString();
    }
}
