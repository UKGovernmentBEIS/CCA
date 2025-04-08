package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service.PerformanceDataGenerateZipFileService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataGenerateZipFileHandlerTest {

    @InjectMocks
    private PerformanceDataGenerateZipFileHandler handler;

    @Mock
    private PerformanceDataGenerateZipFileService performanceDataGenerateZipFileService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        final FileInfoDTO zipFile = FileInfoDTO.builder().name("report.zip").build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(performanceDataGenerateZipFileService.generateZipFile(requestId)).thenReturn(zipFile);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(performanceDataGenerateZipFileService, times(1))
                .generateZipFile(requestId);
        verify(execution, times(1))
                .setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ZIP_FILE, zipFile);
        verify(execution, times(1))
                .setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE, null);
    }
}
