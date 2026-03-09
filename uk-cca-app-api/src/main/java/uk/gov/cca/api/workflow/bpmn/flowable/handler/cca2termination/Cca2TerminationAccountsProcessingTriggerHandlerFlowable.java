package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.service.Cca2TerminationAccountProcessingCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class Cca2TerminationAccountsProcessingTriggerHandlerFlowable implements JavaDelegate {

	private final Cca2TerminationAccountProcessingCreateRequestService cca2TerminationAccountProcessingCreateRequestService;
	
	@Override
    public void execute(DelegateExecution execution) {
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);

        // Create request
        cca2TerminationAccountProcessingCreateRequestService.createRequest(accountId, requestId, requestBusinessKey);
    }
}
