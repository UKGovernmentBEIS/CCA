package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca2termination.common.config.Cca2TerminationWorkflowConfig;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.service.Cca2TerminationRunInitiateService;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationRunInitiateCreateHandlerFlowableTest {

	@InjectMocks
    private Cca2TerminationRunInitiateCreateHandlerFlowable handler;

    @Mock
    private Cca2TerminationRunInitiateService cca2TerminationRunInitiateService;
    
    @Mock
    private Cca2TerminationWorkflowConfig cca2TerminationWorkflowConfig;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
    	
    	when(cca2TerminationWorkflowConfig.getAccountBusinessIds()).thenReturn(List.of());
    	
        // Invoke
        handler.execute(execution);

        // Verify
        verify(cca2TerminationRunInitiateService, times(1)).createCca2TerminationRun(List.of());
        verify(cca2TerminationWorkflowConfig, times(1)).getAccountBusinessIds();
    }
}
