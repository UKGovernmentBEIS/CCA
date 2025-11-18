package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.AuditDetermination;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.AuditReasonDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.RequestedDocuments;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PreAuditReviewSubmitMapperTest {

    private PreAuditReviewSubmitMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(PreAuditReviewSubmitMapper.class);
    }

    @Test
    void toPreAuditReviewSubmittedRequestActionPayload() {

        final PreAuditReviewSubmitRequestTaskPayload requestTaskPayload = PreAuditReviewSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PRE_AUDIT_REVIEW_SUBMIT_PAYLOAD)
                .preAuditReviewDetails(PreAuditReviewDetails.builder()
                        .auditReasonDetails(AuditReasonDetails.builder()
                                .reasonsForAudit(List.of(FacilityAuditReasonType.NON_COMPLIANCE))
                                .comment("bla bla bla")
                                .build())
                        .requestedDocuments(RequestedDocuments.builder()
                                .auditMaterialReceivedDate(LocalDate.of(2024, 3, 15))
                                .annotatedSitePlansFile(UUID.randomUUID())
                                .build())
                        .auditDetermination(AuditDetermination.builder()
                                .reviewCompletionDate(LocalDate.of(2024, 3, 15))
                                .furtherAuditNeeded(true)
                                .reviewComments("test comments")
                                .build())
                        .build())
                .build();

        PreAuditReviewSubmittedRequestActionPayload requestActionPayload =
                mapper.toPreAuditReviewSubmittedRequestActionPayload(requestTaskPayload);

        assertThat(requestActionPayload.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED_PAYLOAD);
        assertThat(requestTaskPayload.getPreAuditReviewDetails())
                .isEqualTo(requestActionPayload.getPreAuditReviewDetails());
        assertThat(requestActionPayload.getAttachments()).isEqualTo(requestTaskPayload.getAttachments());
    }

    @Test
    void toPreAuditReviewDetails() {
        final FacilityAuditDTO facilityAuditDTO = FacilityAuditDTO.builder()
                .reasons(List.of(FacilityAuditReasonType.NON_COMPLIANCE, FacilityAuditReasonType.ELIGIBILITY))
                .comments("bla bla bla")
                .build();

        PreAuditReviewDetails preAuditReviewDetails = mapper.toPreAuditReviewDetails(facilityAuditDTO);

        assertThat(preAuditReviewDetails).isNotNull();
        assertThat(preAuditReviewDetails.getAuditReasonDetails()).isNotNull();
        assertThat(preAuditReviewDetails.getAuditReasonDetails().getReasonsForAudit()).isEqualTo(facilityAuditDTO.getReasons());
        assertThat(preAuditReviewDetails.getAuditReasonDetails().getComment()).isEqualTo(facilityAuditDTO.getComments());
    }
}
