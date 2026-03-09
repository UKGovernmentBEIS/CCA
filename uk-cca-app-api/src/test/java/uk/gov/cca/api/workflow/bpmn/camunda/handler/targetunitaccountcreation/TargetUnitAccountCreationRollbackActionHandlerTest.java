package uk.gov.cca.api.workflow.bpmn.camunda.handler.targetunitaccountcreation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service.TargetUnitAccountCreationRollbackService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountCreationRollbackActionHandlerTest {

    @InjectMocks
    private TargetUnitAccountCreationRollbackActionHandler handler;

    @Mock
    private TargetUnitAccountCreationRollbackService targetUnitAccountCreationRollbackService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        doNothing().when(targetUnitAccountCreationRollbackService).rollback(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(targetUnitAccountCreationRollbackService, times(1)).rollback(requestId);
    }
}
