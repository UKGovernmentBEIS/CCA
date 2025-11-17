package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation.FacilityAuditViolation;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.CorrectiveActionFollowUpResponse;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditTrackCorrectiveActionsSubmitValidatorTest {

    @InjectMocks
    private AuditTrackCorrectiveActionsSubmitValidator validator;

    @Mock
    private DataValidator<AuditCorrectiveActionResponse> correctiveActionResponseDataValidator;

    @Mock
    private AuditTrackCorrectiveActionsFollowUpResponsesValidator auditTrackCorrectiveActionsFollowUpResponsesValidator;

    @Test
    void validate_valid() {
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
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();
        final AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload actionPayload = AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload.builder()
                .actionTitle(correctiveActionTitle)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final BusinessValidationResult emptyBusinessValidationResult = BusinessValidationResult.builder().valid(true).build();

        when(correctiveActionResponseDataValidator.validate(auditCorrectiveActionResponse)).thenReturn(Optional.empty());
        when(auditTrackCorrectiveActionsFollowUpResponsesValidator.validateResponseReference(taskPayload, actionPayload.getActionTitle())).thenReturn(emptyBusinessValidationResult);

        // invoke
        validator.validate(requestTask, actionPayload);

        // verify
        verify(auditTrackCorrectiveActionsFollowUpResponsesValidator, times(1)).validateResponseReference(taskPayload, actionPayload.getActionTitle());
        verify(correctiveActionResponseDataValidator, times(1)).validate(auditCorrectiveActionResponse);
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
                .respondedActions(Set.of(correctiveActionTitle))
                .facilityAuditAttachments(attachments)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();
        final AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload actionPayload = AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload.builder()
                .actionTitle(correctiveActionTitle)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final BusinessValidationResult emptyBusinessValidationResult = BusinessValidationResult.builder().valid(true).build();
        FacilityAuditViolation facilityAuditViolation = new FacilityAuditViolation(AuditTrackCorrectiveActions.class.getName(),
                FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_TRACK_CORRECTIVE_ACTIONS_DATA);

        when(correctiveActionResponseDataValidator.validate(auditCorrectiveActionResponse)).thenReturn(Optional.of(facilityAuditViolation));
        when(auditTrackCorrectiveActionsFollowUpResponsesValidator.validateResponseReference(taskPayload, actionPayload.getActionTitle())).thenReturn(emptyBusinessValidationResult);

        // Invoke
        BusinessException businessException =
                assertThrows(BusinessException.class, () -> validator.validate(requestTask, actionPayload));

        // Verify
        Assertions.assertEquals(CcaErrorCode.INVALID_FACILITY_AUDIT, businessException.getErrorCode());
        verify(correctiveActionResponseDataValidator, times(1)).validate(auditCorrectiveActionResponse);
        verify(auditTrackCorrectiveActionsFollowUpResponsesValidator, times(1)).validateResponseReference(taskPayload, actionPayload.getActionTitle());
    }
}
