package uk.gov.cca.api.workflow.bpmn.flowable.handler.buyoutsurplusfacility;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service.BuyOutSurplusFacilityRunService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityRunPopulateNumberOfAccountsCompletedHandlerFlowable implements JavaDelegate {

    private final BuyOutSurplusFacilityRunService buyOutSurplusFacilityRunService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final long accountsCompletedNumber = buyOutSurplusFacilityRunService.getNumberOfAccountsCompleted(requestId);

        execution.setVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, accountsCompletedNumber);
    }
}
