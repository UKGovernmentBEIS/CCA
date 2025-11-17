package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service.Cca3ExistingFacilitiesMigrationAccountProcessingActivationService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveActionHandlerTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivationService cca3ExistingFacilitiesMigrationAccountProcessingActivationService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_SAVE_APPLICATION;
        final Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveRequestTaskActionPayload taskActionPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveRequestTaskActionPayload.builder().build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingActivationService, times(1))
                .applySaveAction(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_SAVE_APPLICATION);
    }
}
