package uk.gov.cca.api.workflow.bpmn.handler.cca3existingfacilitiesmigration;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.service.Cca3ExistingFacilitiesMigrationCreateRunService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationRunCreateHandlerTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationRunCreateHandler handler;

    @Mock
    private Cca3ExistingFacilitiesMigrationCreateRunService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {

        // Invoke
        handler.execute(execution);

        // Verify
        verify(service, times(1)).createCca3ExistingFacilitiesMigrationRun();
    }
}
