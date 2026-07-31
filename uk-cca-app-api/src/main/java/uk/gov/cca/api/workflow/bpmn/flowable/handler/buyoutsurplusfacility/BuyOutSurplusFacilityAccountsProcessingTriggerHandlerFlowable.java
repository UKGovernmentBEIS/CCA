package uk.gov.cca.api.workflow.bpmn.flowable.handler.buyoutsurplusfacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service.BuyOutSurplusFacilityAccountProcessingCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityAccountsProcessingTriggerHandlerFlowable implements JavaDelegate {
	
	private final BuyOutSurplusFacilityAccountProcessingCreateRequestService service;

    @Override
    public void execute(DelegateExecution execution) {
    	final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);

        // Create request
        service.createRequest(accountId, requestId, requestBusinessKey);
    }
}
