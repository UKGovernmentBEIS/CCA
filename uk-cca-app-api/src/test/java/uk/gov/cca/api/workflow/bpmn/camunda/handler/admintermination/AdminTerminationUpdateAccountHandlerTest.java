package uk.gov.cca.api.workflow.bpmn.camunda.handler.admintermination;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.service.AdminTerminationFinalisedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationUpdateAccountHandlerTest {
	
    @InjectMocks
    private AdminTerminationUpdateAccountHandler handler;
    
    @Mock
    private DelegateExecution execution;
    
    @Mock
    AdminTerminationFinalisedService adminTerminationFinalisedService;

    @Test
    void execute() throws Exception {
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(adminTerminationFinalisedService, times(1)).terminateAccountAndOpenWorkflows(requestId);

    }
}