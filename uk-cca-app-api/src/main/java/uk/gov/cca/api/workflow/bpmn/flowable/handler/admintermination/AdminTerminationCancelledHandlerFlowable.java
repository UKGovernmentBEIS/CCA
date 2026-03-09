package uk.gov.cca.api.workflow.bpmn.flowable.handler.admintermination;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.service.AdminTerminationCancelledService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AdminTerminationCancelledHandlerFlowable implements JavaDelegate {

	private final AdminTerminationCancelledService requestService;

	@Override
	public void execute(DelegateExecution execution){
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		requestService.cancel(requestId);
	}
}
