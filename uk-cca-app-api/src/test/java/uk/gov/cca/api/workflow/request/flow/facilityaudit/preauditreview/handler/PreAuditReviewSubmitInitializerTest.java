package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PreAuditReviewSubmitInitializerTest {

    @InjectMocks
    private PreAuditReviewSubmitInitializer handler;

    @Test
    void initializePayload() {
        final FacilityAuditRequestPayload requestPayload = FacilityAuditRequestPayload.builder()
                .build();

        final Request request = Request.builder()
                .id("ADS_1-T00001-AUDT-1")
                .payload(requestPayload)
                .build();

        final PreAuditReviewSubmitRequestTaskPayload expected =
                PreAuditReviewSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.PRE_AUDIT_REVIEW_SUBMIT_PAYLOAD)
                        .sectionsCompleted(Map.of())
                        .build();
        // Invoke
        RequestTaskPayload actual = handler.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(PreAuditReviewSubmitRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.PRE_AUDIT_REVIEW_SUBMIT);
    }
}
