package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.cca2termination.run.service.Cca2TerminationRunRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class Cca2TerminationRunCompletedHandlerFlowable implements JavaDelegate {

	private final Cca2TerminationRunRequestService cca2TerminationRunRequestService;
	
	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		cca2TerminationRunRequestService.completeCca2TerminationRun(requestId);
	}
}
