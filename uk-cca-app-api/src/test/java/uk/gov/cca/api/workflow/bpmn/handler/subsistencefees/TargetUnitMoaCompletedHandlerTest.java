package uk.gov.cca.api.workflow.bpmn.handler.subsistencefees;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service.MoaCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class TargetUnitMoaCompletedHandlerTest {

	@InjectMocks
    private TargetUnitMoaCompletedHandler handler;

    @Mock
    private MoaCompletedService moaCompletedService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        final String moaRequestId = "moa-request-id";
        final long accountId = 50L;
        final int numberOfAccountsCompleted = 10;
        final boolean accountMoaSucceeded = true;
        final List<String> accountMoaErrors = List.of();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_ID)).thenReturn(moaRequestId);
        when(execution.getVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_SUCCEEDED))
        		.thenReturn(accountMoaSucceeded);
        when(execution.getVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_ERRORS))
        		.thenReturn(accountMoaErrors);
        when(execution.getVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED))
                .thenReturn(numberOfAccountsCompleted);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_SUCCEEDED);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_ERRORS);
        verify(moaCompletedService, times(1)).completed(requestId, accountId, MoaType.TARGET_UNIT_MOA, moaRequestId, accountMoaSucceeded, accountMoaErrors);
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 11);
    }
}
