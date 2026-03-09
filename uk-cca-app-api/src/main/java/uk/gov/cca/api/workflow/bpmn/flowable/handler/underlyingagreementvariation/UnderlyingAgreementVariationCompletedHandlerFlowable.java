package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreementvariation;

import lombok.RequiredArgsConstructor;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationCompletedHandlerFlowable implements JavaDelegate {

    private final UnderlyingAgreementVariationCompletedService underlyingAgreementVariationCompletedService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        underlyingAgreementVariationCompletedService.completeUnderlyingAgreementVariation(requestId);
    }
}
