package uk.gov.cca.api.workflow.bpmn.handler.cca2extensionnotice;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.run.service.Cca2ExtensionNoticeRunService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeRunCompletedHandlerTest {

    @InjectMocks
    private Cca2ExtensionNoticeRunCompletedHandler handler;

    @Mock
    private Cca2ExtensionNoticeRunService cca2ExtensionNoticeRunService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "requestId";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(cca2ExtensionNoticeRunService, times(1)).complete(requestId);
    }
}
