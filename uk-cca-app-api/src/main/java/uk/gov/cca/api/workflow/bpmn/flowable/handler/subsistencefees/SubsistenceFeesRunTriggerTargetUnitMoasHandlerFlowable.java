package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service.TargetUnitMoaCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunTriggerTargetUnitMoasHandlerFlowable implements JavaDelegate {

	private final TargetUnitMoaCreateRequestService targetUnitMoaCreateRequestService;
	
	@Override
	public void execute(DelegateExecution execution) {
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);
		targetUnitMoaCreateRequestService.createRequest(accountId, requestId, requestBusinessKey);
	}
}
