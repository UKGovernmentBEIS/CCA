package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreement;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.service.UnderlyingAgreementActivatedAddRequestActionService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementActivatedAddRequestActionHandler implements JavaDelegate {

	private final UnderlyingAgreementActivatedAddRequestActionService requestActionService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		requestActionService.addRequestAction(requestId);
	}
}
