package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreementvariation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationOfficialNoticeService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationRejectedGenerateOfficialNoticeHandler implements JavaDelegate {


	private final UnderlyingAgreementVariationOfficialNoticeService noticeService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		noticeService.generateAndSaveRejectedOfficialNotice(requestId);
	}
}
