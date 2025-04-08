package uk.gov.cca.api.workflow.bpmn.handler.buyoutsurplus;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class BuyOutSurplusAccountProcessingMarkAsCompletedHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final BuyOutSurplusAccountState buyOutSurplusAccountState = (BuyOutSurplusAccountState) execution
                .getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE);

        if(buyOutSurplusAccountState.getErrors().isEmpty()) {
            // TODO
            buyOutSurplusAccountState.setSucceeded(true);
        }
        else{
            // Set succeeded to false and close request
            buyOutSurplusAccountState.setSucceeded(false);
            execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        }
    }
}
