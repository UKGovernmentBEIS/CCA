package uk.gov.cca.api.workflow.bpmn.handler.subsistencefees;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service.MoaCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class TargetUnitMoaCompletedHandler implements JavaDelegate {

	private final MoaCompletedService moaCompletedService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String tuMoaRequestId = (String) execution.getVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_ID);
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
		final Boolean tuMoaSucceeded = (Boolean) execution.getVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_SUCCEEDED);
		final List<String> targetUnitMoaErrors = (List<String>) execution.getVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_ERRORS);
		
		moaCompletedService.completed(requestId, accountId, MoaType.TARGET_UNIT_MOA, tuMoaRequestId, tuMoaSucceeded, targetUnitMoaErrors);
		
		// Increment completed number var
		final Integer numberOfAccountsCompleted = (Integer) execution.getVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED);
		execution.setVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, numberOfAccountsCompleted + 1);
	}		
}
