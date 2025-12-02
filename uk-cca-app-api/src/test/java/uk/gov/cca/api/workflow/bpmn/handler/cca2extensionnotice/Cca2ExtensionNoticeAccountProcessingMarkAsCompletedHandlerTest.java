package uk.gov.cca.api.workflow.bpmn.handler.cca2extensionnotice;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeAccountProcessingMarkAsCompletedHandlerTest {

    @InjectMocks
    private Cca2ExtensionNoticeAccountProcessingMarkAsCompletedHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private RequestService requestService;

    @Test
    void execute() throws Exception {
        final String requestId = "requestId";
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(1L)
                .errors(List.of())
                .build();
        Request request = Request.builder().build();

        when(execution.getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE)).thenReturn(accountState);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountState.isSucceeded()).isTrue();
        assertThat(request.getSubmissionDate()).isNotNull();
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoMoreInteractions(execution);
    }

    @Test
    void execute_with_errors() throws Exception {
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(1L)
                .errors(List.of("error"))
                .build();

        when(execution.getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountState.isSucceeded()).isFalse();
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE);
        verify(execution, times(1))
                .setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        verifyNoMoreInteractions(execution);
        verifyNoInteractions(requestService);
    }
}
