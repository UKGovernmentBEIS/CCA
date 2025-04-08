package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.service.PerformanceDataDownloadPopulateRequestTaskWithZipFileUuidService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataDownloadZipFileGeneratedHandlerTest {

    @InjectMocks
    private PerformanceDataDownloadZipFileGeneratedHandler handler;

    @Mock
    private PerformanceDataDownloadPopulateRequestTaskWithZipFileUuidService performanceDataDownloadPopulateRequestTaskWithZipFileUuidService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        final FileInfoDTO zipFile = FileInfoDTO.builder().name("zip-1").build();
        final FileInfoDTO errorsFile = FileInfoDTO.builder().name("error-1").build();
        final String errorMessage = "error message";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ZIP_FILE)).thenReturn(zipFile);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ERRORS_FILE)).thenReturn(errorsFile);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE)).thenReturn(errorMessage);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ZIP_FILE);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ERRORS_FILE);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE);
        verify(performanceDataDownloadPopulateRequestTaskWithZipFileUuidService, times(1))
                .populateRequestTaskPayloadWithReportFiles(requestId, zipFile, errorsFile, errorMessage);
    }
}
