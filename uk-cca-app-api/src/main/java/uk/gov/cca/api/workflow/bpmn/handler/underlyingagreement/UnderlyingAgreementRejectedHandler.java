package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreement;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.service.UnderlyingAgreementRejectedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementRejectedHandler implements JavaDelegate {

	private final UnderlyingAgreementRejectedService rejectedService;
	@Override
	public void execute(DelegateExecution execution) throws Exception {

		rejectedService.reject((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
