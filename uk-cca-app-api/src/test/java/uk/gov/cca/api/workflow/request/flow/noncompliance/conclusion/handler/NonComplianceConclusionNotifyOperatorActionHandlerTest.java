package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.handler;

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
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusion;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonCompliancePenaltyOutcomeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.service.NonComplianceConclusionSubmitService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.validation.NonComplianceConclusionNotifyOperatorValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceConclusionNotifyOperatorActionHandlerTest {

    @InjectMocks
    private NonComplianceConclusionNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private NonComplianceConclusionSubmitService nonComplianceConclusionSubmitService;

    @Mock
    private NonComplianceConclusionNotifyOperatorValidator validator;

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processId = "process";
        final String requestTaskActionType = CcaRequestTaskActionType.NON_COMPLIANCE_CONCLUSION_NOTIFY_OPERATOR;
        final AppUser appUser = AppUser.builder().build();
        final UUID fileUuid = UUID.randomUUID();

        final DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operator")).build();
        final NotifyOperatorForDecisionRequestTaskActionPayload requestTaskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .payloadType(CcaRequestTaskActionPayloadType.NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                .build();

        final NonComplianceConclusion nonComplianceConclusion = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.WITHDRAW)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(null)
                .build();
        final NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = NonComplianceConclusionSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD)
                .nonComplianceConclusion(nonComplianceConclusion)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .build();

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
        verify(nonComplianceConclusionSubmitService, times(1)).notifyOperator(requestTask, decisionNotification);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.SUBMITTED,
                        CcaBpmnProcessConstants.IS_NON_COMPLIANCE_REISSUE_PENALTY_NEEDED, false));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_CONCLUSION_NOTIFY_OPERATOR);
    }
}