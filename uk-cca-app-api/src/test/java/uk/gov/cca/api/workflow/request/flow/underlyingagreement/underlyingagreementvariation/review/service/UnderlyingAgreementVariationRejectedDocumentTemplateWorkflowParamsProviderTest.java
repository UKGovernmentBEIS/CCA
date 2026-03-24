package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeVersionsHelperService;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_REJECTED;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRejectedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    UnderlyingAgreementVariationRejectedDocumentTemplateWorkflowParamsProvider paramsProvider;

    @Mock
    private DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
    
    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
    
    @Mock
    private UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;

    @Test
    void getContextActionType() {
        assertThat(paramsProvider.getContextActionType())
                .isEqualTo(UNDERLYING_AGREEMENT_VARIATION_REJECTED);
    }

    @Test
    void constructParams() {
        final VariationDetermination determination = VariationDetermination.builder()
                .determination(Determination.builder()
                        .reason("reason")
                        .additionalInformation("additionalInformation")
                        .build())
                .build();
        final int version = 1;
        final Map<SchemeVersion, Integer> consolidationNumberMap = Map.of(SchemeVersion.CCA_2, version, SchemeVersion.CCA_3, version);
        final FacilityItem facilityItem1 = FacilityItem.builder()
                .facilityDetails(FacilityDetails.builder()
                        .previousFacilityId("Prv1")
                        .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                        .build())
                .build();
        final Facility facility = Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(facilityItem1)
                .build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .determination(determination)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(UnderlyingAgreement.builder().facilities(Set.of(facility)).build())
                        .build())
                .underlyingAgreementVersionMap(consolidationNumberMap)
                .build();

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails))
                .thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));
        when(documentTemplateTransformationMapper.constructVersionMap(Map.of(SchemeVersion.CCA_3, version)))
                .thenReturn(Map.of("CCA3", "1"));
        when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(Set.of(facility)))
        		.thenReturn(Set.of(SchemeVersion.CCA_3));

        // Invoke
        Map<String, Object> actual = paramsProvider.constructParams(requestPayload);

        // Verify
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(Map.of(
                "targetUnitDetails", "test1",
                "reason", "reason",
                "versionMap", Map.of("CCA3", "1")
        ));
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails);
        verify(documentTemplateTransformationMapper, times(1))
                .constructVersionMap(Map.of(SchemeVersion.CCA_3, version));
        verify(underlyingAgreementSchemeVersionsHelperService, times(1))
        		.calculateSchemeVersionsFromActiveFacilities(Set.of(facility));
    }
}
