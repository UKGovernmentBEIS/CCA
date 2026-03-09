package uk.gov.cca.api.workflow.bpmn.camunda.handler.admintermination;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.SendReminderNotificationService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaNotificationTemplateWorkflowTaskType.ADMIN_TERMINATION;

@ExtendWith(MockitoExtension.class)
class AdminTerminationFirstReminderDateReachedHandlerTest {

    @InjectMocks
    private AdminTerminationFirstReminderDateReachedHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private DelegateExecution execution;

    @Mock
    private SendReminderNotificationService reminderNotificationService;

    @Test
    void execute() throws Exception {
        final String requestId = "1";
        final Date expirationDate = new Date();

        final AdminTerminationRequestPayload payload = AdminTerminationRequestPayload.builder()
                .regulatorAssignee("bbb2820b-cbc6-4923-b3f1-8de409ea34c1")
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.builder().code(ADMIN_TERMINATION.name()).build())
                .payload(payload).build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.ADMIN_TERMINATION_EXPIRATION_DATE)).thenReturn(expirationDate);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.ADMIN_TERMINATION_EXPIRATION_DATE);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(reminderNotificationService, times(1)).sendFirstReminderNotification(request, expirationDate, request.getPayload().getRegulatorAssignee());
    }
}
