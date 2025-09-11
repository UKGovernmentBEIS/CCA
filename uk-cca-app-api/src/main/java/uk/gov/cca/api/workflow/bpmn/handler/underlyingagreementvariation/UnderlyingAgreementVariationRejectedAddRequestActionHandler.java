package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreementvariation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service.UnderlyingAgreementVariationRejectedAddRequestActionService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationRejectedAddRequestActionHandler implements JavaDelegate  {

	private final UnderlyingAgreementVariationRejectedAddRequestActionService requestActionService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		requestActionService.addRequestAction(requestId);
	}
}
