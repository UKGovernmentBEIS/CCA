package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceOutcome;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNotice;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.service.EnforcementResponseNoticeSubmitService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.validation.EnforcementResponseNoticeNotifyOperatorValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceEnforcementResponseNoticeNotifyOperatorActionHandlerTest {

    @InjectMocks
    private NonComplianceEnforcementResponseNoticeNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private EnforcementResponseNoticeSubmitService enforcementResponseNoticeSubmitService;

    @Mock
    private EnforcementResponseNoticeNotifyOperatorValidator validator;

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processId = "process";
        final String requestTaskActionType = CcaRequestTaskActionType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_NOTIFY_OPERATOR;
        final AppUser appUser = AppUser.builder().build();
        final UUID fileUuid = UUID.randomUUID();

        final DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operator")).build();
        final NotifyOperatorForDecisionRequestTaskActionPayload requestTaskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .payloadType(CcaRequestTaskActionPayloadType.NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                .build();

        final NonComplianceEnforcementResponseNotice enforcementResponseNotice = NonComplianceEnforcementResponseNotice.builder()
                .type(NonComplianceEnforcementResponseNoticeType.PENALTY)
                .file(fileUuid)
                .comments("bla bla bla")
                .build();
        final NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload requestTaskPayload = NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD)
                .enforcementResponseNotice(enforcementResponseNotice)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .build();
        final boolean isPenaltyNoticeNeeded = requestTaskPayload.getEnforcementResponseNotice().getType().equals(NonComplianceEnforcementResponseNoticeType.PENALTY);

        final Request request = Request.builder().id(requestId).build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        RequestTaskPayload taskPayload = handler.process(requestTaskId, requestTaskActionType, appUser, requestTaskActionPayload);

        // Verify
        assertThat(taskPayload).isEqualTo(requestTaskPayload);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(validator, times(1)).validate(requestTask, requestTaskActionPayload, appUser);
        verify(enforcementResponseNoticeSubmitService, times(1)).notifyOperator(requestTask, decisionNotification);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.SUBMITTED,
                        CcaBpmnProcessConstants.IS_NON_COMPLIANCE_PENALTY_NOTICE_NEEDED, isPenaltyNoticeNeeded));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_NOTIFY_OPERATOR);
    }
}
