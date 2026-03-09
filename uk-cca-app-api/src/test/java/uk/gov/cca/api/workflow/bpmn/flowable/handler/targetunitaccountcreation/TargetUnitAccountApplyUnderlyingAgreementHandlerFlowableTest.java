package uk.gov.cca.api.workflow.bpmn.flowable.handler.targetunitaccountcreation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service.TargetUnitAccountApplyUnderlyingAgreementService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountApplyUnderlyingAgreementHandlerFlowableTest {

	@InjectMocks
    private TargetUnitAccountApplyUnderlyingAgreementHandlerFlowable handler;

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
}
