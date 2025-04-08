package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service.PerformanceDataSpreadsheetProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataSpreadsheetProcessingMarkAsCompletedHandlerTest {

    @InjectMocks
    private PerformanceDataSpreadsheetProcessingMarkAsCompletedHandler handler;

    @Mock
    private PerformanceDataSpreadsheetProcessingService performanceDataSpreadsheetProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final TargetUnitAccountUploadReport accountReport = TargetUnitAccountUploadReport.builder()
                .accountId(1L)
                .build();

        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT)).thenReturn(accountReport);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountReport.isSucceeded()).isTrue();
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);
        verifyNoMoreInteractions(execution);
    }

    @Test
    void execute_with_errors() throws Exception {
        final TargetUnitAccountUploadReport accountReport = TargetUnitAccountUploadReport.builder()
                .accountId(1L)
                .errors(List.of("error1", "error2"))
                .build();

        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT)).thenReturn(accountReport);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountReport.isSucceeded()).isFalse();
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);
        verify(execution, times(1))
                .setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        verify(performanceDataSpreadsheetProcessingService, times(1)).cleanupFailed(accountReport);
    }
}
