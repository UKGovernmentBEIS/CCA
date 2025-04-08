package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreementvariation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service.UnderlyingAgreementVariationActivatedGenerateDocumentsService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivatedGenerateDocumentsHandler implements JavaDelegate {

    private final UnderlyingAgreementVariationActivatedGenerateDocumentsService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        service.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
