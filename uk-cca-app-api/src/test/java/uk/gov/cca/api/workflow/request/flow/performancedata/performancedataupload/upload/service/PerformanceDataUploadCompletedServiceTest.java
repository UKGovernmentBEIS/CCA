package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataUploadCompletedServiceTest {

    @InjectMocks
    private PerformanceDataUploadCompletedService performanceDataUploadCompletedService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PerformanceDataUploadService performanceDataUploadService;

    @Test
    void completed() {
        final String requestId = "requestId";
        final String errorMessage = "error message";
        Map<Long, TargetUnitAccountUploadReport> accountReports = Map.of(
                1L, TargetUnitAccountUploadReport.builder().accountId(1L).succeeded(false).build()
        );
        PerformanceDataUploadSubmitRequestTaskPayload taskPayload = PerformanceDataUploadSubmitRequestTaskPayload.builder()
                .totalFilesUploaded(1)
                .filesSucceeded(0)
                .filesFailed(0)
                .build();
        RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        when(requestTaskService.findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_UPLOAD_SUBMIT, requestId))
                .thenReturn(requestTask);

        // Invoke
        performanceDataUploadCompletedService.completed(requestId, accountReports, errorMessage);

        // Verify
        verify(requestTaskService, times(1))
                .findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_UPLOAD_SUBMIT, requestId);
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getAccountReports())
                .isEqualTo(accountReports);
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getProcessCompleted())
                .isTrue();
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getErrorMessage())
                .isEqualTo(errorMessage);
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getFilesFailed())
                .isEqualTo(1);
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getFilesSucceeded())
                .isZero();
    }

    @Test
    void completedDueToEmptyAccountReports() {
        RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataUploadSubmitRequestTaskPayload.builder()
                        .totalFilesUploaded(1)
                        .filesSucceeded(0)
                        .filesFailed(0)
                        .build())
                .build();

        // Invoke
        performanceDataUploadCompletedService.completedDueToEmptyAccountReports(requestTask);

        // Verify
        verify(performanceDataUploadService, times(1))
                .createCsvFile(requestTask, Map.of());
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getProcessCompleted())
                .isTrue();
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getErrorMessage())
                .isNull();
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getFilesFailed())
                .isEqualTo(1);
        assertThat(((PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload()).getFilesSucceeded())
                .isZero();
    }
}
