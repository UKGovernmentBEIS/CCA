package uk.gov.cca.api.workflow.bpmn.handler.buyoutsurplus;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.service.BuyOutSurplusRunService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusAccountProcessingCompletedHandlerTest {

    @InjectMocks
    private BuyOutSurplusAccountProcessingCompletedHandler handler;

    @Mock
    private BuyOutSurplusRunService buyOutSurplusRunService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        final long accountId = 1L;
        final BuyOutSurplusAccountState buyOutSurplusAccountState = BuyOutSurplusAccountState.builder().accountId(1L).build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE)).thenReturn(buyOutSurplusAccountState);
        when(execution.getVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED))
                .thenReturn(0);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE);
        verify(buyOutSurplusRunService, times(1))
                .accountProcessingCompleted(requestId, accountId, buyOutSurplusAccountState);
        verify(execution, times(1))
                .getVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED);
        verify(execution, times(1))
                .setVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 1);
    }
}
