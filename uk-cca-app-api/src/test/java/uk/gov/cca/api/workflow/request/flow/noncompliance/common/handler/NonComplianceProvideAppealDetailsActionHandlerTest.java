package uk.gov.cca.api.workflow.request.flow.noncompliance.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceAppealDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceProvideAppealDetailsRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceOutcome;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceAppealDetailsService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceAppealDetailsSubmitValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceProvideAppealDetailsActionHandlerTest {

    @InjectMocks
    private NonComplianceProvideAppealDetailsActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private NonComplianceAppealDetailsService nonComplianceAppealDetailsService;

    @Mock
    private NonComplianceAppealDetailsSubmitValidator validator;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processTaskId = UUID.randomUUID().toString();
        AppUser appUser = new AppUser();
        NonComplianceProvideAppealDetailsRequestTaskActionPayload payload = NonComplianceProvideAppealDetailsRequestTaskActionPayload.builder()
                .appealDetails(NonComplianceAppealDetails.builder()
                        .files(Set.of(UUID.randomUUID()))
                        .registrationDate(LocalDate.now().minusDays(1))
                        .comments("bla bla bla")
                        .build())
                .build();
        Map<String, Object> variables =
                Map.of(CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.APPEAL_OUTCOME_REQUIRED);
        NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = NonComplianceConclusionSubmitRequestTaskPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
                .payload(requestTaskPayload)
                .request(Request.builder().id("NCOM-1").build())
                .build();
        requestTask.setProcessTaskId(processTaskId);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, CcaRequestTaskActionType.NON_COMPLIANCE_PROVIDE_APPEAL_DETAILS, appUser, payload);

        verify(nonComplianceAppealDetailsService, times(1)).applyAppealAction(payload, requestTask);
        verify(validator, times(1)).validate(requestTaskPayload);
        verify(nonComplianceAppealDetailsService, times(1)).submitAppealAction(requestTask);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(workflowService, times(1)).completeTask(processTaskId, variables);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_PROVIDE_APPEAL_DETAILS);
    }
}
