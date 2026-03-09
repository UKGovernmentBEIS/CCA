package uk.gov.cca.api.workflow.bpmn.camunda.handler.admintermination;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.service.AdminTerminationFinalisedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AdminTerminationUpdateAccountHandler implements JavaDelegate {

	private final AdminTerminationFinalisedService adminTerminationFinalisedService;
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		adminTerminationFinalisedService.terminateAccountAndOpenWorkflows(requestId);
	}
}
