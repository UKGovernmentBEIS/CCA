package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;

@Service
public class EditedUnderlyingAgreementVariationDetailsValidatorService extends UnderlyingAgreementVariationDetailsValidatorService<UnderlyingAgreementVariationRequestTaskPayload> {
    public EditedUnderlyingAgreementVariationDetailsValidatorService(DataValidator<UnderlyingAgreementVariationDetails> validator) {
        super(validator);
    }

    @Override
    public UnderlyingAgreementVariationPayload getUnderlyingAgreementPayload(UnderlyingAgreementVariationRequestTaskPayload taskPayload) {
        return taskPayload.getEditedUnderlyingAgreement();
    }

    @Override
    public String getPayloadType() {
        return UnderlyingAgreementPayloadType.EDITED.toString();
    }
}
