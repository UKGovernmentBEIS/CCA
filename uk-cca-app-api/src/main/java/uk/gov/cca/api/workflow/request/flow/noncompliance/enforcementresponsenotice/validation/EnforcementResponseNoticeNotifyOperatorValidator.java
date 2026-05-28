package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.DecisionNotificationValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EnforcementResponseNoticeNotifyOperatorValidator {

    private final EnforcementResponseNoticeSubmitValidator enforcementResponseNoticeSubmitValidator;
    private final DecisionNotificationValidator decisionNotificationValidator;

    public void validate(final RequestTask requestTask, final NotifyOperatorForDecisionRequestTaskActionPayload payload,
                         final AppUser appUser) {
        final NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload taskPayload =
                (NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload) requestTask.getPayload();

        final List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate submit data
        validationResults.add(enforcementResponseNoticeSubmitValidator.validate(taskPayload));

        // Validate decisionNotification data
        validationResults.add(decisionNotificationValidator.validate(requestTask, payload.getDecisionNotification(), appUser));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_NON_COMPLIANCE, ValidatorHelper.extractViolations(validationResults));
        }
    }
}
