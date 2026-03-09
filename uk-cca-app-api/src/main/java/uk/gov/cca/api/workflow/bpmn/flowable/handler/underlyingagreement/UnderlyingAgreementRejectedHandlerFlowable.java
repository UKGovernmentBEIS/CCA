package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreement;

import lombok.RequiredArgsConstructor;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service.UnderlyingAgreementRejectedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementRejectedHandlerFlowable implements JavaDelegate {

	private final UnderlyingAgreementRejectedService rejectedService;
	@Override
	public void execute(DelegateExecution execution) {

		rejectedService.reject((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
