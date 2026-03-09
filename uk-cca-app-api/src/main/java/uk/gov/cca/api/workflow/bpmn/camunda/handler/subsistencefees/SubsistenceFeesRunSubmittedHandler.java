package uk.gov.cca.api.workflow.bpmn.camunda.handler.subsistencefees;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service.SubsistenceFeesRunSubmittedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunSubmittedHandler implements JavaDelegate {

	private final SubsistenceFeesRunSubmittedService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		service.subsistenceFeesRunSubmitted((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
