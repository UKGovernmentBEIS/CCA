package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreementvariation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service.UnderlyingAgreementVariationActivatedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivatedHandlerFlowable implements JavaDelegate {

    private final UnderlyingAgreementVariationActivatedService underlyingAgreementVariationActivatedService;

	@Override
	public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		underlyingAgreementVariationActivatedService.activateUnderlyingAgreementVariation(requestId);
	}
}
