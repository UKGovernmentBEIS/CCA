package uk.gov.cca.api.workflow.bpmn.handler.cca2extensionnotice;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.service.Cca2ExtensionNoticeCreateRunService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeRunInitiateCreateHandlerTest {

    @InjectMocks
    private Cca2ExtensionNoticeRunInitiateCreateHandler handler;

    @Mock
    private Cca2ExtensionNoticeCreateRunService cca2ExtensionNoticeRunInitiateService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_with_account_ids() throws Exception {
        final List<String> providedAccounts = List.of("account1", "account2");

        when(execution.getProcessInstance()).thenReturn(execution);
        when(execution.hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)).thenReturn(true);
        when(execution.getVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)).thenReturn(providedAccounts);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(2)).getProcessInstance();
        verify(execution, times(1)).hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.ACCOUNT_IDS);
        verify(cca2ExtensionNoticeRunInitiateService, times(1)).createRun(Set.of("account1", "account2"));
    }

    @Test
    void execute_without_account_ids() throws Exception {
        when(execution.getProcessInstance()).thenReturn(execution);
        when(execution.hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)).thenReturn(false);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getProcessInstance();
        verify(execution, times(1)).hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS);
        verify(cca2ExtensionNoticeRunInitiateService, times(1)).createRun(Set.of());
        verifyNoMoreInteractions(execution);
    }
}
