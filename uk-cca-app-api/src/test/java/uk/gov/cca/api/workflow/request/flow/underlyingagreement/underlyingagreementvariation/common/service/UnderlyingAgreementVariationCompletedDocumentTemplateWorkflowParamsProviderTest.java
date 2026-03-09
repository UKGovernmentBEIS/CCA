package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeVersionsHelperService;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationRegulatorLedDetermination;
import uk.gov.netz.api.common.constants.RoleTypeConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    
    @Mock
    private UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;

    @Test
    void constructParams() {
        final int version = 1;
        final Map<SchemeVersion, Integer> consolidationNumberMap = Map.of(SchemeVersion.CCA_2, version);
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final Set<Facility> facility = Set.of(
		        Facility.builder()
		                .status(FacilityStatus.LIVE)
		                .facilityItem(FacilityItem.builder()
		                        .facilityDetails(FacilityDetails.builder()
		                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
		                                .build())
		                        .build())
		                .build()
		);
		final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .determination(VariationDetermination.builder()
                        .determination(Determination.builder()
                                .additionalInformation("additional information")
                                .build())
                        .build())
                .originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(facility)
                                .build())
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
        when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(facility))
        		.thenReturn(Set.of(SchemeVersion.CCA_2));

        // Invoke
        Map<String, Object> actual = paramsProvider.constructParams(requestPayload);

        // Verify
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(Map.of(
                "targetUnitDetails", "test1",
                "isRegulatorLed", false,
                "additionalInformation", "additional information",
                "versionMap", Map.of("CCA2", "1")
        ));
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails);
        verify(documentTemplateTransformationMapper, times(1))
                .constructVersionMap(consolidationNumberMap);
    }

    @Test
    void constructParams_regulator_led() {
        final int version = 1;
        final Map<SchemeVersion, Integer> consolidationNumberMap = Map.of(SchemeVersion.CCA_2, version);
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final Set<Facility> facility = Set.of(
		        Facility.builder()
		                .status(FacilityStatus.LIVE)
		                .facilityItem(FacilityItem.builder()
		                        .facilityDetails(FacilityDetails.builder()
		                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
		                                .build())
		                        .build())
		                .build()
		);
		final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .initiatorRoleType(RoleTypeConstants.REGULATOR)
                .originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(facility)
                                .build())
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .underlyingAgreementVersionMap(consolidationNumberMap)
                .regulatorLedDetermination(VariationRegulatorLedDetermination.builder()
                        .additionalInformation("Some regulator additional information")
                        .build())
                .build();

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails))
                .thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));
        when(documentTemplateTransformationMapper.constructVersionMap(consolidationNumberMap))
                .thenReturn(Map.of("CCA2", "1"));
        when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(facility))
				.thenReturn(Set.of(SchemeVersion.CCA_2));

        // Invoke
        Map<String, Object> actual = paramsProvider.constructParams(requestPayload);

        // Verify
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(Map.of(
                "targetUnitDetails", "test1",
                "isRegulatorLed", true,
                "additionalInformation", "Some regulator additional information",
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
