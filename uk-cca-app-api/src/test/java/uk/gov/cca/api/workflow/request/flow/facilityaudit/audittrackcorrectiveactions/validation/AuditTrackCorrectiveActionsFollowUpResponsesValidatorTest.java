package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation.FacilityAuditViolation;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.CorrectiveActionFollowUpResponse;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuditTrackCorrectiveActionsFollowUpResponsesValidatorTest {

    @InjectMocks
    private AuditTrackCorrectiveActionsFollowUpResponsesValidator validator;

    @Test
    void validateCompletedResponses() {
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
        final AuditCorrectiveActionResponse auditCorrectiveActionResponse = AuditCorrectiveActionResponse.builder()
                .action(correctiveAction)
                .response(correctiveActionFollowUpResponse)
                .build();
        Map<String, AuditCorrectiveActionResponse> correctiveActionResponses = Map.of(correctiveActionTitle, auditCorrectiveActionResponse);
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

        BusinessValidationResult businessValidationResult = validator.validateCompletedResponses(taskPayload);

        // verify
        assertThat(businessValidationResult.isValid()).isTrue();
    }

    @Test
    void validateCompletedResponses_not_valid() {
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
                .respondedActions(Set.of())
                .facilityAuditAttachments(attachments)
                .sectionsCompleted(sectionsCompleted)
                .build();

        BusinessValidationResult businessValidationResult = validator.validateCompletedResponses(taskPayload);

        // verify
        List<? extends BusinessViolation> violations = businessValidationResult.getViolations();
        assertThat(businessValidationResult.isValid()).isFalse();
        assertThat(violations).hasSize(1);
        assertThat((FacilityAuditViolation) violations.getFirst()).extracting(FacilityAuditViolation::getMessage)
                .isEqualTo(FacilityAuditViolation.FacilityAuditViolationMessage.MISSING_CORRECTIVE_ACTION_RESPONSES.getMessage());
    }

    @Test
    void validateResponseReference() {
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
                .facilityAuditAttachments(attachments)
                .respondedActions(Set.of(correctiveActionTitle))
                .sectionsCompleted(sectionsCompleted)
                .build();

        BusinessValidationResult businessValidationResult = validator.validateResponseReference(taskPayload, correctiveActionTitle);

        // verify
        assertThat(businessValidationResult.isValid()).isTrue();
    }

    @Test
    void validateResponseReference_not_valid() {
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
        final String unknownReference = "unknownReference";

        BusinessValidationResult businessValidationResult = validator.validateResponseReference(taskPayload, unknownReference);

        // verify
        List<? extends BusinessViolation> violations = businessValidationResult.getViolations();
        assertThat(businessValidationResult.isValid()).isFalse();
        assertThat(violations).hasSize(1);
        assertThat((FacilityAuditViolation) violations.getFirst()).extracting(FacilityAuditViolation::getMessage)
                .isEqualTo(FacilityAuditViolation.FacilityAuditViolationMessage.REFERENCE_NOT_FOUND.getMessage());
    }
}
