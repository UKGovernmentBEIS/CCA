package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationAccountProcessingMarkAsCompletedHandlerTest {

    @InjectMocks
    private FacilityCertificationAccountProcessingMarkAsCompletedHandler handler;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .accountId(1L)
                .facilitiesCertified(10L)
                .build();

        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountState.isSucceeded()).isTrue();
        assertThat(accountState.getFacilitiesCertified()).isEqualTo(10L);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }

    @Test
    void execute_with_errors() throws Exception {
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .accountId(1L)
                .facilitiesCertified(10L)
                .errors(List.of("error1", "error2"))
                .build();

        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE)).thenReturn(accountState);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountState.isSucceeded()).isFalse();
        assertThat(accountState.getFacilitiesCertified()).isZero();
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
