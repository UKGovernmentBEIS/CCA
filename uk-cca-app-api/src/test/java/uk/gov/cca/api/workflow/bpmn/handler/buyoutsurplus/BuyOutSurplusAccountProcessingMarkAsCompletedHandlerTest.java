package uk.gov.cca.api.workflow.bpmn.handler.buyoutsurplus;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service.BuyOutSurplusAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusAccountProcessingMarkAsCompletedHandlerTest {

    @InjectMocks
    private BuyOutSurplusAccountProcessingMarkAsCompletedHandler handler;

    @Mock
    private BuyOutSurplusAccountProcessingService buyOutSurplusAccountProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "requestId";
        final BuyOutSurplusAccountState buyOutSurplusAccountState = BuyOutSurplusAccountState.builder()
                .accountId(1L)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE)).thenReturn(buyOutSurplusAccountState);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE);
        verify(buyOutSurplusAccountProcessingService, times(1))
                .complete(requestId, buyOutSurplusAccountState);
        verifyNoMoreInteractions(execution);
    }

    @Test
    void execute_with_errors() throws Exception {
        final String requestId = "requestId";
        final BuyOutSurplusAccountState buyOutSurplusAccountState = BuyOutSurplusAccountState.builder()
                .accountId(1L)
                .errors(List.of("error1", "error2"))
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE)).thenReturn(buyOutSurplusAccountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(buyOutSurplusAccountState.isSucceeded()).isFalse();
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE);
        verify(execution, times(1))
                .setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        verifyNoInteractions(buyOutSurplusAccountProcessingService);
    }
}
