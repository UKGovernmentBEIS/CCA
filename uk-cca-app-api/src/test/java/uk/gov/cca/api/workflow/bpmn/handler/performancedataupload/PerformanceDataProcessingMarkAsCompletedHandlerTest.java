package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PerformanceDataProcessingMarkAsCompletedHandlerTest {

    @InjectMocks
    private PerformanceDataProcessingMarkAsCompletedHandler handler;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1))
                .setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
