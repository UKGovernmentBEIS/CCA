package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation.FacilityAuditViolation;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AuditTrackCorrectiveActionsFollowUpResponsesValidator {

    public BusinessValidationResult validateCompletedResponses(final AuditTrackCorrectiveActionsRequestTaskPayload taskPayload) {
        List<FacilityAuditViolation> violations = new ArrayList<>();
        // Validate data
        AuditTrackCorrectiveActions auditTrackCorrectiveActions = taskPayload.getAuditTrackCorrectiveActions();
        if (ObjectUtils.isEmpty(auditTrackCorrectiveActions)) {
            violations.add(new FacilityAuditViolation(AuditTrackCorrectiveActions.class.getName(),
                    FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_TRACK_CORRECTIVE_ACTIONS_DATA));
        } else {
            // Validate that all corrective actions have been responded
            Set<String> correctiveActions = taskPayload.getAuditTrackCorrectiveActions().getCorrectiveActionResponses().keySet();
            Set<String> respondedItems = taskPayload.getRespondedActions();
            Set<String> unRespondedActions = SetUtils.difference(correctiveActions, respondedItems);

            if (!unRespondedActions.isEmpty()) {
                violations.add(new FacilityAuditViolation(AuditTrackCorrectiveActions.class.getName(),
                        FacilityAuditViolation.FacilityAuditViolationMessage.MISSING_CORRECTIVE_ACTION_RESPONSES));
            }
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    public BusinessValidationResult validateResponseReference(final AuditTrackCorrectiveActionsRequestTaskPayload taskPayload, final String reference) {
        List<FacilityAuditViolation> violations = new ArrayList<>();
        // Validate data
        final Map<String, AuditCorrectiveActionResponse> correctiveActionResponses =
                taskPayload.getAuditTrackCorrectiveActions().getCorrectiveActionResponses();

        if (!correctiveActionResponses.containsKey(reference)) {
            violations.add(new FacilityAuditViolation(AuditTrackCorrectiveActions.class.getName(),
                    FacilityAuditViolation.FacilityAuditViolationMessage.REFERENCE_NOT_FOUND));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
