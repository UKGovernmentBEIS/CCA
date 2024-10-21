package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreementvariation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.service.UnderlyingAgreementVariationOfficialNoticeService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationSendOfficialNoticeEmailHandler implements JavaDelegate {

	private final UnderlyingAgreementVariationOfficialNoticeService service;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		service.sendOfficialNotice(requestId);
	}
}
