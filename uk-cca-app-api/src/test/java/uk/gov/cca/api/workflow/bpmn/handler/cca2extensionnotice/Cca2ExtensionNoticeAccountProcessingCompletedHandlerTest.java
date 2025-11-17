package uk.gov.cca.api.workflow.bpmn.handler.cca2extensionnotice;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.run.service.Cca2ExtensionNoticeRunService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeAccountProcessingCompletedHandlerTest {

    @InjectMocks
    private Cca2ExtensionNoticeAccountProcessingCompletedHandler handler;

    @Mock
    private Cca2ExtensionNoticeRunService cca2ExtensionNoticeRunService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        final Long accountId = 1L;
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(accountId)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE))
                .thenReturn(accountState);
        when(execution.getVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED))
                .thenReturn(0);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE);
        verify(cca2ExtensionNoticeRunService, times(1))
                .accountProcessingCompleted(requestId, accountId, accountState);
        verify(execution, times(1))
                .getVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED);
        verify(execution, times(1))
                .setVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 1);
    }
}
