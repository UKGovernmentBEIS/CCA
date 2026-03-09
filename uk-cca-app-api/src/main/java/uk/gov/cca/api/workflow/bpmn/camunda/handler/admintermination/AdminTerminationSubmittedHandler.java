package uk.gov.cca.api.workflow.bpmn.camunda.handler.admintermination;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.service.AdminTerminationSubmittedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AdminTerminationSubmittedHandler implements JavaDelegate {

	private final AdminTerminationSubmittedService adminTerminationSubmittedService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		adminTerminationSubmittedService.submit(requestId);
	}
}
