package uk.gov.cca.api.workflow.request.flow.admintermination.submit.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTerminationSubmitNotifyOperatorValidator {

    private final AdminTerminationSubmitValidator adminTerminationSubmitValidator;
    private final AdminTerminationSubmitDecisionNotificationValidator adminTerminationSubmitDecisionNotificationValidator;

    public void validate(final RequestTask requestTask, final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload,
                         final AppUser appUser) {
        final AdminTerminationSubmitRequestTaskPayload taskPayload =
                (AdminTerminationSubmitRequestTaskPayload) requestTask.getPayload();

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate submit data
        validationResults.add(adminTerminationSubmitValidator.validate(taskPayload));

        // Validate decision notification
        validationResults.add(adminTerminationSubmitDecisionNotificationValidator
                .validate(requestTask, payload.getDecisionNotification(), appUser));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_ADMIN_TERMINATION, ValidatorHelper.extractViolations(validationResults));
        }
    }
}
