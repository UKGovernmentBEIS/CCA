package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service.PerformanceDataUploadCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataUploadCompletedHandlerTest {

    @InjectMocks
    private PerformanceDataUploadCompletedHandler handler;

    @Mock
    private PerformanceDataUploadCompletedService performanceDataUploadCompletedService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        Map<Long, TargetUnitAccountUploadReport> accountReports = Map.of(
                1L, TargetUnitAccountUploadReport.builder().accountId(1L).build()
        );
        final String errorMessage = "error message";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_ACCOUNT_REPORTS)).thenReturn(accountReports);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE)).thenReturn(errorMessage);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_ACCOUNT_REPORTS);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE);
        verify(performanceDataUploadCompletedService, times(1)).completed(requestId, accountReports, errorMessage);
    }
}
