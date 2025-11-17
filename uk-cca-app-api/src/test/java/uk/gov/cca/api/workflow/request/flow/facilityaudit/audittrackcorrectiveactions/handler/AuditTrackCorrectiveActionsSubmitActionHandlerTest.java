package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.service.AuditTrackCorrectiveActionsService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation.AuditTrackCorrectiveActionsSubmitValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditTrackCorrectiveActionsSubmitActionHandlerTest {

    @InjectMocks
    private AuditTrackCorrectiveActionsSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AuditTrackCorrectiveActionsSubmitValidator auditTrackCorrectiveActionsSubmitValidator;

    @Mock
    private AuditTrackCorrectiveActionsService auditTrackCorrectiveActionsService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final String requestId = "ADS_1-F00008-AUDT-1";
        final Long requestTaskId = 1L;
        final AppUser user = AppUser.builder().build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(Request.builder()
                        .id(requestId)
                        .build())
                .build();
        final String requestTaskActionType = CcaRequestTaskActionType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMIT_RESPONSE;
        final AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload taskActionPayload =
                AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMIT_PAYLOAD)
                        .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // invoke
        handler.process(requestTaskId, requestTaskActionType, user, taskActionPayload);

        // verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(auditTrackCorrectiveActionsSubmitValidator, times(1)).validate(requestTask, taskActionPayload);
        verify(auditTrackCorrectiveActionsService, times(1)).applySubmitAction(taskActionPayload, requestTask);
        verify(workflowService, times(1)).sendEvent(requestTask.getRequest().getId(),
                CcaBpmnProcessConstants.TIMER_RECALCULATED,
                Map.of());
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMIT_RESPONSE);
    }
}
