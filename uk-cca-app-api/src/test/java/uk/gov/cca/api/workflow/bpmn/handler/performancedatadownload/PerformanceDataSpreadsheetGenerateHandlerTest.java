package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service.PerformanceDataSpreadsheetDoGenerateService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataSpreadsheetGenerateHandlerTest {

    @InjectMocks
    private PerformanceDataSpreadsheetGenerateHandler handler;

    @Mock
    private PerformanceDataSpreadsheetDoGenerateService performanceDataSpreadsheetDoGenerateService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        final long accountId = 1L;
        TargetUnitAccountReport accountReport = TargetUnitAccountReport.builder()
                .accountId(accountId)
                .errors(new ArrayList<>())
                .build();
        final FileInfoDTO file = FileInfoDTO.builder().name("excel").build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT))
                .thenReturn(accountReport);
        when(performanceDataSpreadsheetDoGenerateService.doGenerate(requestId, accountId))
                .thenReturn(file);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountReport.getFileInfo()).isEqualTo(file);
        assertThat(accountReport.getErrors()).isEmpty();
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1))
                .getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);
        verify(performanceDataSpreadsheetDoGenerateService, times(1)).doGenerate(requestId, accountId);
    }

    @Test
    void execute_ReportingSpreadsheetGenerateException() throws Exception {
        final String requestId = "request-id";
        final long accountId = 1L;
        TargetUnitAccountReport accountReport = TargetUnitAccountReport.builder()
                .accountId(accountId)
                .errors(new ArrayList<>())
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT))
                .thenReturn(accountReport);
        when(performanceDataSpreadsheetDoGenerateService.doGenerate(requestId, accountId))
                .thenThrow(new BpmnExecutionException("Message", List.of("Error message")));

        // Invoke
        BpmnError ex = assertThrows(BpmnError.class, () -> handler.execute(execution));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo("PerformanceDataSpreadsheetGenerateHandler");
        assertThat(accountReport.getFileInfo()).isNull();
        assertThat(accountReport.getErrors()).containsExactly("Error message");
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1))
                .getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);
        verify(performanceDataSpreadsheetDoGenerateService, times(1)).doGenerate(requestId, accountId);
    }
}
