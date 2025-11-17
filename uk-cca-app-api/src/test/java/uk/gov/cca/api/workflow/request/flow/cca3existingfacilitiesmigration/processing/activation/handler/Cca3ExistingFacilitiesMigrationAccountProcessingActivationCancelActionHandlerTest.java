package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3FacilitiesActivationOutcome;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingActivationCancelActionHandlerTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivationCancelActionHandler handler;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String processId = "processId";
        final String requestTaskActionType = CcaRequestTaskActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCEL_APPLICATION;
        final RequestTaskActionEmptyPayload taskActionPayload = RequestTaskActionEmptyPayload.builder().build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).processTaskId(processId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(workflowService, times(1)).completeTask(processId, Map.of(
                CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_AGREEMENT_OUTCOME, Cca3FacilitiesActivationOutcome.CANCELLED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCEL_APPLICATION);
    }
}
