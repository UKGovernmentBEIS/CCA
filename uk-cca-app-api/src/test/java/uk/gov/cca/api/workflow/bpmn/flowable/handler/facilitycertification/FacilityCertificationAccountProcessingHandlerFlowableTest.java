package uk.gov.cca.api.workflow.bpmn.flowable.handler.facilitycertification;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service.FacilityCertificationAccountProcessingService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationAccountProcessingHandlerFlowableTest {

    @InjectMocks
    private FacilityCertificationAccountProcessingHandlerFlowable handler;

    @Mock
    private RequestService requestService;

    @Mock
    private FacilityCertificationAccountProcessingService facilityCertificationAccountProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws BpmnExecutionException {
        final String requestId = "request-id";
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .accountId(1L)
                .build();

        final Request request = Request.builder().id(requestId).build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE)).thenReturn(accountState);
        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(facilityCertificationAccountProcessingService, times(1))
                .doProcess(request, accountState);
    }

    @Test
    void execute_exception() throws Exception {
        final String requestId = "request-id";
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .accountId(1L)
                .build();

        final Request request = Request.builder().id(requestId).build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE)).thenReturn(accountState);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        doThrow(new BpmnExecutionException(List.of("error")))
                .when(facilityCertificationAccountProcessingService).doProcess(request, accountState);

        // Invoke
        assertDoesNotThrow(() -> handler.execute(execution));

        // Verify
        assertThat(accountState.getErrors()).isNotEmpty();
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(facilityCertificationAccountProcessingService, times(1))
                .doProcess(request, accountState);
    }
}
