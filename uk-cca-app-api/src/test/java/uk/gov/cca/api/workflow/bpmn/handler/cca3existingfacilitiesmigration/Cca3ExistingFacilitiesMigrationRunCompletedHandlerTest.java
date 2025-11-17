package uk.gov.cca.api.workflow.bpmn.handler.cca3existingfacilitiesmigration;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.run.service.Cca3ExistingFacilitiesMigrationRunService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationRunCompletedHandlerTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationRunCompletedHandler handler;

    @Mock
    private Cca3ExistingFacilitiesMigrationRunService cca3ExistingFacilitiesMigrationRunService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "requestId";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(cca3ExistingFacilitiesMigrationRunService, times(1)).complete(requestId);
    }
}
