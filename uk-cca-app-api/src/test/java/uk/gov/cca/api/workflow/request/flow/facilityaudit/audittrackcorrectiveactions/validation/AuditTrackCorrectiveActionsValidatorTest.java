package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation.FacilityAuditViolation;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.CorrectiveActionFollowUpResponse;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditTrackCorrectiveActionsValidatorTest {

    @InjectMocks
    private AuditTrackCorrectiveActionsValidator validator;

    @Mock
    private DataValidator<AuditTrackCorrectiveActions> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate() {
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

        when(dataValidator.validate(taskPayload.getAuditTrackCorrectiveActions())).thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getFacilityAuditAttachments().keySet()))
                .thenReturn(true);

        // invoke
        BusinessValidationResult businessValidationResult = validator.validate(taskPayload);

        // verify
        assertThat(businessValidationResult.isValid()).isTrue();
        verify(dataValidator, times(1)).validate(taskPayload.getAuditTrackCorrectiveActions());
        verify(fileAttachmentsExistenceValidator, times(1)).valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getFacilityAuditAttachments().keySet());
    }

    @Test
    void validate_not_valid() {
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
                .isActionCarriedOut(true)
                .comments("bla bla bla bla")
                .evidenceFiles(Set.of(fileUUID))
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

        when(dataValidator.validate(taskPayload.getAuditTrackCorrectiveActions())).thenReturn(
                Optional.of(new FacilityAuditViolation(AuditTrackCorrectiveActions.class.getName(),
                        FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_TRACK_CORRECTIVE_ACTIONS_DATA)));
        when(fileAttachmentsExistenceValidator.valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getFacilityAuditAttachments().keySet()))
                .thenReturn(true);

        // invoke
        BusinessValidationResult businessValidationResult = validator.validate(taskPayload);

        // verify
        assertThat(businessValidationResult.isValid()).isFalse();
        verify(dataValidator, times(1)).validate(taskPayload.getAuditTrackCorrectiveActions());
        verify(fileAttachmentsExistenceValidator, times(1)).valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getFacilityAuditAttachments().keySet());
    }
}
