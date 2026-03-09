package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import static org.mockito.Mockito.never;
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
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationRunInitiateHandlerFlowableTest {

	@InjectMocks
    private Cca2TerminationRunInitiateHandlerFlowable handler;

    @Mock
    private Cca2TerminationRunInitiateService service;
    
    @Mock
    private Cca2TerminationWorkflowConfig cca2TerminationWorkflowConfig;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
    	
    	when(service.isValidForCca2TerminationRun()).thenReturn(true);
    	when(cca2TerminationWorkflowConfig.getAccountBusinessIds()).thenReturn(List.of());
    	
        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.CCA2_TERMINATION_RUN_INITIATE_FLAG, true);
        verify(cca2TerminationWorkflowConfig, times(1)).getAccountBusinessIds();
        verify(service, times(1)).isValidForCca2TerminationRun();
    }
    
    @Test
    void execute_account_ids_provided() {
    	
    	when(cca2TerminationWorkflowConfig.getAccountBusinessIds()).thenReturn(List.of("ADS-123"));
    	
        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.CCA2_TERMINATION_RUN_INITIATE_FLAG, true);
        verify(cca2TerminationWorkflowConfig, times(1)).getAccountBusinessIds();
        verify(service, never()).isValidForCca2TerminationRun();
    }
}
