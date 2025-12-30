package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationCompletedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    UnderlyingAgreementVariationCompletedDocumentTemplateWorkflowParamsProvider paramsProvider;

    @Mock
    private DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Test
    void constructParams() {
        final VariationDetermination determination = VariationDetermination.builder()
                .determination(Determination.builder()
                        .additionalInformation("additional information")
                        .build())
                .build();
        final int version = 1;
        final Map<SchemeVersion, Integer> consolidationNumberMap = Map.of(SchemeVersion.CCA_2, version);
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .determination(determination)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .underlyingAgreementVersionMap(consolidationNumberMap)
                .build();

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails))
                .thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));
        when(documentTemplateTransformationMapper.constructVersionMap(consolidationNumberMap))
                .thenReturn(Map.of("CCA2", "1"));

        // Invoke
        Map<String, Object> actual = paramsProvider.constructParams(requestPayload);

        // Verify
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(Map.of(
                "targetUnitDetails", "test1",
                "additionalInformation", "additional information",
                "versionMap", Map.of("CCA2", "1")
        ));
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails);
        verify(documentTemplateTransformationMapper, times(1))
                .constructVersionMap(consolidationNumberMap);
    }

    @Test
    void getContextActionType() {
        assertThat(paramsProvider.getContextActionType())
                .isEqualTo(CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_COMPLETED);
    }
}
