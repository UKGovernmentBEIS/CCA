package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationAcceptedProposedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private UnderlyingAgreementVariationAcceptedProposedDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PROPOSED_DOCUMENT);
    }

    @Test
    void constructParams() {
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final UnderlyingAgreement una = UnderlyingAgreement.builder().build();
        final UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(una)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(una)
                        .build())
                .facilitiesReviewGroupDecisions(Map.of("id1", UnderlyingAgreementVariationFacilityReviewDecision.builder()
                        .type(CcaReviewDecisionType.ACCEPTED)
                        .build()))
                .underlyingAgreementVersion(99)
                .build();

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails, 100))
                .thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));
        when(documentTemplateUnderlyingAgreementParamsProvider.constructTemplateParams(una, null, 100))
                .thenReturn(new HashMap<>(Map.of("params", "test2")));

        // Invoke
        Map<String, Object> actual = provider.constructParams(payload);

        // Verify
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(Map.of(
                "targetUnitDetails", "test1",
                "params", "test2"
        ));
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails, 100);
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTemplateParams(una, null, 100);
    }
}
