package uk.gov.cca.api.workflow.request.flow.noncompliance.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceOutcome;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseJustification;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceCloseService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceCloseApplicationValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceCloseActionHandlerTest {

    @InjectMocks
    private NonComplianceCloseActionHandler handler;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NonComplianceCloseService nonComplianceCloseService;

    @Mock
    private NonComplianceCloseApplicationValidator validator;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processTaskId = UUID.randomUUID().toString();
        AppUser appUser = new AppUser();
        NonComplianceCloseRequestTaskActionPayload payload = NonComplianceCloseRequestTaskActionPayload.builder()
                .closeJustification(NonComplianceCloseJustification.builder().reason("bla bla bla").build())
                .build();
        Map<String, Object> variables =
                Map.of(CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.CLOSED);
        NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
                .payload(requestTaskPayload)
                .build();
        requestTask.setProcessTaskId(processTaskId);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, CcaRequestTaskActionType.NON_COMPLIANCE_CANCEL_APPLICATION, appUser, payload);

        verify(nonComplianceCloseService, times(1)).applyCloseAction(payload, requestTask);
        verify(validator, times(1)).validate(requestTaskPayload);
        verify(nonComplianceCloseService, times(1)).submitCloseAction(requestTask);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(workflowService, times(1)).completeTask(processTaskId, variables);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION);
    }
}
