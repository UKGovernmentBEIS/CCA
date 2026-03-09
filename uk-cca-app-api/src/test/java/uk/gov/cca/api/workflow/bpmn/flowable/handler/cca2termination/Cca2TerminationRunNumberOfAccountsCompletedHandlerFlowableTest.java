package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca2termination.run.service.Cca2TerminationRunRequestService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationRunNumberOfAccountsCompletedHandlerFlowableTest {

	@InjectMocks
    private Cca2TerminationRunNumberOfAccountsCompletedHandlerFlowable handler;

    @Mock
    private Cca2TerminationRunRequestService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
    	final String requestId = "request-id";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
    	when(service.getNumberOfAccountsCompleted(requestId)).thenReturn(100L);
    	
        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 100L);
        verify(service, times(1)).getNumberOfAccountsCompleted(requestId);
    }
}
