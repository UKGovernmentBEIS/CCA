package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;

@ExtendWith(MockitoExtension.class)
public class UnderlyingAgreementActivatedFinalCca3DocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
    private UnderlyingAgreementActivatedFinalCca3DocumentTemplateWorkflowParamsProvider provider;


    @Mock
    private DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACTIVATED_FINAL_DOCUMENT_CCA3);
    }

    @Test
    void constructParams() {
        final int version = 1;
        final UnderlyingAgreement una = UnderlyingAgreement.builder().build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final UnderlyingAgreementRequestPayload payload = UnderlyingAgreementRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(una)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(una)
                        .build())
                .facilitiesReviewGroupDecisions(Map.of("id1", UnderlyingAgreementFacilityReviewDecision.builder()
                        .type(CcaReviewDecisionType.ACCEPTED)
                        .build()))
                .build();
        final String activationDate = "dd MMMM yyyy";

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails))
                .thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));
        when(documentTemplateTransformationMapper.formatCurrentDate())
                .thenReturn(activationDate);
        when(documentTemplateUnderlyingAgreementParamsProvider.constructTemplateParams(una, activationDate, SchemeVersion.CCA_3, version))
                .thenReturn(new HashMap<>(Map.of("params", "test2")));

        // Invoke
        Map<String, Object> actual = provider.constructParams(payload);

        // Verify
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(Map.of(
                "targetUnitDetails", "test1",
                "params", "test2"
        ));
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails);
        verify(documentTemplateTransformationMapper, times(1))
                .formatCurrentDate();
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTemplateParams(una, activationDate, SchemeVersion.CCA_3, version);
    }
}
