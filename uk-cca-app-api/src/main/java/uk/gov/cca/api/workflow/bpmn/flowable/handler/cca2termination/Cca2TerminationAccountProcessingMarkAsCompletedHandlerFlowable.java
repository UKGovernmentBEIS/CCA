package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.domain.Cca2TerminationAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.service.Cca2TerminationAccountProcessingCompletedService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class Cca2TerminationAccountProcessingMarkAsCompletedHandlerFlowable implements JavaDelegate {

	private final RequestService requestService;
	private final Cca2TerminationAccountProcessingCompletedService cca2TerminationAccountProcessingCompletedService;
	
	@Override
    public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
		final Request request = requestService.findRequestById(requestId);
		final Cca2TerminationAccountState accountState = (Cca2TerminationAccountState) execution.getVariable(CcaBpmnProcessConstants.CCA2_TERMINATION_ACCOUNT_STATE);
		final Cca2TerminationAccountProcessingRequestMetadata metadata = (Cca2TerminationAccountProcessingRequestMetadata) request.getMetadata();
		
		// Update succeeded status
		if(accountState.getErrors().isEmpty()) {
            accountState.setSucceeded(Boolean.TRUE);
        }
        else {
            accountState.setSucceeded(Boolean.FALSE);
            execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        }
		// Update parent request metadata
		cca2TerminationAccountProcessingCompletedService.completed(metadata.getParentRequestId(), requestId, accountId, accountState);
		
	}
}
