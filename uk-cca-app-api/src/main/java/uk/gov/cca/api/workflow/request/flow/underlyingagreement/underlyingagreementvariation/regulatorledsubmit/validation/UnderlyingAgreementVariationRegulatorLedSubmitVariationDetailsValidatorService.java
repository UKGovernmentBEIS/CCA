package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;

@Service
public class UnderlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService
        extends UnderlyingAgreementVariationDetailsValidatorService<UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload> {

    public UnderlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService(DataValidator<UnderlyingAgreementVariationDetails> validator) {
        super(validator);
    }

    @Override
    public UnderlyingAgreementVariationPayload getUnderlyingAgreementPayload(UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload) {
        return taskPayload.getUnderlyingAgreement();
    }

    @Override
    public String getPayloadType() {
        return "";
    }
}
