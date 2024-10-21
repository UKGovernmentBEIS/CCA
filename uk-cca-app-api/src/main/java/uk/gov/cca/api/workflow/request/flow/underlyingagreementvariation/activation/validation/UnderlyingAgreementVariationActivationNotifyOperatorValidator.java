package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.DecisionNotificationValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivationNotifyOperatorValidator {

    private final UnderlyingAgreementVariationActivationSubmitValidator underlyingAgreementVariationActivationSubmitValidator;
    private final DecisionNotificationValidator underlyingAgreementVariationDecisionNotificationValidator;

    public void validate(final RequestTask requestTask, final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload,
                         final AppUser appUser) {
        final UnderlyingAgreementVariationActivationRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationActivationRequestTaskPayload) requestTask.getPayload();

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate submit data
        validationResults.add(underlyingAgreementVariationActivationSubmitValidator.validate(taskPayload));

        // Validate decision notification
        validationResults.add(underlyingAgreementVariationDecisionNotificationValidator
                .validateDecisionNotification(requestTask, payload.getDecisionNotification(), appUser));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);
        if(!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION_ACTIVATION, ValidatorHelper.extractViolations(validationResults));
        }
    }
}
