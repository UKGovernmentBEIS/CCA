package uk.gov.cca.api.workflow.request.flow.admintermination.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminTerminationSubmitInitializerTest {

    @InjectMocks
    private AdminTerminationSubmitInitializer handler;

    @Test
    void initializePayload() {

        final AdminTerminationReasonDetails adminTerminationReasonDetails = AdminTerminationReasonDetails.builder()
                .reason(AdminTerminationReason.FAILURE_TO_COMPLY)
                .build();
        final AdminTerminationRequestPayload requestPayload = AdminTerminationRequestPayload.builder()
                .adminTerminationReasonDetails(adminTerminationReasonDetails)
                .build();
        final Request request = Request.builder()
                .id("UNA-ADS_1T00001")
                .payload(requestPayload)
                .build();
        final AdminTerminationSubmitRequestTaskPayload expected =
                AdminTerminationSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_SUBMIT_PAYLOAD)
                        .adminTerminationReasonDetails(adminTerminationReasonDetails)
                        .build();
        // Invoke
        RequestTaskPayload actual = handler.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(AdminTerminationSubmitRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_SUBMIT);
    }
}
