package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.config.Cca2TerminationWorkflowConfig;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.service.Cca2TerminationRunInitiateService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class Cca2TerminationRunInitiateHandlerFlowable implements JavaDelegate {

	private final Cca2TerminationRunInitiateService cca2TerminationRunInitiateService;
	private final Cca2TerminationWorkflowConfig cca2TerminationWorkflowConfig;

    @Override
    public void execute(DelegateExecution execution) {
    	boolean canBeInitiated = !cca2TerminationWorkflowConfig.getAccountBusinessIds().isEmpty() 
    			|| cca2TerminationRunInitiateService.isValidForCca2TerminationRun();
        execution.setVariable(CcaBpmnProcessConstants.CCA2_TERMINATION_RUN_INITIATE_FLAG, canBeInitiated);
    }
}
