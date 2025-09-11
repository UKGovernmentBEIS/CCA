package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
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
class UnderlyingAgreementVariationActivatedFinalDocumentTemplateWorkflowParamsProviderTest {


    @InjectMocks
    private UnderlyingAgreementVariationActivatedFinalDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_FINAL_DOCUMENT);
    }

    @Test
    void constructParams() {
        final int version = 1;
        final Facility facility = Facility.builder().status(FacilityStatus.LIVE).facilityItem(FacilityItem.builder().facilityId("id1").build()).build();
        final UnderlyingAgreement una = UnderlyingAgreement.builder().facilities(Set.of(facility)).build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(una)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(una)
                        .build())
                .originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder().underlyingAgreement(una).build())
                .facilitiesReviewGroupDecisions(Map.of("id1", UnderlyingAgreementVariationFacilityReviewDecision.builder()
                        .type(CcaReviewDecisionType.ACCEPTED)
                        .build()))
                .underlyingAgreementVersion(version)
                .build();

        final String activationDate = "dd MMMM yyyy";
        when(documentTemplateTransformationMapper.formatCurrentDate()).thenReturn(activationDate);
        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails, version + 1))
                .thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));
        when(documentTemplateUnderlyingAgreementParamsProvider.constructTemplateParams(una, activationDate, version + 1))
                .thenReturn(new HashMap<>(Map.of("params", "test2")));

        // Invoke
        Map<String, Object> actual = provider.constructParams(payload);

        // Verify
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(Map.of(
                "targetUnitDetails", "test1",
                "params", "test2"
        ));
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails, version + 1);
        verify(documentTemplateTransformationMapper, times(1)).formatCurrentDate();
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTemplateParams(una, activationDate, version + 1);
    }
}
