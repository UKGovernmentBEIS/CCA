package uk.gov.cca.api.workflow.bpmn.handler.targetunitaccountcreation;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service.TargetUnitAccountApplyUnderlyingAgreementService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountApplyUnderlyingAgreementHandlerTest {

    @InjectMocks
    private TargetUnitAccountApplyUnderlyingAgreementHandler handler;

    @Mock
    private TargetUnitAccountApplyUnderlyingAgreementService targetUnitAccountApplyUnderlyingAgreementService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final long accountId = 1L;

        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(targetUnitAccountApplyUnderlyingAgreementService, times(1)).execute(accountId);
    }

    @Test
    void execute_throw_error() {
        final long accountId = 1L;

        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        doThrow(new BusinessException((RESOURCE_NOT_FOUND))).when(targetUnitAccountApplyUnderlyingAgreementService)
                .execute(accountId);

        // Invoke
        Exception ex = assertThrows(Exception.class,
                () -> handler.execute(execution));

        // Verify
        assertThat(ex).isInstanceOf(BpmnError.class);
        verify(targetUnitAccountApplyUnderlyingAgreementService, times(1)).execute(accountId);
    }
}
