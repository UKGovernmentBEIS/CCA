package uk.gov.cca.api.workflow.bpmn.flowable.handler.targetunitaccountcreation;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service.TargetUnitAccountApplyUnderlyingAgreementService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Component
@RequiredArgsConstructor
public class TargetUnitAccountApplyUnderlyingAgreementHandlerFlowable implements JavaDelegate {

    private final TargetUnitAccountApplyUnderlyingAgreementService targetUnitAccountApplyUnderlyingAgreementService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        final Long accountId = (Long) delegateExecution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        
        targetUnitAccountApplyUnderlyingAgreementService.execute(accountId);

    }
}
