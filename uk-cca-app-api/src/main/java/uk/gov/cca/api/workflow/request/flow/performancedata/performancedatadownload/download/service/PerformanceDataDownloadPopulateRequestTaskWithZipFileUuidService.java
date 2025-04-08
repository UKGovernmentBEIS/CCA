package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadSubmitRequestTaskPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

@Service
@RequiredArgsConstructor
public class PerformanceDataDownloadPopulateRequestTaskWithZipFileUuidService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void populateRequestTaskPayloadWithReportFiles(String requestId, FileInfoDTO zipFile, FileInfoDTO errorsFile, String errorMessage) {
        final RequestTask requestTask = requestTaskService
                .findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_DOWNLOAD_SUBMIT, requestId);
        PerformanceDataDownloadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataDownloadSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setZipFile(zipFile);
        taskPayload.setErrorsFile(errorsFile);
        taskPayload.setErrorMessage(errorMessage);
        taskPayload.setProcessCompleted(true);
    }
}
