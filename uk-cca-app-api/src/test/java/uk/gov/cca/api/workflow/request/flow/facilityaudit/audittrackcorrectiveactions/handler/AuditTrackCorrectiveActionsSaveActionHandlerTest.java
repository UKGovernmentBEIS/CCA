package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.CorrectiveActionFollowUpResponse;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.service.AuditTrackCorrectiveActionsService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation.AuditTrackCorrectiveActionsFollowUpResponsesValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditTrackCorrectiveActionsSaveActionHandlerTest {

    @InjectMocks
    private AuditTrackCorrectiveActionsSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AuditTrackCorrectiveActionsService auditTrackCorrectiveActionsService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AuditTrackCorrectiveActionsFollowUpResponsesValidator auditTrackCorrectiveActionsFollowUpResponsesValidator;

    @Test
    void process() {
        final String requestId = "ADS_1-F00008-AUDT-1";
        final Long requestTaskId = 1L;
        final AppUser user = AppUser.builder().build();
        final String correctiveActionTitle = "Corrective Action 1";
        final CorrectiveAction correctiveAction = CorrectiveAction.builder()
                .title(correctiveActionTitle)
                .details("bla bla")
                .deadline(LocalDate.now())
                .build();
        final CorrectiveActionFollowUpResponse correctiveActionFollowUpResponse = CorrectiveActionFollowUpResponse.builder()
                .isActionCarriedOut(false)
                .comments("bla bla bla bla")
                .build();
        final AuditCorrectiveActionResponse auditCorrectiveActionResponse = AuditCorrectiveActionResponse.builder()
                .action(correctiveAction)
                .response(correctiveActionFollowUpResponse)
                .build();
        final AuditTrackCorrectiveActions auditTrackCorrectiveActions = AuditTrackCorrectiveActions.builder()
                .correctiveActionResponses(Map.of())
                .build();
        final AuditTrackCorrectiveActionsRequestTaskPayload requestTaskPayload =
                AuditTrackCorrectiveActionsRequestTaskPayload.builder()
                        .auditTrackCorrectiveActions(auditTrackCorrectiveActions)
                        .respondedActions(Set.of(correctiveActionTitle))
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .payload(requestTaskPayload)
                .request(Request.builder()
                        .id(requestId)
                        .build())
                .build();
        final String requestTaskActionType = CcaRequestTaskActionType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SAVE_RESPONSE;
        final AuditTrackCorrectiveActionsSaveRequestTaskActionPayload actionPayload =
                AuditTrackCorrectiveActionsSaveRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SAVE_PAYLOAD)
                        .correctiveActionFollowUpResponse(auditCorrectiveActionResponse.getResponse())
                        .actionTitle(correctiveActionTitle)
                        .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(auditTrackCorrectiveActionsFollowUpResponsesValidator.validateResponseReference(requestTaskPayload, actionPayload.getActionTitle()))
                .thenReturn(BusinessValidationResult.builder().valid(true).violations(List.of()).build());

        // invoke
        handler.process(requestTaskId, requestTaskActionType, user, actionPayload);

        // verify
//        assertThat(requestTaskPayload.getAuditTrackCorrectiveActions().getCorrectiveActionResponses())
//                .isEqualTo(correctiveActionResponses);
//        assertThat(requestTaskPayload.getRespondedActions()).isEqualTo(Set.of(correctiveActionTitle));
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(auditTrackCorrectiveActionsService, times(1)).applySaveAction(actionPayload, requestTask);
        verify(workflowService, times(1)).sendEvent(requestTask.getRequest().getId(),
                CcaBpmnProcessConstants.TIMER_RECALCULATED,
                Map.of());
        verify(auditTrackCorrectiveActionsFollowUpResponsesValidator, times(1))
                .validateResponseReference(requestTaskPayload, actionPayload.getActionTitle());
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SAVE_RESPONSE);
    }
}
