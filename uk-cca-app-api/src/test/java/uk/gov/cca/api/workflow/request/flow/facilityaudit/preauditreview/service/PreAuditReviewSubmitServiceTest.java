package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewDetails;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.AuditReasonDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.AuditDetermination;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.RequestedDocuments;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PreAuditReviewSubmitServiceTest {

    @InjectMocks
    private PreAuditReviewSubmitService preAuditReviewSubmitService;

    @Test
    void applySaveAction() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final PreAuditReviewDetails preAuditReviewDetails = PreAuditReviewDetails.builder()
                .auditReasonDetails(AuditReasonDetails.builder()
                        .reasonsForAudit(List.of(FacilityAuditReasonType.REPORTING_DATA, FacilityAuditReasonType.NON_COMPLIANCE))
                        .build())
                .requestedDocuments(RequestedDocuments.builder()
                        .annotatedSitePlansFile(UUID.randomUUID())
                        .build())
                .auditDetermination(AuditDetermination.builder()
                        .furtherAuditNeeded(true)
                        .reviewCompletionDate(LocalDate.of(2022, 2, 2))
                        .build())
                .build();
        final PreAuditReviewSubmitSaveRequestTaskActionPayload taskActionPayload =
                PreAuditReviewSubmitSaveRequestTaskActionPayload.builder()
                        .preAuditReviewDetails(preAuditReviewDetails)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(PreAuditReviewSubmitRequestTaskPayload.builder().build())
                .build();

        // Invoke
        preAuditReviewSubmitService.applySaveAction(taskActionPayload, requestTask);

        // Verify
        PreAuditReviewSubmitRequestTaskPayload actual =
                (PreAuditReviewSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getPreAuditReviewDetails()).isEqualTo(preAuditReviewDetails);
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void submitPreAuditReview() {
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
        final PreAuditReviewSubmitRequestTaskPayload requestTaskPayload = PreAuditReviewSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PRE_AUDIT_REVIEW_SUBMIT_PAYLOAD)
                .preAuditReviewDetails(preAuditReviewDetails)
                .sectionsCompleted(sectionsCompleted)
                .facilityAuditAttachments(Map.of(annotatedSitePlansFileUUID, filename))
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(FacilityAuditRequestPayload.builder().build())
                        .build())
                .payload(requestTaskPayload)
                .build();

        // Invoke
        preAuditReviewSubmitService.submitPreAuditReview(requestTask);

        final Request request = requestTask.getRequest();
        FacilityAuditRequestPayload actual = (FacilityAuditRequestPayload) request.getPayload();
        assertThat(actual.getPreAuditReviewDetails()).isEqualTo(preAuditReviewDetails);
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(actual.getPreAuditReviewAttachments()).isEqualTo(Map.of(annotatedSitePlansFileUUID, filename));
    }

}
