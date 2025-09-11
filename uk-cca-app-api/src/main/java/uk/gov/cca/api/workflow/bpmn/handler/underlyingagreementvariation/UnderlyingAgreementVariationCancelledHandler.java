package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreementvariation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationCancelledService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationCancelledHandler implements JavaDelegate {

    private final UnderlyingAgreementVariationCancelledService cancelledService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final String assigneeRoleType = (String) execution.getVariable(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE);
        cancelledService.cancel(requestId, assigneeRoleType);
    }
}
