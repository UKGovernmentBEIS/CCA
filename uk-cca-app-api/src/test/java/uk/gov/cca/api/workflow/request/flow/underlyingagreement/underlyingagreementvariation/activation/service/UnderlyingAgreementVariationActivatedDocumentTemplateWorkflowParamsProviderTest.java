package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED);
    }

    @Test
    void constructParams() {
        final Map<SchemeVersion, Integer> consolidationNumberMap = Map.of(
                SchemeVersion.CCA_2, 2,
                SchemeVersion.CCA_3, 1
        );
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreementVersionMap(consolidationNumberMap)
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(
                                        Facility.builder()
                                                .status(FacilityStatus.LIVE)
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityDetails(FacilityDetails.builder()
                                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                                                .build())
                                                        .build())
                                                .build()
                                ))
                                .build())
                        .build())
                .build();

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails))
                .thenReturn(new HashMap<>(Map.of("account", "test1")));
        when(documentTemplateTransformationMapper.constructVersionMap(Map.of(SchemeVersion.CCA_2, 3)))
                .thenReturn(Map.of("CCA2", "3"));

        // Invoke
        Map<String, Object> params = provider.constructParams(payload);

        // Verify
        assertThat(params).containsExactlyInAnyOrderEntriesOf(Map.of(
                "account", "test1",
                "versionMap", Map.of("CCA2", "3")
        ));
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails);
        verify(documentTemplateTransformationMapper, times(1))
                .constructVersionMap(Map.of(SchemeVersion.CCA_2, 3));
    }

    @Test
    void constructParams_for_new_document() {
        final Map<SchemeVersion, Integer> consolidationNumberMap = Map.of(
                SchemeVersion.CCA_3, 1
        );
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreementVersionMap(consolidationNumberMap)
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(
                                        Facility.builder()
                                                .status(FacilityStatus.LIVE)
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityDetails(FacilityDetails.builder()
                                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                                                .build())
                                                        .build())
                                                .build()
                                ))
                                .build())
                        .build())
                .build();

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails))
                .thenReturn(new HashMap<>(Map.of("account", "test1")));
        when(documentTemplateTransformationMapper.constructVersionMap(Map.of(SchemeVersion.CCA_2, 1)))
                .thenReturn(Map.of("CCA2", "1"));

        // Invoke
        Map<String, Object> params = provider.constructParams(payload);

        // Verify
        assertThat(params).containsExactlyInAnyOrderEntriesOf(Map.of(
                "account", "test1",
                "versionMap", Map.of("CCA2", "1")
        ));
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails);
        verify(documentTemplateTransformationMapper, times(1))
                .constructVersionMap(Map.of(SchemeVersion.CCA_2, 1));
    }
}
