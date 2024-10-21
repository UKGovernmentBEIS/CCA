package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreement;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.service.UnderlyingAgreementRejectedAddRequestActionService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementRejectedAddRequestActionHandlerTest {
	
    @InjectMocks
    UnderlyingAgreementRejectedAddRequestActionHandler handler;
    
    @Mock
    private UnderlyingAgreementRejectedAddRequestActionService requestActionService;
    
    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        verify(requestActionService, times(1)).addRequestAction(requestId);
    }
}