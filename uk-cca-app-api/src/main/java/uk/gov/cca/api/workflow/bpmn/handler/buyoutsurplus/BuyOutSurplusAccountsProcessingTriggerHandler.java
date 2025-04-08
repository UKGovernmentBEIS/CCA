package uk.gov.cca.api.workflow.bpmn.handler.buyoutsurplus;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service.BuyOutSurplusAccountProcessingCreateRequestService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusAccountsProcessingTriggerHandler implements JavaDelegate {

    private final BuyOutSurplusAccountProcessingCreateRequestService service;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) throws Exception {
        final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        final Map<Long, BuyOutSurplusAccountState> accountStates = (Map<Long, BuyOutSurplusAccountState>) execution
                .getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATES);
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);

        service.createRequest(accountStates.get(accountId), requestId, requestBusinessKey);
    }
}
