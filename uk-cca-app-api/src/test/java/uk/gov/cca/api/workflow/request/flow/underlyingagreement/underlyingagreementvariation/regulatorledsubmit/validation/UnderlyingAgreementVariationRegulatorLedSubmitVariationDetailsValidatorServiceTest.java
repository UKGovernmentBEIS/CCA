package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService service;

    @Test
    void getUnderlyingAgreementPayload() {
        final UnderlyingAgreementVariationPayload variationPayload = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder()
                        .operatorName("operatorName")
                        .build())
                .build();
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .underlyingAgreement(variationPayload)
                        .build();
        // Invoke
        UnderlyingAgreementVariationPayload result = service.getUnderlyingAgreementPayload(taskPayload);

        // Verify
        assertThat(result).isEqualTo(variationPayload);
    }

    @Test
    void getPayloadType() {
        assertThat(service.getPayloadType()).isEmpty();
    }
}
