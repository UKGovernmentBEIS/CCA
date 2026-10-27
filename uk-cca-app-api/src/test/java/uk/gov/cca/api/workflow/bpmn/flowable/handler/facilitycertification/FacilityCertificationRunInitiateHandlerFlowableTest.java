package uk.gov.cca.api.workflow.bpmn.flowable.handler.facilitycertification;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service.FacilityCertificationRunInitiateService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationRunInitiateHandlerFlowableTest {

    @InjectMocks
    private FacilityCertificationRunInitiateHandlerFlowable handler;

    @Mock
    private FacilityCertificationRunInitiateService facilityCertificationRunInitiateService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        when(facilityCertificationRunInitiateService.isValidForFacilityCertificationRun()).thenReturn(true);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(facilityCertificationRunInitiateService, times(1)).isValidForFacilityCertificationRun();
        verify(execution, times(1))
                .setVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_RUN_INITIATE_FLAG, true);
    }
}
