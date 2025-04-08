package uk.gov.cca.api.workflow.bpmn.handler.buyoutsurplus;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.service.BuyOutSurplusRunService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusAccountProcessingCompletedHandler implements JavaDelegate {

    private final BuyOutSurplusRunService buyOutSurplusRunService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        final BuyOutSurplusAccountState accountState = (BuyOutSurplusAccountState) execution
                .getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE);

        buyOutSurplusRunService.accountProcessingCompleted(requestId, accountId, accountState);

        // Increment completed number var
        final Integer numberOfAccountsCompleted = (Integer) execution
                .getVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED);
        execution.setVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, numberOfAccountsCompleted + 1);
    }
}
