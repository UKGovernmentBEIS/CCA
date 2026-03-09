package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service.SubsistenceFeesRunSubmittedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunSubmittedHandlerFlowable implements JavaDelegate {

	private final SubsistenceFeesRunSubmittedService service;
	
	@Override
	public void execute(DelegateExecution execution) {
		service.subsistenceFeesRunSubmitted((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
