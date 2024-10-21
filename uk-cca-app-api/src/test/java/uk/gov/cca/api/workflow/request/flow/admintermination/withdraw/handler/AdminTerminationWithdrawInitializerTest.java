package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminTerminationWithdrawInitializerTest {

    @InjectMocks
    private AdminTerminationWithdrawInitializer initializer;

    @Test
    void initializePayload() {
        final Request request = Request.builder()
                .id("UNA-ADS_1T00001")
                .accountId(1L)
                .build();

        final AdminTerminationWithdrawRequestTaskPayload expected =
                AdminTerminationWithdrawRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_WITHDRAW_PAYLOAD)
                        .build();
        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(AdminTerminationWithdrawRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_WITHDRAW);
    }
}
