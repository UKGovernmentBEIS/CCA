package uk.gov.cca.api.workflow.bpmn.handler.cca2extensionnotice;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service.Cca2ExtensionNoticeAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeAccountProcessingHandlerTest {

    @InjectMocks
    private Cca2ExtensionNoticeAccountProcessingHandler handler;

    @Mock
    private Cca2ExtensionNoticeAccountProcessingService cca2ExtensionNoticeAccountProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(1L)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountState.getErrors()).isEmpty();
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE);
        verify(cca2ExtensionNoticeAccountProcessingService, times(1))
                .doProcess(requestId, accountState);
    }

    @Test
    void execute_PerformanceDataException() throws Exception {
        final String requestId = "request-id";
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(1L)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE)).thenReturn(accountState);
        doThrow(new BpmnExecutionException(List.of("error")))
                .when(cca2ExtensionNoticeAccountProcessingService).doProcess(requestId, accountState);

        // Invoke
        assertDoesNotThrow(() -> handler.execute(execution));

        // Verify
        assertThat(accountState.getErrors()).isNotEmpty();
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE);
        verify(cca2ExtensionNoticeAccountProcessingService, times(1))
                .doProcess(requestId, accountState);
    }
}
