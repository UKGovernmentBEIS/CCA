package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca2termination.run.service.Cca2TerminationRunRequestService;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationRunTerminateVariationRequestsHandlerFlowableTest {

	@InjectMocks
    private Cca2TerminationRunTerminateVariationRequestsHandlerFlowable handler;

    @Mock
    private Cca2TerminationRunRequestService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {

        // Invoke
        handler.execute(execution);

        // Verify
        verify(service, times(1)).terminateVariationRequests();
    }
}
