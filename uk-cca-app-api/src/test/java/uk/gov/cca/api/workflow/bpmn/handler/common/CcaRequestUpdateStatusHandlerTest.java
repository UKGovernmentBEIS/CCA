package uk.gov.cca.api.workflow.bpmn.handler.common;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaRequestUpdateStatusHandlerTest {

    @InjectMocks
    private CcaRequestUpdateStatusHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private RequestService requestService;

    @Test
    void execute() {
        String requestId = "1";
        String status = "APPROVED";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_STATUS)).thenReturn(status);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_STATUS);
        verify(requestService, times(1)).updateRequestStatus(requestId, status);
    }
}
