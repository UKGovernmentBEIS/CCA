package uk.gov.cca.api.workflow.bpmn.handler.buyoutsurplus;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service.BuyOutSurplusAccountProcessingCreateRequestService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusAccountsProcessingTriggerHandlerTest {

    @InjectMocks
    private BuyOutSurplusAccountsProcessingTriggerHandler handler;

    @Mock
    private BuyOutSurplusAccountProcessingCreateRequestService buyOutSurplusAccountProcessingCreateRequestService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final long accountId = 1L;
        final String requestId = "request-id";
        final BuyOutSurplusAccountState account = BuyOutSurplusAccountState.builder()
                .accountId(1L)
                .build();
        final Map<Long, BuyOutSurplusAccountState> accountResults = Map.of(1L, account);
        final String requestBusinessKey ="bk-request-id";

        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATES)).thenReturn(accountResults);
        when(execution.getVariable(BpmnProcessConstants.BUSINESS_KEY)).thenReturn(requestBusinessKey);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATES);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.BUSINESS_KEY);
        verify(buyOutSurplusAccountProcessingCreateRequestService, times(1))
                .createRequest(account, requestId, requestBusinessKey);
    }
}
