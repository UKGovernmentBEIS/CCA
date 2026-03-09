package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreement;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.service.UnderlyingAgreementActivatedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementActivatedHandlerFlowable implements JavaDelegate {

	private final UnderlyingAgreementActivatedService underlyingAgreementActivatedService;
	
	@Override
	public void execute(DelegateExecution execution) {
		String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		underlyingAgreementActivatedService.activateUnderlyingAgreement(requestId);
	}
}
