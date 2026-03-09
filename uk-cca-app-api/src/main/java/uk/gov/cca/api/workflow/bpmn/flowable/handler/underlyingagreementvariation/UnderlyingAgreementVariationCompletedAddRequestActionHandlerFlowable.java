package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreementvariation;

import lombok.RequiredArgsConstructor;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service.UnderlyingAgreementVariationCompletedAddRequestActionService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationCompletedAddRequestActionHandlerFlowable implements JavaDelegate {

    private final UnderlyingAgreementVariationCompletedAddRequestActionService requestActionService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        requestActionService.addRequestAction(requestId);
    }
}
