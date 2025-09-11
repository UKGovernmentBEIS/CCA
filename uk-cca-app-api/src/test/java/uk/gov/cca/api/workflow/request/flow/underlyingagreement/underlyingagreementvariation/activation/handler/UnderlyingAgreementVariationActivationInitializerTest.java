package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationActivationInitializerTest {

    @InjectMocks
    private UnderlyingAgreementVariationActivationInitializer initializer;

    @Test
    void initializePayload() {
        final Request request = Request.builder()
                .id("ADS_1-T00001-UNA")
                .build();

        final UnderlyingAgreementVariationActivationRequestTaskPayload expected =
                UnderlyingAgreementVariationActivationRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_PAYLOAD)
                        .build();
        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(UnderlyingAgreementVariationActivationRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION);
    }
}
