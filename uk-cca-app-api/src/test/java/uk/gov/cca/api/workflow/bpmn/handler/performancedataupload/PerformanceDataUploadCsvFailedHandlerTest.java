package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PerformanceDataUploadCsvFailedHandlerTest {

    @InjectMocks
    private PerformanceDataUploadCsvFailedHandler handler;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE,
                PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.GENERATE_CSV_FAILED.name());
    }
}
