package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service.FacilityCertificationAccountProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationAccountProcessingHandlerTest {

    @InjectMocks
    private FacilityCertificationAccountProcessingHandler handler;

    @Mock
    private FacilityCertificationAccountProcessingService facilityCertificationAccountProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .accountId(1L)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE);
        verify(facilityCertificationAccountProcessingService, times(1))
                .doProcess(requestId, accountState);
    }

    @Test
    void execute_exception() throws Exception {
        final String requestId = "request-id";
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .accountId(1L)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE)).thenReturn(accountState);
        doThrow(new BpmnExecutionException(List.of("error")))
                .when(facilityCertificationAccountProcessingService).doProcess(requestId, accountState);

        // Invoke
        assertDoesNotThrow(() -> handler.execute(execution));

        // Verify
        assertThat(accountState.getErrors()).isNotEmpty();
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE);
        verify(facilityCertificationAccountProcessingService, times(1))
                .doProcess(requestId, accountState);
    }
}
