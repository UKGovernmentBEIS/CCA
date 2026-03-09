package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreement;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.service.UnderlyingAgreementActivatedGenerateDocumentsService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementActivatedGenerateDocumentsHandlerFlowable implements JavaDelegate {
	
	private final UnderlyingAgreementActivatedGenerateDocumentsService service;
	
	@Override
	public void execute(DelegateExecution execution) {
		service.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
