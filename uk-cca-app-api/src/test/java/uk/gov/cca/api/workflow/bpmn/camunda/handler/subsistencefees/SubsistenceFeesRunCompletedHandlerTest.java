package uk.gov.cca.api.workflow.bpmn.camunda.handler.subsistencefees;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service.SubsistenceFeesRunCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunCompletedHandlerTest {

	@InjectMocks
    private SubsistenceFeesRunCompletedHandler handler;
	
	@Mock
	private SubsistenceFeesRunCompletedService subsistenceFeesRunCompletedService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
    	final String requestId = "request-id";
    	when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
    	
    	handler.execute(execution);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(subsistenceFeesRunCompletedService, times(1)).completeSubsistenceFeesRun(requestId);
    }
}
