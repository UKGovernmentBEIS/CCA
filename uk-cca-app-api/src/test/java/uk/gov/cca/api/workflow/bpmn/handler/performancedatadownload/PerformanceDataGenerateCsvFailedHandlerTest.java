package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.validation.PerformanceDataDownloadViolation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PerformanceDataGenerateCsvFailedHandlerTest {

    @InjectMocks
    private PerformanceDataGenerateCsvFailedHandler handler;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).setVariable(
                CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE,
                PerformanceDataDownloadViolation.PerformanceDataDownloadViolationMessage.GENERATE_CSV_FAILED.name());
        verify(execution, times(1)).setVariable(
                CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ERRORS_FILE, null);
    }
}
