package uk.gov.cca.api.workflow.bpmn.handler.buyoutsurplus;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service.BuyOutSurplusAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusAccountProcessingMarkAsCompletedHandler implements JavaDelegate {

    private final BuyOutSurplusAccountProcessingService buyOutSurplusAccountProcessingService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final BuyOutSurplusAccountState buyOutSurplusAccountState = (BuyOutSurplusAccountState) execution
                .getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE);

        if(buyOutSurplusAccountState.getErrors().isEmpty()) {
            buyOutSurplusAccountProcessingService.complete(requestId, buyOutSurplusAccountState);
        }
        else{
            // Set succeeded to false and close request
            buyOutSurplusAccountState.setSucceeded(false);
            execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        }
    }
}
