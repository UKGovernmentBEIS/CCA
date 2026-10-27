package uk.gov.cca.api.workflow.bpmn.flowable.handler.buyoutsurplusfacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service.BuyOutSurplusFacilityAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityAccountProcessingMarkAsCompletedHandlerFlowable implements JavaDelegate {

	private final BuyOutSurplusFacilityAccountProcessingService buyOutSurplusFacilityAccountProcessingService;

    @Override
    public void execute(DelegateExecution execution) {
    	final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final BuyOutSurplusFacilityAccountState accountState = (BuyOutSurplusFacilityAccountState) execution
                .getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_STATE);

    	buyOutSurplusFacilityAccountProcessingService.complete(requestId, accountState);
    	
    	// Close request if account process failed
        if(!accountState.isSucceeded()) {
            execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        }
    }
}
