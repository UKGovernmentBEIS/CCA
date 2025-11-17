package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation.FacilityAuditViolation;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditTrackCorrectiveActionsSubmitValidator {

    private final DataValidator<AuditCorrectiveActionResponse> correctiveActionResponseDataValidator;
    private final AuditTrackCorrectiveActionsFollowUpResponsesValidator auditTrackCorrectiveActionsFollowUpResponsesValidator;

    public void validate(RequestTask requestTask, AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload actionPayload) {

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        AuditTrackCorrectiveActionsRequestTaskPayload taskPayload =
                (AuditTrackCorrectiveActionsRequestTaskPayload) requestTask.getPayload();

        validationResults.add(validateResponseData(taskPayload, actionPayload.getActionTitle()));

        validationResults.add(auditTrackCorrectiveActionsFollowUpResponsesValidator.validateResponseReference(taskPayload, actionPayload.getActionTitle()));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_FACILITY_AUDIT, ValidatorHelper.extractViolations(validationResults));
        }
    }

    private BusinessValidationResult validateResponseData(final AuditTrackCorrectiveActionsRequestTaskPayload taskPayload, final String actionTitle) {
        List<FacilityAuditViolation> violations = new ArrayList<>();

        // Validate data
        if (ObjectUtils.isEmpty(taskPayload.getAuditTrackCorrectiveActions()) ||
                ObjectUtils.isEmpty(taskPayload.getAuditTrackCorrectiveActions().getCorrectiveActionResponses())) {
            violations.add(new FacilityAuditViolation(AuditTrackCorrectiveActions.class.getName(),
                    FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_TRACK_CORRECTIVE_ACTIONS_DATA));
        } else {
            AuditCorrectiveActionResponse auditCorrectiveActionResponse =
                    taskPayload.getAuditTrackCorrectiveActions().getCorrectiveActionResponses().get(actionTitle);
            correctiveActionResponseDataValidator.validate(auditCorrectiveActionResponse)
                    .map(businessViolation ->
                            new FacilityAuditViolation(AuditTrackCorrectiveActions.class.getName(),
                                    FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_TRACK_CORRECTIVE_ACTIONS_DATA,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
