package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingActivationServiceTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivationService service;

    @Mock
    private RequestService requestService;

    @Test
    void applySaveAction() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails details =
                Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails.builder()
                        .comments("test")
                        .build();
        final Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveRequestTaskActionPayload taskActionPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveRequestTaskActionPayload.builder()
                        .activationDetails(details)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload.builder()
                        .build())
                .build();

        // Invoke
        service.applySaveAction(taskActionPayload, requestTask);

        // Verify
        Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload actual =
                (Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getActivationDetails()).isEqualTo(details);
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void notifyOperator() {
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "file.png");
        final Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails details =
                Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails.builder()
                        .evidenceFiles(Set.of(UUID.randomUUID()))
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder().build())
                        .build())
                .payload(Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload.builder()
                        .activationDetails(details)
                        .activationAttachments(attachments)
                        .build())
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();

        // Invoke
        service.notifyOperator(requestTask, decisionNotification);

        // Verify
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload savePayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestTask.getRequest().getSubmissionDate()).isNotNull();
        assertThat(savePayload.getActivationDetails()).isEqualTo(details);
        assertThat(savePayload.getActivationAttachments()).isEqualTo(attachments);
        assertThat(savePayload.getDecisionNotification()).isEqualTo(decisionNotification);
    }

    @Test
    void cancel() {
        final String requestId = "requestId";
        final String requestActionType = CcaRequestActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCELLED;
        final String regulatorAssignee = "regulatorAssignee";
        final Request request = Request.builder()
                .id(requestId)
                .payload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .regulatorAssignee(regulatorAssignee)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.cancel(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
                .addActionToRequest(request, null, requestActionType, regulatorAssignee);
    }
}
