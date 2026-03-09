package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationApplicationReasonDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;

@Service
public class UnderlyingAgreementVariationRegulatorLedSubmitApplicationReasonDataValidator
        extends UnderlyingAgreementVariationApplicationReasonDataValidator<UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload> {

    @Override
    public UnderlyingAgreementVariationPayload getUnderlyingAgreementPayload(UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload) {
        return taskPayload.getUnderlyingAgreement();
    }

    @Override
    public String getPayloadType() {
        return "";
    }
}
