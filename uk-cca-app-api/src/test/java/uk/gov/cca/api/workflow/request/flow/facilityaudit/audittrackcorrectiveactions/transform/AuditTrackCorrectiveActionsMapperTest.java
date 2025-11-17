package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.CorrectiveActionFollowUpResponse;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuditTrackCorrectiveActionsMapperTest {

    private AuditTrackCorrectiveActionsMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(AuditTrackCorrectiveActionsMapper.class);
    }

    @Test
    void toAuditTrackCorrectiveActionsSubmittedRequestActionPayload() {
        final Map<String, String> sectionsCompleted = Map.of("section1", "COMPLETED");
        final String correctiveActionTitle = "Corrective Action 1";
        final UUID fileUUID = UUID.randomUUID();
        final String filename = "testFile";
        Map<UUID, String> attachments = Map.of(fileUUID, filename);
        final CorrectiveAction correctiveAction = CorrectiveAction.builder()
                .title(correctiveActionTitle)
                .details("bla bla")
                .deadline(LocalDate.now())
                .build();
        final CorrectiveActionFollowUpResponse correctiveActionFollowUpResponse = CorrectiveActionFollowUpResponse.builder()
                .isActionCarriedOut(false)
                .comments("bla bla bla bla")
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
                .respondedActions(Set.of(correctiveActionTitle))
                .facilityAuditAttachments(attachments)
                .sectionsCompleted(sectionsCompleted)
                .build();

        AuditTrackCorrectiveActionsSubmittedRequestActionPayload actionPayload =
                mapper.toAuditTrackCorrectiveActionsSubmittedRequestActionPayload(taskPayload);

        // verify
        assertThat(actionPayload.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED_PAYLOAD);
        assertThat(taskPayload.getAuditTrackCorrectiveActions()).isEqualTo(actionPayload.getAuditTrackCorrectiveActions());
        assertThat(taskPayload.getAttachments()).isEqualTo(actionPayload.getAttachments());
    }

}
