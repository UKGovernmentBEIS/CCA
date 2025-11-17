package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuditDetailsCorrectiveActionsSubmitInitializerTest {

    @InjectMocks
    private AuditDetailsCorrectiveActionsSubmitInitializer handler;

    @Test
    void initializePayload() {
        final FacilityAuditRequestPayload requestPayload = FacilityAuditRequestPayload.builder()
                .build();

        final Request request = Request.builder()
                .id("ADS_1-T00001-AUDT-1")
                .payload(requestPayload)
                .build();

        final AuditDetailsCorrectiveActionsSubmitRequestTaskPayload expected =
                AuditDetailsCorrectiveActionsSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_PAYLOAD)
                        .sectionsCompleted(Map.of())
                        .build();
        // Invoke
        RequestTaskPayload actual = handler.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(AuditDetailsCorrectiveActionsSubmitRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT);
    }

}
