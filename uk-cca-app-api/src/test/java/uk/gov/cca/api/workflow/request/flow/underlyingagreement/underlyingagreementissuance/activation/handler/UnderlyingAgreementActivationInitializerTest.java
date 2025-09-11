package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.domain.UnderlyingAgreementActivationRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementActivationInitializerTest {

	@InjectMocks
    private UnderlyingAgreementActivationInitializer initializer;

    @Test
    void initializePayload() {
        final Request request = Request.builder()
                .id("ADS_1-T00001-UNA")
                .build();

        final UnderlyingAgreementActivationRequestTaskPayload expected =
        		UnderlyingAgreementActivationRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_ACTIVATION_PAYLOAD)
                        .build();
        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(UnderlyingAgreementActivationRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.UNDERLYING_AGREEMENT_APPLICATION_ACTIVATION);
    }
}
