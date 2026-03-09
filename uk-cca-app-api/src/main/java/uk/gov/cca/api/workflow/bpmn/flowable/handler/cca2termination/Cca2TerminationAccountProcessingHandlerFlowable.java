package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.service.Cca2TerminationAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class Cca2TerminationAccountProcessingHandlerFlowable implements JavaDelegate {
	
	private final Cca2TerminationAccountProcessingService cca2TerminationAccountProcessingService;
	
	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final Cca2TerminationAccountState accountState = (Cca2TerminationAccountState) execution
                .getVariable(CcaBpmnProcessConstants.CCA2_TERMINATION_ACCOUNT_STATE);

        try {
        	cca2TerminationAccountProcessingService.doProcess(requestId, accountState);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            accountState.getErrors().add(e.getMessage());
        }
	}
}
