package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivationRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementActivationSubmitValidator {

    private final DataValidator<UnderlyingAgreementActivationDetails> validator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public BusinessValidationResult validate(final UnderlyingAgreementActivationRequestTaskPayload taskPayload) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        // Validate data
        if(ObjectUtils.isEmpty(taskPayload.getUnderlyingAgreementActivationDetails())) {
            violations.add(new UnderlyingAgreementViolation(UnderlyingAgreementActivationDetails.class.getName(),
                    UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_UNDERLYING_AGREEMENT_ACTIVATION_DETAILS_DATA));
        }
        else {
            validator.validate(taskPayload.getUnderlyingAgreementActivationDetails())
                    .map(businessViolation ->
                            new UnderlyingAgreementViolation(UnderlyingAgreementActivationDetails.class.getName(),
                                    UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_UNDERLYING_AGREEMENT_ACTIVATION_DETAILS_DATA,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);
        }

        // Validate files
        if(!fileAttachmentsExistenceValidator
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getUnderlyingAgreementActivationAttachments().keySet())) {
            violations.add(new UnderlyingAgreementViolation(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.ATTACHMENT_NOT_FOUND));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
