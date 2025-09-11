package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreement;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service.UnderlyingAgreementReviewDeterminationSubmittedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementDeterminationNoticeHandler implements JavaDelegate {

	private final UnderlyingAgreementReviewDeterminationSubmittedService underlyingAgreementReviewDeterminationSubmittedService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		underlyingAgreementReviewDeterminationSubmittedService.acceptUnderlyingAgreement(requestId);
	}
}
