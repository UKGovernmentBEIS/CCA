package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsAndCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditTechnique;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.CorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation.FacilityAuditViolation;
import uk.gov.netz.api.common.exception.BusinessException;

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
class AuditDetailsCorrectiveActionsSubmitValidatorTest {

    @InjectMocks
    private AuditDetailsCorrectiveActionsSubmitValidator validator;

    @Mock
    private DataValidator<AuditDetailsAndCorrectiveActions> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final UUID fileUuid = UUID.randomUUID();
        final String filename = "testFile";

        final AuditDetailsAndCorrectiveActions auditDetailsAndCorrectiveActions = AuditDetailsAndCorrectiveActions.builder()
                .auditDetails(AuditDetails.builder()
                        .auditTechnique(AuditTechnique.DESK_BASED_INTERVIEW)
                        .auditDate(LocalDate.of(2025, 2, 2))
                        .finalAuditReportDate(LocalDate.of(2025, 2, 2))
                        .comments("bla bla bla bla")
                        .auditDocuments(Set.of(fileUuid))
                        .build())
                .correctiveActions(CorrectiveActions.builder()
                        .hasActions(true)
                        .actions(Set.of(CorrectiveAction.builder()
                                .title("title")
                                .deadline(LocalDate.of(2022, 3, 3))
                                .details("bla bla bla bla")
                                .build()))
                        .build())
                .build();

        final AuditDetailsCorrectiveActionsSubmitRequestTaskPayload requestTaskPayload = AuditDetailsCorrectiveActionsSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_PAYLOAD)
                .auditDetailsAndCorrectiveActions(auditDetailsAndCorrectiveActions)
                .sectionsCompleted(sectionsCompleted)
                .facilityAuditAttachments(Map.of(fileUuid, filename))
                .build();


        when(dataValidator.validate(requestTaskPayload.getAuditDetailsAndCorrectiveActions()))
                .thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(requestTaskPayload.getReferencedAttachmentIds(), requestTaskPayload.getFacilityAuditAttachments().keySet()))
                .thenReturn(true);

        // Invoke
        validator.validate(requestTaskPayload);

        // Verify
        verify(dataValidator, times(1)).validate(requestTaskPayload.getAuditDetailsAndCorrectiveActions());
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(requestTaskPayload.getReferencedAttachmentIds(), requestTaskPayload.getFacilityAuditAttachments().keySet());
    }

    @Test
    void validate_not_valid() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final UUID fileUuid = UUID.randomUUID();
        final String filename = "testFile";

        final AuditDetailsAndCorrectiveActions auditDetailsAndCorrectiveActions = AuditDetailsAndCorrectiveActions.builder()
                .auditDetails(AuditDetails.builder()
                        .auditTechnique(null)
                        .auditDate(LocalDate.of(2025, 2, 2))
                        .finalAuditReportDate(LocalDate.of(2025, 2, 2))
                        .comments("bla bla bla bla")
                        .auditDocuments(Set.of(fileUuid))
                        .build())
                .correctiveActions(CorrectiveActions.builder()
                        .hasActions(true)
                        .actions(Set.of(CorrectiveAction.builder()
                                .title("title")
                                .deadline(LocalDate.of(2022, 3, 3))
                                .details("bla bla bla bla")
                                .build()))
                        .build())
                .build();

        final AuditDetailsCorrectiveActionsSubmitRequestTaskPayload requestTaskPayload = AuditDetailsCorrectiveActionsSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_PAYLOAD)
                .auditDetailsAndCorrectiveActions(auditDetailsAndCorrectiveActions)
                .sectionsCompleted(sectionsCompleted)
                .facilityAuditAttachments(Map.of(fileUuid, filename))
                .build();

        when(dataValidator.validate(requestTaskPayload.getAuditDetailsAndCorrectiveActions()))
                .thenReturn(Optional.of(new FacilityAuditViolation(AuditDetailsAndCorrectiveActions.class.getName(),
                        FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_AUDIT_DETAILS_CORRECTIVE_ACTIONS_DATA)));
        when(fileAttachmentsExistenceValidator.valid(requestTaskPayload.getReferencedAttachmentIds(), requestTaskPayload.getFacilityAuditAttachments().keySet()))
                .thenReturn(true);

        // Invoke
        BusinessException businessException =
                assertThrows(BusinessException.class, () -> validator.validate(requestTaskPayload));

        // Verify
        Assertions.assertEquals(CcaErrorCode.INVALID_FACILITY_AUDIT, businessException.getErrorCode());
        verify(dataValidator, times(1)).validate(requestTaskPayload.getAuditDetailsAndCorrectiveActions());
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(requestTaskPayload.getReferencedAttachmentIds(), requestTaskPayload.getFacilityAuditAttachments().keySet());
    }
}
