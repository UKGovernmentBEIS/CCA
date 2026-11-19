package uk.gov.cca.api.workflow.bpmn.flowable.handler.buyoutsurplusfacility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service.BuyOutSurplusFacilityAccountProcessingFinalizeService;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service.BuyOutSurplusFacilityAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityAccountProcessingHandlerFlowableTest {
    
    @InjectMocks
    private BuyOutSurplusFacilityAccountProcessingHandlerFlowable handler;

    @Mock
    private RequestService requestService;

    @Mock
    private BuyOutSurplusFacilityAccountProcessingService processingService;

    @Mock
    private BuyOutSurplusFacilityAccountProcessingFinalizeService finalizeService;

    @Mock
    private DelegateExecution execution;
    
    @Test
    void execute() throws BpmnExecutionException {
        final String requestId = "requestId";
        final List<BuyOutSurplusFacilityPerformanceResult> results =
                List.of(BuyOutSurplusFacilityPerformanceResult.builder().succeeded(true).build());
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder()
                .accountId(1L)
                .facilityPerformanceResults(results)
                .build();
        
        final Request request = Request.builder()
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_STATE)).thenReturn(accountState);
        when(requestService.findRequestById(requestId)).thenReturn(request);      

        // Invoke
        handler.execute(execution);

        // Verify
        verify(processingService).doProcess(requestId, accountState);
        verify(finalizeService).finalizeAccountProcessing(request, results);
    }
    
    @Test
    void execute_whenProcessingFails() throws BpmnExecutionException {
        final String requestId = "requestId";
        final List<BuyOutSurplusFacilityPerformanceResult> results = new ArrayList<>();
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder()
                .accountId(1L)
                .build();
        
        final Request request = Request.builder()
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_STATE)).thenReturn(accountState);
        when(requestService.findRequestById(requestId)).thenReturn(request);      
        doThrow(new RuntimeException("Processing failed"))
            .when(processingService).doProcess(requestId, accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(results).isEmpty();
        verify(finalizeService, never()).finalizeAccountProcessing(any(), anyList());
        assertThat(accountState.getErrors()).hasSize(1);
    }
}