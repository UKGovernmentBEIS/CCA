package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreementvariation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationOfficialNoticeService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationSendOfficialNoticeEmailHandlerFlowable implements JavaDelegate {

	private final UnderlyingAgreementVariationOfficialNoticeService service;

	@Override
	public void execute(DelegateExecution execution) {

		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		service.sendOfficialNotice(requestId);
	}
}
