package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementAcceptedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private UnderlyingAgreementAcceptedDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACCEPTED);
    }

    @Test
    void constructParams() {
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
        		.facilities(Set.of(Facility.builder()
        				.facilityItem(FacilityItem.builder()
        						.facilityDetails(FacilityDetails.builder()
        								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2)).build()
        								).build()
        						)
        				.status(FacilityStatus.LIVE)
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
        final UnderlyingAgreementRequestPayload payload = UnderlyingAgreementRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .determination(Determination.builder()
								.additionalInformation("Some additional information")
								.build())
                .facilitiesReviewGroupDecisions(Map.of(
                        "ADS_1-F01244", UnderlyingAgreementFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build(),
                        "ADS_1-F01245", UnderlyingAgreementFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build(),
                        "ADS_1-F01249", UnderlyingAgreementFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.REJECTED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build(),
                        "ADS_1-F01246", UnderlyingAgreementFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.REJECTED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build()
                ))
                .build();

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails))
                .thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));

        Map<String, Object> result = provider.constructParams(payload);

        // Verify
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "targetUnitDetails", "test1",
                "rejectedFacilities", List.of("ADS_1-F01246", "ADS_1-F01249"),
                "versionMap", Map.of("CCA2", "v1", "CCA3", "v1"),
                "additionalInformation", "Some additional information"
        ));
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails);
    }
}
