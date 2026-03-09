package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
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
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationRegulatorLedDetermination;
import uk.gov.netz.api.common.constants.RoleTypeConstants;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationAcceptedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private UnderlyingAgreementVariationAcceptedDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
    
    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
    
    @Mock
    private UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED);
    }

    @Test
    void constructParams() {
        final int version = 100;
        final Map<SchemeVersion, Integer> consolidationNumberMap = Map.of(SchemeVersion.CCA_2, version, SchemeVersion.CCA_3, version);
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final Set<Facility> facilities = Set.of(Facility.builder()
				.facilityItem(FacilityItem.builder()
						.facilityDetails(FacilityDetails.builder()
								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2)).build()
								).build()
						)
				.status(FacilityStatus.EXCLUDED)
				.build(), 
				Facility.builder()
				.facilityItem(FacilityItem.builder()
						.facilityDetails(FacilityDetails.builder()
								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_3)).build()
								).build()
						)
				.status(FacilityStatus.LIVE)
				.build()
				);
		final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
        		.facilities(facilities)
        		.build();
        UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .underlyingAgreementVersionMap(consolidationNumberMap)
                .determination(VariationDetermination.builder()
                		.determination(
						Determination.builder()
								.additionalInformation("Some additional information")
								.build())
						.build())
                .build();
        
        Map<String, String> transformedMap = Map.of("CCA2", "v1", "CCA3", "v1");

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails))
                .thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));
        when(documentTemplateTransformationMapper.constructVersionMap(consolidationNumberMap))
        		.thenReturn(transformedMap);
        when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(facilities))
				.thenReturn(Set.of(SchemeVersion.CCA_3));

        // Invoke
        Map<String, Object> result = provider.constructParams(payload);

        // Verify
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails);
        verify(underlyingAgreementSchemeVersionsHelperService, times(2))
        		.calculateSchemeVersionsFromActiveFacilities(facilities);
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
        		"targetUnitDetails", "test1", 
        		"versionMap", transformedMap,
        		"terminatedSchemeVersion", "",
        		"additionalInformation", "Some additional information",
				"isRegulatorLed", false));
    }

	@Test
	void constructParams_regulator_led() {
		final int version = 100;
		final Map<SchemeVersion, Integer> consolidationNumberMap = Map.of(SchemeVersion.CCA_2, version, SchemeVersion.CCA_3, version);
		final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
		final Set<Facility> facilities = Set.of(Facility.builder()
				.facilityItem(FacilityItem.builder()
						.facilityDetails(FacilityDetails.builder()
								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2)).build()
								).build()
						)
				.status(FacilityStatus.EXCLUDED)
				.build(), 
				Facility.builder()
				.facilityItem(FacilityItem.builder()
						.facilityDetails(FacilityDetails.builder()
								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_3)).build()
								).build()
						)
				.status(FacilityStatus.LIVE)
				.build()
				);
		final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
				.facilities(Set.of(Facility.builder()
								.facilityItem(FacilityItem.builder()
										.facilityDetails(FacilityDetails.builder()
												.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2)).build()
										).build()
								)
								.status(FacilityStatus.EXCLUDED)
								.build(),
						Facility.builder()
								.facilityItem(FacilityItem.builder()
										.facilityDetails(FacilityDetails.builder()
												.participatingSchemeVersions(Set.of(SchemeVersion.CCA_3)).build()
										).build()
								)
								.status(FacilityStatus.LIVE)
								.build()
				))
				.build();
		UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
				.initiatorRoleType(RoleTypeConstants.REGULATOR)
				.underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
						.underlyingAgreementTargetUnitDetails(targetUnitDetails)
						.build())
				.underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
						.underlyingAgreementTargetUnitDetails(targetUnitDetails)
						.underlyingAgreement(underlyingAgreement)
						.build())
				.originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder()
						.underlyingAgreement(underlyingAgreement)
						.build())
				.underlyingAgreementVersionMap(consolidationNumberMap)
				.regulatorLedDetermination(VariationRegulatorLedDetermination.builder()
						.additionalInformation("Some regulator additional information")
						.build())
				.build();

		Map<String, String> transformedMap = Map.of("CCA2", "v1", "CCA3", "v1");

		when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails))
				.thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));
		when(documentTemplateTransformationMapper.constructVersionMap(consolidationNumberMap))
				.thenReturn(transformedMap);
		when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(facilities))
				.thenReturn(Set.of(SchemeVersion.CCA_3));

		// Invoke
		Map<String, Object> result = provider.constructParams(payload);

		// Verify
		verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
				.constructTargetUnitDetailsTemplateParams(targetUnitDetails);
		verify(underlyingAgreementSchemeVersionsHelperService, times(2))
				.calculateSchemeVersionsFromActiveFacilities(facilities);
		assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
				"targetUnitDetails", "test1",
				"versionMap", transformedMap,
				"terminatedSchemeVersion", "",
				"additionalInformation", "Some regulator additional information",
				"isRegulatorLed", true));
	}
}
