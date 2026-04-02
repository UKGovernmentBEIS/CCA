package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.CcaDecisionNotificationValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationReviewDecisionDataValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewNotifyOperatorValidator {

    private final CcaDecisionNotificationValidator decisionNotificationValidator;
    private final UnderlyingAgreementVariationReviewDecisionDataValidator underlyingAgreementVariationReviewDecisionDataValidator;
    private final UnderlyingAgreementVariationReviewValidatorService underlyingAgreementVariationReviewValidatorService;

    public void validate(final RequestTask requestTask,
                         final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload,
                         final AppUser appUser) {

        List<BusinessValidationResult> validationResults =
                new ArrayList<>(underlyingAgreementVariationReviewValidatorService.validateEditedUnderlyingAgreement(requestTask));

        validationResults.addAll(underlyingAgreementVariationReviewValidatorService.validateProposedUnderlyingAgreement(requestTask));

        validationResults.add(underlyingAgreementVariationReviewDecisionDataValidator
                .validateReviewDecisionData(requestTask));

        validationResults.add(decisionNotificationValidator
                .validateDecisionNotification(requestTask, payload.getDecisionNotification(), appUser));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION_REVIEW, ValidatorHelper.extractViolations(validationResults));
        }
    }
}