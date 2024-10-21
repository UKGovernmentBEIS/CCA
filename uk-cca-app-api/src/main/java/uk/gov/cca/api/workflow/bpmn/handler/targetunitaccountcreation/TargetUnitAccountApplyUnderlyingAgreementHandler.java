package uk.gov.cca.api.workflow.bpmn.handler.targetunitaccountcreation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service.TargetUnitAccountApplyUnderlyingAgreementService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
@Log4j2
@Component
@RequiredArgsConstructor
public class TargetUnitAccountApplyUnderlyingAgreementHandler implements JavaDelegate {

    private final TargetUnitAccountApplyUnderlyingAgreementService targetUnitAccountApplyUnderlyingAgreementService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        final Long accountId = (Long) delegateExecution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        try {
            targetUnitAccountApplyUnderlyingAgreementService.execute(accountId);
        } catch (Exception e) {
            log.error(String.format("TargetUnitAccountApplyUnderlyingAgreementHandler error for account with id '%s'", accountId), e);
            throw new BpmnError("TargetUnitAccountApplyUnderlyingAgreementHandler", e);
        }
    }
}
