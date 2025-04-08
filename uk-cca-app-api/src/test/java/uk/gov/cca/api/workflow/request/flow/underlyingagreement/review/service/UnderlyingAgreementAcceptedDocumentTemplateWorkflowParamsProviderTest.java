package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecisionDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final int version = 1;
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder().build();
        final UnderlyingAgreementRequestPayload payload = UnderlyingAgreementRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .build())
                .facilitiesReviewGroupDecisions(Map.of(
                        "ADS_1-F01244", UnderlyingAgreementFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build(),
                        "ADS_1-F01245", UnderlyingAgreementFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build(),
                        "ADS_1-F01246", UnderlyingAgreementFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.REJECTED)
                                .details(UnderlyingAgreementReviewDecisionDetails.builder().build()).build()
                ))
                .build();

        when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails, version))
                .thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));

        Map<String, Object> result = provider.constructParams(payload);

        // Verify
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "targetUnitDetails", "test1",
                "rejectedFacilities", List.of("ADS_1-F01246")
        ));
        verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails, version);
    }
}
