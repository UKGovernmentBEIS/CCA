package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreementvariation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivatedHandler implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		//TODO
	}
}
