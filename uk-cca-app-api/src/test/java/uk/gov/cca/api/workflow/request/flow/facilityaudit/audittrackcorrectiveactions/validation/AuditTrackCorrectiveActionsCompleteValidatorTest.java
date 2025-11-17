package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation.FacilityAuditViolation;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.CorrectiveActionFollowUpResponse;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditTrackCorrectiveActionsCompleteValidatorTest {

    @InjectMocks
    private AuditTrackCorrectiveActionsCompleteValidator validator;

    @Mock
    private AuditTrackCorrectiveActionsValidator auditTrackCorrectiveActionsValidator;

    @Mock
    private AuditTrackCorrectiveActionsFollowUpResponsesValidator auditTrackCorrectiveActionsFollowUpResponsesValidator;

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
                .auditTrackCorrectiveActions(auditTrackCorrectiveActions)
                .respondedActions(Set.of(correctiveActionTitle))
                .facilityAuditAttachments(attachments)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final BusinessValidationResult emptyBusinessValidationResult = BusinessValidationResult.builder().valid(true).build();

        when(auditTrackCorrectiveActionsValidator.validate(taskPayload)).thenReturn(emptyBusinessValidationResult);
        when(auditTrackCorrectiveActionsFollowUpResponsesValidator.validateCompletedResponses(taskPayload)).thenReturn(emptyBusinessValidationResult);

        // invoke
        validator.validate(taskPayload);

        // verify
        verify(auditTrackCorrectiveActionsFollowUpResponsesValidator, times(1)).validateCompletedResponses(taskPayload);
        verify(auditTrackCorrectiveActionsValidator, times(1)).validate(taskPayload);
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
                .auditTrackCorrectiveActions(auditTrackCorrectiveActions)
                .respondedActions(Set.of())
                .facilityAuditAttachments(attachments)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final BusinessValidationResult emptyBusinessValidationResult = BusinessValidationResult.builder().valid(true).build();
        final FacilityAuditViolation facilityAuditViolation = new FacilityAuditViolation(AuditTrackCorrectiveActions.class.getName(),
                FacilityAuditViolation.FacilityAuditViolationMessage.MISSING_CORRECTIVE_ACTION_RESPONSES);
        final BusinessValidationResult errorBusinessValidationResult = BusinessValidationResult.builder()
                .valid(false)
                .violations(List.of(facilityAuditViolation))
                .build();

        when(auditTrackCorrectiveActionsValidator.validate(taskPayload)).thenReturn(emptyBusinessValidationResult);
        when(auditTrackCorrectiveActionsFollowUpResponsesValidator.validateCompletedResponses(taskPayload)).thenReturn(errorBusinessValidationResult);

        // Invoke
        BusinessException businessException =
                assertThrows(BusinessException.class, () -> validator.validate(taskPayload));

        // Verify
        assertThat(CcaErrorCode.INVALID_FACILITY_AUDIT).isEqualTo(businessException.getErrorCode());
        assertThat(businessException.getData()).containsExactly(facilityAuditViolation);
        verify(auditTrackCorrectiveActionsFollowUpResponsesValidator, times(1)).validateCompletedResponses(taskPayload);
        verify(auditTrackCorrectiveActionsValidator, times(1)).validate(taskPayload);
    }
}
