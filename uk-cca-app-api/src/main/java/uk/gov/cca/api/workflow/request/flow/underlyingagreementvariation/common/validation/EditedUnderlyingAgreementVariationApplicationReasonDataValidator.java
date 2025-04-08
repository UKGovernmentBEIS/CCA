package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;

@Service
public class EditedUnderlyingAgreementVariationApplicationReasonDataValidator extends UnderlyingAgreementVariationApplicationReasonDataValidator<UnderlyingAgreementVariationRequestTaskPayload> {
    @Override
    public UnderlyingAgreementVariationPayload getUnderlyingAgreementPayload(UnderlyingAgreementVariationRequestTaskPayload taskPayload) {
        return taskPayload.getEditedUnderlyingAgreement();
    }

    @Override
    public String getPayloadType() {
        return UnderlyingAgreementPayloadType.EDITED.toString();
    }
}
