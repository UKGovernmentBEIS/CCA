package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service.PerformanceDataSpreadsheetProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class PerformanceDataSpreadsheetProcessingHandlerTest {

    @InjectMocks
    private PerformanceDataSpreadsheetProcessingHandler handler;

    @Mock
    private PerformanceDataSpreadsheetProcessingService performanceDataSpreadsheetProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        final TargetUnitAccountUploadReport accountReport = TargetUnitAccountUploadReport.builder()
                .accountId(1L)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT)).thenReturn(accountReport);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);
        verify(performanceDataSpreadsheetProcessingService, times(1))
                .doProcess(requestId, accountReport);
    }

    @Test
    void execute_PerformanceDataException() throws Exception {
        final String requestId = "request-id";
        final TargetUnitAccountUploadReport accountReport = TargetUnitAccountUploadReport.builder()
                .accountId(1L)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT)).thenReturn(accountReport);
        doThrow(new BpmnExecutionException(List.of("error")))
                .when(performanceDataSpreadsheetProcessingService).doProcess(requestId, accountReport);

        // Invoke
        assertDoesNotThrow(() -> handler.execute(execution));

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);
        verify(performanceDataSpreadsheetProcessingService, times(1))
                .doProcess(requestId, accountReport);
    }
}
