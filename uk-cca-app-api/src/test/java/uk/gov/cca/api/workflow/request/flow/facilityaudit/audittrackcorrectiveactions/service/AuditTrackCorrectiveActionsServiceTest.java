package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.CorrectiveActionFollowUpResponse;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuditTrackCorrectiveActionsServiceTest {

    @InjectMocks
    private AuditTrackCorrectiveActionsService service;

    @Test
    void applySaveAction() {
        final Map<String, String> sectionsCompleted = Map.of("section1", "COMPLETED");
        final String correctiveActionTitle = "Corrective Action 1";
        final CorrectiveAction correctiveAction = CorrectiveAction.builder()
                .title(correctiveActionTitle)
                .details("bla bla")
                .deadline(LocalDate.now())
                .build();
        final CorrectiveActionFollowUpResponse correctiveActionFollowUpResponse = CorrectiveActionFollowUpResponse.builder()
                .isActionCarriedOut(false)
                .comments("bla bla bla bla")
                .build();
        final AuditTrackCorrectiveActionsSaveRequestTaskActionPayload taskActionPayload = AuditTrackCorrectiveActionsSaveRequestTaskActionPayload.builder()
                .payloadType(CcaRequestTaskActionPayloadType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SAVE_PAYLOAD)
                .actionTitle(correctiveActionTitle)
                .correctiveActionFollowUpResponse(correctiveActionFollowUpResponse)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final AuditCorrectiveActionResponse auditCorrectiveActionResponse = AuditCorrectiveActionResponse.builder()
                .action(correctiveAction)
                .response(correctiveActionFollowUpResponse)
                .build();
        final Map<String, AuditCorrectiveActionResponse> correctiveActionResponses = Map.of(correctiveActionTitle, auditCorrectiveActionResponse);
        final AuditTrackCorrectiveActions auditTrackCorrectiveActions = AuditTrackCorrectiveActions.builder()
                .correctiveActionResponses(correctiveActionResponses)
                .build();
        final AuditTrackCorrectiveActionsRequestTaskPayload taskPayload = AuditTrackCorrectiveActionsRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.AUDIT_TRACK_CORRECTIVE_ACTIONS_PAYLOAD)
                .auditTrackCorrectiveActions(auditTrackCorrectiveActions)
                .respondedActions(new HashSet<>(Set.of(correctiveActionTitle)))
                .sectionsCompleted(sectionsCompleted)
                .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();
        final Map<String, AuditCorrectiveActionResponse> expectedCorrectiveActionResponses =
                Map.of(correctiveActionTitle, AuditCorrectiveActionResponse.builder()
                        .action(correctiveAction)
                        .response(correctiveActionFollowUpResponse)
                        .build());

        // invoke
        service.applySaveAction(taskActionPayload, requestTask);

        // verify
        AuditTrackCorrectiveActionsRequestTaskPayload actual = (AuditTrackCorrectiveActionsRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getRespondedActions()).isEmpty();
        assertThat(actual.getAuditTrackCorrectiveActions().getCorrectiveActionResponses()).isEqualTo(expectedCorrectiveActionResponses);
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void applySubmitAction() {
        final Map<String, String> sectionsCompleted = Map.of("section1", "COMPLETED");
        final String correctiveActionTitle = "Corrective Action 1";
        final CorrectiveAction correctiveAction = CorrectiveAction.builder()
                .title(correctiveActionTitle)
                .details("bla bla")
                .deadline(LocalDate.now())
                .build();
        final CorrectiveActionFollowUpResponse correctiveActionFollowUpResponse = CorrectiveActionFollowUpResponse.builder()
                .isActionCarriedOut(false)
                .comments("bla bla bla bla")
                .build();
        final AuditCorrectiveActionResponse auditTrackCorrectiveAction = AuditCorrectiveActionResponse.builder()
                .response(correctiveActionFollowUpResponse)
                .action(correctiveAction)
                .build();
        final AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload taskActionPayload = AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload.builder()
                .payloadType(CcaRequestTaskActionPayloadType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMIT_PAYLOAD)
                .actionTitle(correctiveActionTitle)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final AuditTrackCorrectiveActionsRequestTaskPayload taskPayload = AuditTrackCorrectiveActionsRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.AUDIT_TRACK_CORRECTIVE_ACTIONS_PAYLOAD)
                .auditTrackCorrectiveActions(AuditTrackCorrectiveActions.builder()
                        .correctiveActionResponses(Map.of(correctiveActionTitle, auditTrackCorrectiveAction))
                        .build())
                .sectionsCompleted(sectionsCompleted)
                .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();
        final Set<String> expectedRespondedItems = Set.of(taskActionPayload.getActionTitle());

        // invoke
        service.applySubmitAction(taskActionPayload, requestTask);

        // verify
        AuditTrackCorrectiveActionsRequestTaskPayload actual = (AuditTrackCorrectiveActionsRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getRespondedActions()).isEqualTo(expectedRespondedItems);
    }

    @Test
    void applyCompleteAction() {
        final Map<String, String> sectionsCompleted = Map.of("section1", "COMPLETED");
        final String correctiveActionTitle = "Corrective Action 1";
        final UUID fileUUID = UUID.randomUUID();
        final String filename = "testFile";
        final Map<UUID, String> attachments = Map.of(fileUUID, filename);
        final CorrectiveAction correctiveAction = CorrectiveAction.builder()
                .title(correctiveActionTitle)
                .details("bla bla")
                .deadline(LocalDate.now())
                .build();
        final CorrectiveActionFollowUpResponse correctiveActionFollowUpResponse = CorrectiveActionFollowUpResponse.builder()
                .isActionCarriedOut(false)
                .comments("bla bla bla bla")
                .build();
        final AuditCorrectiveActionResponse auditTrackCorrectiveAction = AuditCorrectiveActionResponse.builder()
                .response(correctiveActionFollowUpResponse)
                .action(correctiveAction)
                .build();
        final AuditTrackCorrectiveActions auditTrackCorrectiveActions = AuditTrackCorrectiveActions.builder()
                .correctiveActionResponses(Map.of(correctiveActionTitle, auditTrackCorrectiveAction))
                .build();
        final AuditTrackCorrectiveActionsRequestTaskPayload taskPayload = AuditTrackCorrectiveActionsRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.AUDIT_TRACK_CORRECTIVE_ACTIONS_PAYLOAD)
                .auditTrackCorrectiveActions(auditTrackCorrectiveActions)
                .respondedActions(Set.of(correctiveActionTitle))
                .facilityAuditAttachments(attachments)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(Request.builder()
                        .payload(FacilityAuditRequestPayload.builder().build())
                        .build())
                .build();

        // invoke
        service.applyCompleteAction(requestTask);

        // verify
        final Request request = requestTask.getRequest();
        FacilityAuditRequestPayload actual = (FacilityAuditRequestPayload) request.getPayload();

        assertThat(actual.getAuditTrackCorrectiveActions()).isEqualTo(auditTrackCorrectiveActions);
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(actual.getAuditTrackCorrectiveActionsAttachments()).isEqualTo(attachments);
    }
}
