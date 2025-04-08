package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationOutcome;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service.AdminTerminationFinalDecisionService;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.validation.AdminTerminationFinalDecisionNotifyOperatorValidator;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationFinalDecisionNotifyOperatorActionHandlerTest {

    @InjectMocks
    private AdminTerminationFinalDecisionNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AdminTerminationFinalDecisionNotifyOperatorValidator adminTerminationFinalDecisionNotifyOperatorValidator;

    @Mock
    private AdminTerminationFinalDecisionService adminTerminationFinalDecisionService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processId = "process";
        final String requestTaskActionType = CcaRequestTaskActionType.ADMIN_TERMINATION_FINAL_DECISION_NOTIFY_OPERATOR_FOR_DECISION;
        final AppUser appUser = AppUser.builder().build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();

        final AdminTerminationFinalDecisionRequestTaskPayload requestTaskPayload = AdminTerminationFinalDecisionRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_FINAL_DECISION_PAYLOAD)
                .adminTerminationFinalDecisionReasonDetails(AdminTerminationFinalDecisionReasonDetails.builder()
                        .finalDecisionType(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        RequestTaskPayload taskPayload = handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        assertThat(taskPayload).isEqualTo(requestTaskPayload);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(adminTerminationFinalDecisionNotifyOperatorValidator, times(1))
                .validate(requestTask, taskActionPayload, appUser);
        verify(adminTerminationFinalDecisionService, times(1))
                .notifyOperator(requestTask, decisionNotification);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        CcaBpmnProcessConstants.ADMIN_TERMINATION_FINAL_DECISION, AdminTerminationOutcome.FINALISE));
    }

    @Test
    void process_withdraw() {
        final long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processId = "process";
        final String requestTaskActionType = CcaRequestTaskActionType.ADMIN_TERMINATION_FINAL_DECISION_NOTIFY_OPERATOR_FOR_DECISION;
        final AppUser appUser = AppUser.builder().build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .processTaskId(processId)
                .payload(AdminTerminationFinalDecisionRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_FINAL_DECISION_PAYLOAD)
                        .adminTerminationFinalDecisionReasonDetails(AdminTerminationFinalDecisionReasonDetails.builder()
                                .finalDecisionType(AdminTerminationFinalDecisionType.WITHDRAW_TERMINATION)
                                .build())
                        .build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(adminTerminationFinalDecisionNotifyOperatorValidator, times(1))
                .validate(requestTask, taskActionPayload, appUser);
        verify(adminTerminationFinalDecisionService, times(1))
                .notifyOperator(requestTask, decisionNotification);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        CcaBpmnProcessConstants.ADMIN_TERMINATION_FINAL_DECISION, AdminTerminationOutcome.WITHDRAW));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.ADMIN_TERMINATION_FINAL_DECISION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
