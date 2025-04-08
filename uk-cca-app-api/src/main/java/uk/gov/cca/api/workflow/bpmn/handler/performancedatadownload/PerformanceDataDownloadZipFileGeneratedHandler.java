package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.service.PerformanceDataDownloadPopulateRequestTaskWithZipFileUuidService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceDataDownloadZipFileGeneratedHandler implements JavaDelegate {

    private final PerformanceDataDownloadPopulateRequestTaskWithZipFileUuidService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final FileInfoDTO zipFile = (FileInfoDTO) execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ZIP_FILE);
        final FileInfoDTO errorsFile = (FileInfoDTO) execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ERRORS_FILE);
        final String errorMessage = (String) execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE);

        service.populateRequestTaskPayloadWithReportFiles(requestId, zipFile, errorsFile, errorMessage);
    }
}
