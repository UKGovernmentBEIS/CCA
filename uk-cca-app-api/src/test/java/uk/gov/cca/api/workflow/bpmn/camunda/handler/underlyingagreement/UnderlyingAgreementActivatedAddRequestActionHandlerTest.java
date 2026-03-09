package uk.gov.cca.api.workflow.bpmn.camunda.handler.underlyingagreement;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.service.UnderlyingAgreementActivatedAddRequestActionService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementActivatedAddRequestActionHandlerTest {

	@InjectMocks
	UnderlyingAgreementActivatedAddRequestActionHandler handler;
    
    @Mock
    private UnderlyingAgreementActivatedAddRequestActionService requestActionService;
    
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
