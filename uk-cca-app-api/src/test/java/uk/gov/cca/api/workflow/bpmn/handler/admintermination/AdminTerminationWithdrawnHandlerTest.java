package uk.gov.cca.api.workflow.bpmn.handler.admintermination;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.service.AdminTerminationWithdrawSubmittedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminTerminationWithdrawnHandlerTest {

    @InjectMocks
    private AdminTerminationWithdrawnHandler handler;

    @Mock
    private AdminTerminationWithdrawSubmittedService adminTerminationWithdrawSubmittedService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(adminTerminationWithdrawSubmittedService, times(1)).submit(requestId);
    }
}
