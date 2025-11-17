package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewDetails;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation.FacilityAuditViolation;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.AuditReasonDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.AuditDetermination;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.RequestedDocuments;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreAuditReviewSubmitValidatorTest {

    @InjectMocks
    private PreAuditReviewSubmitValidator preAuditReviewSubmitValidator;

    @Mock
    private DataValidator<PreAuditReviewDetails> dataValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final UUID annotatedSitePlansFileUUID = UUID.randomUUID();
        final String filename = "testFile";
        final PreAuditReviewDetails preAuditReviewDetails = PreAuditReviewDetails.builder()
                .auditReasonDetails(AuditReasonDetails.builder()
                        .reasonsForAudit(List.of(FacilityAuditReasonType.REPORTING_DATA, FacilityAuditReasonType.NON_COMPLIANCE))
                        .build())
                .requestedDocuments(RequestedDocuments.builder()
                        .annotatedSitePlansFile(annotatedSitePlansFileUUID)
                        .build())
                .auditDetermination(AuditDetermination.builder()
                        .furtherAuditNeeded(true)
                        .reviewCompletionDate(LocalDate.of(2022, 2, 2))
                        .build())
                .build();
        final PreAuditReviewSubmitRequestTaskPayload taskPayload = PreAuditReviewSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PRE_AUDIT_REVIEW_SUBMIT_PAYLOAD)
                .preAuditReviewDetails(preAuditReviewDetails)
                .sectionsCompleted(sectionsCompleted)
                .facilityAuditAttachments(Map.of(annotatedSitePlansFileUUID, filename))
                .build();

        when(dataValidator.validate(taskPayload.getPreAuditReviewDetails()))
                .thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getFacilityAuditAttachments().keySet()))
                .thenReturn(true);

        // Invoke
        preAuditReviewSubmitValidator.validate(taskPayload);

        // Verify
        verify(dataValidator, times(1)).validate(taskPayload.getPreAuditReviewDetails());
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getFacilityAuditAttachments().keySet());
    }

    @Test
    void validate_not_valid() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final UUID annotatedSitePlansFileUUID = UUID.randomUUID();
        final String filename = "testFile";
        final PreAuditReviewDetails preAuditReviewDetails = PreAuditReviewDetails.builder()
                .auditReasonDetails(AuditReasonDetails.builder()
                        .reasonsForAudit(List.of(FacilityAuditReasonType.REPORTING_DATA, FacilityAuditReasonType.NON_COMPLIANCE))
                        .build())
                .requestedDocuments(RequestedDocuments.builder()
                        .annotatedSitePlansFile(annotatedSitePlansFileUUID)
                        .build())
                .auditDetermination(AuditDetermination.builder()
                        .furtherAuditNeeded(true)
                        .reviewCompletionDate(LocalDate.of(2022, 2, 2))
                        .build())
                .build();
        final PreAuditReviewSubmitRequestTaskPayload taskPayload = PreAuditReviewSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PRE_AUDIT_REVIEW_SUBMIT_PAYLOAD)
                .preAuditReviewDetails(preAuditReviewDetails)
                .sectionsCompleted(sectionsCompleted)
                .facilityAuditAttachments(Map.of(annotatedSitePlansFileUUID, filename))
                .build();

        when(dataValidator.validate(taskPayload.getPreAuditReviewDetails()))
                .thenReturn(Optional.of(new FacilityAuditViolation(PreAuditReviewDetails.class.getName(),
                        FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_PRE_AUDIT_MATERIAL_REVIEW_DATA)));
        when(fileAttachmentsExistenceValidator.valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getFacilityAuditAttachments().keySet()))
                .thenReturn(true);

        // Invoke
        BusinessException businessException =
                assertThrows(BusinessException.class, () -> preAuditReviewSubmitValidator.validate(taskPayload));

        // Verify
        Assertions.assertEquals(CcaErrorCode.INVALID_FACILITY_AUDIT, businessException.getErrorCode());
        verify(dataValidator, times(1)).validate(taskPayload.getPreAuditReviewDetails());
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getFacilityAuditAttachments().keySet());
    }
}
