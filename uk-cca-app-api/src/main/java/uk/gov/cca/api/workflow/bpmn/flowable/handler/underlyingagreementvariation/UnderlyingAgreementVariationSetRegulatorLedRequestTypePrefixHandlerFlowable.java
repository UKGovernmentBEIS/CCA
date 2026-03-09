package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreementvariation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestCustomContext;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class UnderlyingAgreementVariationSetRegulatorLedRequestTypePrefixHandlerFlowable implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable(BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, CcaRequestCustomContext.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED);
    }
}
