package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service.FacilityCertificationAccountProcessingCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationAccountsProcessingTriggerHandlerTest {

    @InjectMocks
    private FacilityCertificationAccountsProcessingTriggerHandler handler;

    @Mock
    private FacilityCertificationAccountProcessingCreateRequestService facilityCertificationAccountProcessingCreateRequestService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final long accountId = 1L;
        final String requestId = "request-id";
        final String requestBusinessKey ="bk-request-id";

        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.BUSINESS_KEY)).thenReturn(requestBusinessKey);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.BUSINESS_KEY);
        verify(facilityCertificationAccountProcessingCreateRequestService, times(1))
                .createRequest(accountId, requestId, requestBusinessKey);
    }
}
