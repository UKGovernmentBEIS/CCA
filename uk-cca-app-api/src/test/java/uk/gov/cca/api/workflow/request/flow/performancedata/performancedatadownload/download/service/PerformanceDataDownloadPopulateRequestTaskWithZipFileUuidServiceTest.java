package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadSubmitRequestTaskPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataDownloadPopulateRequestTaskWithZipFileUuidServiceTest {

    @InjectMocks
    private PerformanceDataDownloadPopulateRequestTaskWithZipFileUuidService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void populateRequestTaskPayloadWithReportFiles() {
        final String requestId = "request-id";
        final FileInfoDTO zipFile = FileInfoDTO.builder().name("zip-1").build();
        final FileInfoDTO errorsFile = FileInfoDTO.builder().name("error-1").build();
        final String errorMessage = "error message";

        RequestTask requestTask = RequestTask.builder()
                .payload(PerformanceDataDownloadSubmitRequestTaskPayload.builder().build())
                .build();

        when(requestTaskService.findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_DOWNLOAD_SUBMIT, requestId))
                .thenReturn(requestTask);

        // Invoke
        service.populateRequestTaskPayloadWithReportFiles(requestId, zipFile, errorsFile, errorMessage);

        // Verify
        PerformanceDataDownloadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataDownloadSubmitRequestTaskPayload) requestTask.getPayload();

        assertThat(taskPayload.getZipFile()).isEqualTo(zipFile);
        assertThat(taskPayload.getErrorsFile()).isEqualTo(errorsFile);
        assertThat(taskPayload.getErrorMessage()).isEqualTo(errorMessage);
        assertThat(taskPayload.getProcessCompleted()).isTrue();
        verify(requestTaskService, times(1))
                .findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_DOWNLOAD_SUBMIT, requestId);
    }
}
