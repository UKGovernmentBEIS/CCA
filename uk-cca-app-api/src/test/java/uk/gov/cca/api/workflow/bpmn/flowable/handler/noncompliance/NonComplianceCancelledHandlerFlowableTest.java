package uk.gov.cca.api.workflow.bpmn.flowable.handler.noncompliance;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceCancelledService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceCancelledHandlerFlowableTest {

    @InjectMocks
    private NonComplianceCancelledHandlerFlowable handler;

    @Mock
    private NonComplianceCancelledService nonComplianceCancelledService;

    @Mock
    private DelegateExecution execution;

    // TODO: add services
    @Test
    void execute() {
        String requestId = "ADS_1-T00003-NCOM-1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(nonComplianceCancelledService, times(1)).cancel(requestId);
    }
}
