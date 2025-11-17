package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.service.AuditTrackCorrectiveActionsService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation.AuditTrackCorrectiveActionsCompleteValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditTrackCorrectiveActionsCompleteActionHandlerTest {

    @InjectMocks
    private AuditTrackCorrectiveActionsCompleteActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AuditTrackCorrectiveActionsCompleteValidator auditTrackCorrectiveActionsCompleteValidator;

    @Mock
    private AuditTrackCorrectiveActionsService auditTrackCorrectiveActionsService;

    @Test
    void process() {
        final String requestId = "ADS_1-F00008-AUDT-1";
        final Long requestTaskId = 1L;
        final AppUser user = AppUser.builder().build();
        final Request request = Request.builder().id(requestId).build();
        final AuditTrackCorrectiveActionsRequestTaskPayload requestTaskPayload = AuditTrackCorrectiveActionsRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(request)
                .payload(requestTaskPayload)
                .build();
        final String requestTaskActionType = CcaRequestTaskActionType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_COMPLETE_APPLICATION;
        final RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder().build();
        final AuditTrackCorrectiveActionsSubmittedRequestActionPayload requestActionPayload =
                AuditTrackCorrectiveActionsSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED_PAYLOAD)
                        .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // invoke
        handler.process(requestTaskId, requestTaskActionType, user, taskActionEmptyPayload);

        // verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(auditTrackCorrectiveActionsCompleteValidator, times(1)).validate(requestTaskPayload);
        verify(auditTrackCorrectiveActionsService, times(1)).applyCompleteAction(requestTask);
        verify(requestService, times(1)).addActionToRequest(request, requestActionPayload,
                CcaRequestActionType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED, user.getUserId());
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId()));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_COMPLETE_APPLICATION);
    }
}
