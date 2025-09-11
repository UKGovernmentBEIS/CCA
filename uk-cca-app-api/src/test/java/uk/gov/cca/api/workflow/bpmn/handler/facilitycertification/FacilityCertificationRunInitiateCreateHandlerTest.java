package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service.FacilityCertificationRunInitiateService;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationRunInitiateCreateHandlerTest {

    @InjectMocks
    private FacilityCertificationRunInitiateCreateHandler handler;

    @Mock
    private FacilityCertificationRunInitiateService facilityCertificationRunInitiateService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_with_account_ids() throws Exception {
        final List<String> providedAccountIds = List.of("accountId1", "accountId2");
        final CertificationPeriodType type = CertificationPeriodType.CP7;

        when(execution.getProcessInstance()).thenReturn(execution);
        when(execution.hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)).thenReturn(true);
        when(execution.getVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)).thenReturn(providedAccountIds);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_PERIOD)).thenReturn(type.name());

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(3)).getProcessInstance();
        verify(execution, times(1)).hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.ACCOUNT_IDS);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_PERIOD);
        verify(facilityCertificationRunInitiateService, times(1))
                .createFacilityCertificationRun(providedAccountIds, type);
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
        verify(facilityCertificationRunInitiateService, times(1))
                .createFacilityCertificationRun();
        verifyNoMoreInteractions(execution);
    }
}
