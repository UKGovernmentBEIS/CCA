package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service.PerformanceDataGenerateErrorsFileService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataGenerateErrorsFileHandlerTest {

    @InjectMocks
    private PerformanceDataGenerateErrorsFileHandler handler;

    @Mock
    private PerformanceDataGenerateErrorsFileService performanceDataGenerateErrorsFileService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        final FileInfoDTO csvFile = FileInfoDTO.builder().name("csvFile").build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(performanceDataGenerateErrorsFileService.generateErrorsFile(requestId)).thenReturn(Optional.of(csvFile));

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(performanceDataGenerateErrorsFileService, times(1)).generateErrorsFile(requestId);
        verify(execution, times(1))
                .setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ERRORS_FILE, csvFile);
    }
}
