package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreementvariation;

import lombok.RequiredArgsConstructor;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service.UnderlyingAgreementVariationActivatedGenerateDocumentsService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivatedGenerateDocumentsHandlerFlowable implements JavaDelegate {

    private final UnderlyingAgreementVariationActivatedGenerateDocumentsService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
