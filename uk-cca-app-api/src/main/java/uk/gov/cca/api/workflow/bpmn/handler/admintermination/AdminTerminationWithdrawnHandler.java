package uk.gov.cca.api.workflow.bpmn.handler.admintermination;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.service.AdminTerminationWithdrawSubmittedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AdminTerminationWithdrawnHandler implements JavaDelegate {

    private final AdminTerminationWithdrawSubmittedService adminTerminationWithdrawSubmittedService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        adminTerminationWithdrawSubmittedService.submit(requestId);
	}
}
