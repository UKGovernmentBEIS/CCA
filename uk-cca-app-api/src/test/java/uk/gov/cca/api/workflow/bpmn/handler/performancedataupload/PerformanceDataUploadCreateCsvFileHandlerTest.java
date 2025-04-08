package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service.PerformanceDataUploadService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataUploadCreateCsvFileHandlerTest {

    @InjectMocks
    private PerformanceDataUploadCreateCsvFileHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PerformanceDataUploadService performanceDataUploadService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        Map<Long, TargetUnitAccountUploadReport> accountReports = Map.of(
                1L, TargetUnitAccountUploadReport.builder().accountId(1L).build()
        );
        final RequestTask requestTask = RequestTask.builder().id(1L).build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_ACCOUNT_REPORTS)).thenReturn(accountReports);
        when(requestTaskService.findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_UPLOAD_SUBMIT, requestId))
                .thenReturn(requestTask);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_ACCOUNT_REPORTS);
        verify(requestTaskService, times(1))
                .findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_UPLOAD_SUBMIT, requestId);
        verify(performanceDataUploadService, times(1)).createCsvFile(requestTask, accountReports);
    }
}
