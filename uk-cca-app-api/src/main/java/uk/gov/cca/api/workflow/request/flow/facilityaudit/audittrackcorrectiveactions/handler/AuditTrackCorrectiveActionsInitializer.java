package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.handler;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuditTrackCorrectiveActionsInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final FacilityAuditRequestPayload requestPayload = (FacilityAuditRequestPayload) request.getPayload();
        final Set<CorrectiveAction> correctiveActions = requestPayload.getAuditDetailsAndCorrectiveActions().getCorrectiveActions().getActions();
        final Map<String, AuditCorrectiveActionResponse> correctiveActionResponses = correctiveActions.stream()
                .collect(Collectors.toMap(CorrectiveAction::getTitle,
                        action -> AuditCorrectiveActionResponse.builder().action(action).build(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
        return AuditTrackCorrectiveActionsRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.AUDIT_TRACK_CORRECTIVE_ACTIONS_PAYLOAD)
                .auditTrackCorrectiveActions(AuditTrackCorrectiveActions.builder()
                        .correctiveActionResponses(correctiveActionResponses)
                        .build())
                .sectionsCompleted(Map.of())
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.AUDIT_TRACK_CORRECTIVE_ACTIONS);
    }
}
