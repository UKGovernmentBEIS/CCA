package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreement;

import lombok.RequiredArgsConstructor;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service.UnderlyingAgreementReviewDeterminationSubmittedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementDeterminationNoticeHandlerFlowable implements JavaDelegate {

	private final UnderlyingAgreementReviewDeterminationSubmittedService underlyingAgreementReviewDeterminationSubmittedService;

	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		underlyingAgreementReviewDeterminationSubmittedService.acceptUnderlyingAgreement(requestId);
	}
}
