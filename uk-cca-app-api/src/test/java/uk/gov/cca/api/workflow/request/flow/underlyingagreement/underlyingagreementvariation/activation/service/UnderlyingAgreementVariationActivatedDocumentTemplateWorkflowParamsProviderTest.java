package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationActivatedDocumentTemplateWorkflowParamsProviderTest {


    @InjectMocks
    private UnderlyingAgreementVariationActivatedDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED);
    }

    @Test
    void constructParams() {
        final int version = 1;
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreementVersion(1)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .build();

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails, version + 1))
                .thenReturn(Map.of("test", "test"));

        // Invoke
        provider.constructParams(payload);

        // Verify
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails, version + 1);
    }
}
