package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.CcaDecisionNotificationValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementReviewNotifyOperatorValidator {

    private final CcaDecisionNotificationValidator decisionNotificationValidator;
    private final UnderlyingAgreementReviewValidatorService underlyingAgreementReviewValidatorService;
    private final UnderlyingAgreementReviewDecisionDataValidator underlyingAgreementReviewDecisionDataValidator;

    public void validate(final RequestTask requestTask,
                         final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload,
                         final AppUser appUser) {

        final List<BusinessValidationResult> validationResults =
                new ArrayList<>(underlyingAgreementReviewValidatorService.validateEditedUnderlyingAgreement(requestTask));

        validationResults.addAll(underlyingAgreementReviewValidatorService.validateProposedUnderlyingAgreement(requestTask));

        validationResults.add(underlyingAgreementReviewDecisionDataValidator.validateReviewDecisionData(requestTask));

        validationResults.add(decisionNotificationValidator
                .validateDecisionNotification(requestTask, payload.getDecisionNotification(), appUser));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_REVIEW, ValidatorHelper.extractViolations(validationResults));
        }
    }

}
