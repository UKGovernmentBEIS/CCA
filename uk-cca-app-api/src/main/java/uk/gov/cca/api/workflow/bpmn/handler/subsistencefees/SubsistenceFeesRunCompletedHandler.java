package uk.gov.cca.api.workflow.bpmn.handler.subsistencefees;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service.SubsistenceFeesRunCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunCompletedHandler implements JavaDelegate {

	private final SubsistenceFeesRunCompletedService subsistenceFeesRunCompletedService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		subsistenceFeesRunCompletedService.completeSubsistenceFeesRun(requestId);
	}
}
