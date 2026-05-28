package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealTribunalDecision;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.service.NonComplianceAppealOutcomeService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.transform.NonComplianceAppealOutcomeMapper;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.validation.NonComplianceAppealOutcomeSubmitValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceAppealOutcomeCompleteActionHandlerTest {

    @InjectMocks
    private NonComplianceAppealOutcomeCompleteActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private NonComplianceAppealOutcomeService nonComplianceAppealOutcomeService;

    @Mock
    private NonComplianceAppealOutcomeSubmitValidator validator;

    private final NonComplianceAppealOutcomeMapper NON_COMPLIANCE_APPEAL_OUTCOME_MAPPER = Mappers.getMapper(NonComplianceAppealOutcomeMapper.class);

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processId = "process";
        final AppUser appUser = AppUser.builder().build();
        final RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder().build();
        final UUID fileUuid = UUID.randomUUID();
        final String regulatorAssignee = appUser.getUserId();
        final NonComplianceAppealOutcomeDetails appealOutcome = NonComplianceAppealOutcomeDetails.builder()
                .file(fileUuid)
                .appealOutcomeDate(LocalDate.now())
                .tribunalDecision(NonComplianceAppealTribunalDecision.APPEAL_ALLOWED)
                .comments("bla bla")
                .build();
        final NonComplianceAppealOutcomeSubmitRequestTaskPayload requestTaskPayload = NonComplianceAppealOutcomeSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_APPEAL_OUTCOME_SUBMIT_PAYLOAD)
                .appealOutcome(appealOutcome)
                .build();

        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .appealOutcome(appealOutcome)
                .nonComplianceAttachments(Map.of(fileUuid, "file"))
                .regulatorAssignee(regulatorAssignee)
                .build();

        final NonComplianceAppealOutcomeSubmittedRequestActionPayload expectedActionPayload = NonComplianceAppealOutcomeSubmittedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_APPEAL_OUTCOME_SUBMITTED_PAYLOAD)
                .appealOutcome(appealOutcome)
                .nonComplianceAttachments(Map.of(fileUuid, "file"))
                .build();

        final NonComplianceAppealOutcomeSubmittedRequestActionPayload actionPayload =
                NON_COMPLIANCE_APPEAL_OUTCOME_MAPPER.toNonComplianceAppealOutcomeSubmittedRequestActionPayload(requestPayload);

        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        RequestTaskPayload taskPayload =
                handler.process(requestTaskId, CcaRequestTaskActionType.NON_COMPLIANCE_APPEAL_OUTCOME_COMPLETE_APPLICATION, appUser, taskActionEmptyPayload);

        // Verify
        assertThat(taskPayload).isEqualTo(requestTaskPayload);
        assertThat(actionPayload).isEqualTo(expectedActionPayload);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(validator, times(1)).validate(requestTaskPayload);
        verify(nonComplianceAppealOutcomeService, times(1)).complete(requestTask);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, CcaRequestActionType.NON_COMPLIANCE_APPEAL_OUTCOME_SUBMITTED, appUser.getUserId());
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_APPEAL_OUTCOME_COMPLETE_APPLICATION);
    }
}
