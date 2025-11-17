package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSaveRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuditTrackCorrectiveActionsService {

    @Transactional
    public void applySaveAction(final AuditTrackCorrectiveActionsSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        AuditTrackCorrectiveActionsRequestTaskPayload taskPayload = (AuditTrackCorrectiveActionsRequestTaskPayload) requestTask.getPayload();
        Set<String> respondedActions = taskPayload.getRespondedActions();
        Map<String, AuditCorrectiveActionResponse> correctiveActionResponses =
                taskPayload.getAuditTrackCorrectiveActions().getCorrectiveActionResponses();
        correctiveActionResponses.get(payload.getActionTitle()).setResponse(payload.getCorrectiveActionFollowUpResponse());
        taskPayload.setSectionsCompleted(payload.getSectionsCompleted());
        // remove from responded actions
        respondedActions.remove(payload.getActionTitle());
    }

    @Transactional
    public void applySubmitAction(final AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload payload, RequestTask requestTask) {
        AuditTrackCorrectiveActionsRequestTaskPayload taskPayload =
                (AuditTrackCorrectiveActionsRequestTaskPayload) requestTask.getPayload();
        taskPayload.getRespondedActions().add(payload.getActionTitle());
    }

    @Transactional
    public void applyCompleteAction(RequestTask requestTask) {
        AuditTrackCorrectiveActionsRequestTaskPayload taskPayload = (AuditTrackCorrectiveActionsRequestTaskPayload) requestTask.getPayload();

        Request request = requestTask.getRequest();
        FacilityAuditRequestPayload requestPayload = (FacilityAuditRequestPayload) request.getPayload();

        requestPayload.setAuditTrackCorrectiveActions(taskPayload.getAuditTrackCorrectiveActions());
        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setAuditTrackCorrectiveActionsAttachments(taskPayload.getFacilityAuditAttachments());
    }
}
