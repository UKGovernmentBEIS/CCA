package uk.gov.cca.api.workflow.bpmn.flowable.handler.admintermination;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.service.AdminTerminationWithdrawSubmittedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AdminTerminationWithdrawnHandlerFlowable implements JavaDelegate {

    private final AdminTerminationWithdrawSubmittedService adminTerminationWithdrawSubmittedService;

	@Override
	public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        adminTerminationWithdrawSubmittedService.submit(requestId);
	}
}
