package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca2termination.run.service.Cca2TerminationRunRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationRunCompletedHandlerFlowableTest {

	@InjectMocks
    private Cca2TerminationRunCompletedHandlerFlowable handler;

    @Mock
    private Cca2TerminationRunRequestService cca2TerminationRunRequestService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "request-id";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(cca2TerminationRunRequestService, times(1)).completeCca2TerminationRun(requestId);
    }
}
