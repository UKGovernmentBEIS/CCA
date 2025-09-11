package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service.FacilityCertificationRunInitiateService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationRunInitiateHandlerTest {

    @InjectMocks
    private FacilityCertificationRunInitiateHandler handler;

    @Mock
    private FacilityCertificationRunInitiateService facilityCertificationRunInitiateService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_with_account_ids() throws Exception {
        when(execution.getProcessInstance()).thenReturn(execution);
        when(execution.hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)).thenReturn(true);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getProcessInstance();
        verify(execution, times(1)).hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS);
        verifyNoInteractions(facilityCertificationRunInitiateService);
        verify(execution, times(1))
                .setVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_RUN_INITIATE_FLAG, true);
    }

    @Test
    void execute_without_account_ids() throws Exception {
        when(execution.getProcessInstance()).thenReturn(execution);
        when(execution.hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)).thenReturn(false);
        when(facilityCertificationRunInitiateService.isValidForFacilityCertificationRun()).thenReturn(true);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getProcessInstance();
        verify(execution, times(1)).hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS);
        verify(facilityCertificationRunInitiateService, times(1)).isValidForFacilityCertificationRun();
        verify(execution, times(1))
                .setVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_RUN_INITIATE_FLAG, true);
    }

    @Test
    void execute_no_valid_run() throws Exception {
        when(execution.getProcessInstance()).thenReturn(execution);
        when(execution.hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS)).thenReturn(false);
        when(facilityCertificationRunInitiateService.isValidForFacilityCertificationRun()).thenReturn(false);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getProcessInstance();
        verify(execution, times(1)).hasVariable(CcaBpmnProcessConstants.ACCOUNT_IDS);
        verify(facilityCertificationRunInitiateService, times(1)).isValidForFacilityCertificationRun();
        verify(execution, times(1))
                .setVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_RUN_INITIATE_FLAG, false);
    }
}
