package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditDTO;
import uk.gov.cca.api.facilityaudit.service.FacilityAuditService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.AuditReasonDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreAuditReviewSubmitInitializerTest {

    @InjectMocks
    private PreAuditReviewSubmitInitializer handler;

    @Mock
    private FacilityAuditService facilityAuditService;

    @Test
    void initializePayload() {
        final FacilityAuditRequestPayload requestPayload = FacilityAuditRequestPayload.builder()
                .build();
        final Long facilityId = 12L;
        final List<FacilityAuditReasonType> reasonsForAudit = List.of(FacilityAuditReasonType.NON_COMPLIANCE, FacilityAuditReasonType.ELIGIBILITY);
        final String comment = "bla bla bla";

        final Request request = Request.builder()
                .id("ADS_1-T00001-AUDT-1")
                .payload(requestPayload)
                .requestResources(List.of(RequestResource.builder()
                        .resourceId(facilityId.toString())
                        .resourceType(CcaResourceType.FACILITY)
                        .build()))
                .build();

        final PreAuditReviewSubmitRequestTaskPayload expected =
                PreAuditReviewSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.PRE_AUDIT_REVIEW_SUBMIT_PAYLOAD)
                        .preAuditReviewDetails(PreAuditReviewDetails.builder()
                                .auditReasonDetails(AuditReasonDetails.builder()
                                        .reasonsForAudit(reasonsForAudit)
                                        .comment(comment)
                                        .build())
                                .build())
                        .sectionsCompleted(Map.of())
                        .build();

        final FacilityAuditDTO facilityAuditDTO = FacilityAuditDTO.builder()
                .reasons(reasonsForAudit)
                .comments(comment)
                .build();

        when(facilityAuditService.getFacilityAuditByFacilityId(facilityId)).thenReturn(facilityAuditDTO);

        // Invoke
        RequestTaskPayload actual = handler.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(PreAuditReviewSubmitRequestTaskPayload.class).isEqualTo(expected);
        PreAuditReviewSubmitRequestTaskPayload preAuditReviewSubmitRequestTaskPayload = (PreAuditReviewSubmitRequestTaskPayload) actual;

        assertThat(preAuditReviewSubmitRequestTaskPayload.getPreAuditReviewDetails()).isNotNull();
        assertThat(preAuditReviewSubmitRequestTaskPayload.getPreAuditReviewDetails().getAuditReasonDetails()).isNotNull();
        assertThat(preAuditReviewSubmitRequestTaskPayload.getPreAuditReviewDetails().getAuditReasonDetails().getReasonsForAudit()).isEqualTo(facilityAuditDTO.getReasons());
        assertThat(preAuditReviewSubmitRequestTaskPayload.getPreAuditReviewDetails().getAuditReasonDetails().getComment()).isEqualTo(facilityAuditDTO.getComments());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.PRE_AUDIT_REVIEW_SUBMIT);
    }
}
