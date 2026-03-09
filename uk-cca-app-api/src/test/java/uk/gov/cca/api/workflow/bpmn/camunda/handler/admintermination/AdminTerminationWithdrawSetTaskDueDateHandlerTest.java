package uk.gov.cca.api.workflow.bpmn.camunda.handler.admintermination;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaRequestExpirationKey;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

@ExtendWith(MockitoExtension.class)
class AdminTerminationWithdrawSetTaskDueDateHandlerTest {

    @InjectMocks
    private AdminTerminationWithdrawSetTaskDueDateHandler handler;

    @Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "1";
        final Date date = new Date();
        final LocalDate expirationDate = date.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.ADMIN_TERMINATION_EXPIRATION_DATE)).thenReturn(date);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.ADMIN_TERMINATION_EXPIRATION_DATE);
        verify(requestTaskTimeManagementService, times(1))
                .setDueDateToTasks(requestId, CcaRequestExpirationKey.ADMIN_TERMINATION, expirationDate);
    }
}
