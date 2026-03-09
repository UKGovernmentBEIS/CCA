package uk.gov.cca.api.workflow.bpmn.flowable.handler.cca2termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.domain.Cca2TerminationAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.service.Cca2TerminationAccountProcessingCompletedService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationAccountProcessingMarkAsCompletedHandlerFlowableTest {

	@InjectMocks
    private Cca2TerminationAccountProcessingMarkAsCompletedHandlerFlowable handler;

	@Mock
    private Cca2TerminationAccountProcessingCompletedService cca2TerminationAccountProcessingCompletedService;
	
	@Mock
	private RequestService requestService;
	
    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final Cca2TerminationAccountState accountState = Cca2TerminationAccountState.builder()
                .accountId(1L)
                .build();
        final Request request = Request.builder()
        		.metadata(Cca2TerminationAccountProcessingRequestMetadata.builder()
        				.parentRequestId("parentRequestId")
        				.build())
        		.build();

		when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn("requestId");
		when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(1L);
		when(requestService.findRequestById("requestId")).thenReturn(request);

        when(execution.getVariable(CcaBpmnProcessConstants.CCA2_TERMINATION_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountState.getSucceeded()).isTrue();
        verify(cca2TerminationAccountProcessingCompletedService, times(1))
        		.completed("parentRequestId", "requestId", 1L, accountState);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.CCA2_TERMINATION_ACCOUNT_STATE);
        verify(execution, never()).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }

    @Test
    void execute_with_errors() {
        final Cca2TerminationAccountState accountState = Cca2TerminationAccountState.builder()
                .accountId(1L)
                .errors(List.of("error1", "error2"))
                .build();

        final Request request = Request.builder()
        		.metadata(Cca2TerminationAccountProcessingRequestMetadata.builder()
        				.parentRequestId("parentRequestId")
        				.build())
        		.build();

		when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn("requestId");
		when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(1L);
		when(requestService.findRequestById("requestId")).thenReturn(request);

        when(execution.getVariable(CcaBpmnProcessConstants.CCA2_TERMINATION_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountState.getSucceeded()).isFalse();
        verify(cca2TerminationAccountProcessingCompletedService, times(1))
				.completed("parentRequestId", "requestId", 1L, accountState);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.CCA2_TERMINATION_ACCOUNT_STATE);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
