package uk.gov.cca.api.workflow.bpmn.flowable.handler.buyoutsurplusfacility;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service.BuyOutSurplusFacilityAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityAccountProcessingMarkAsCompletedHandlerFlowableTest {

    @InjectMocks
    private BuyOutSurplusFacilityAccountProcessingMarkAsCompletedHandlerFlowable handler;
    
    @Mock
	private BuyOutSurplusFacilityAccountProcessingService buyOutSurplusFacilityAccountProcessingService;
    @Mock
    private DelegateExecution execution;
    
    @Test
    void execute() {
        final String requestId = "requestId";
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder()
                .accountId(1L)
                .facilityPerformanceResults(List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                        .build()))
                .succeeded(true)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_STATE);
        verify(buyOutSurplusFacilityAccountProcessingService, times(1)).complete(requestId, accountState);
        verifyNoMoreInteractions(execution);
    }

    @Test
    void execute_withErrors() {
        final String requestId = "requestId";
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder()
                .accountId(1L)
                .facilityPerformanceResults(List.of())
                .errors(List.of("error1", "error2"))
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_STATE);
        verify(buyOutSurplusFacilityAccountProcessingService, times(1)).complete(requestId, accountState);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }

    @Test
    void execute_withFacilityErrors() {
        final String requestId = "requestId";
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder()
                .accountId(1L)
                .facilityPerformanceResults(List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                        .build()))
                .succeeded(false)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);
        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_STATE);
        verify(buyOutSurplusFacilityAccountProcessingService, times(1)).complete(requestId, accountState);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
