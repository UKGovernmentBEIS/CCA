package uk.gov.cca.api.workflow.bpmn.handler.facilitycertification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationRunInitiateHandlerTest {

    @InjectMocks
    private FacilityCertificationRunInitiateHandler handler;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_with_account_ids() throws Exception {
        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1))
                .setVariable(CcaBpmnProcessConstants.FACILITY_CERTIFICATION_RUN_INITIATE_FLAG, false);
    }
}
