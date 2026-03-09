package uk.gov.cca.api.workflow.bpmn.camunda.handler.underlyingagreement;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.service.UnderlyingAgreementOfficialNoticeService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementRejectedGenerateOfficialNoticeHandler implements JavaDelegate {


	private final UnderlyingAgreementOfficialNoticeService noticeService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		noticeService.generateAndSaveRejectedOfficialNotice(requestId);
	}
}
