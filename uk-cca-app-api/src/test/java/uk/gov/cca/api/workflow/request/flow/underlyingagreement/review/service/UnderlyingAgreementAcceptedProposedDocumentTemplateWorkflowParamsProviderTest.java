package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.service;

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

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementAcceptedProposedDocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
    private UnderlyingAgreementAcceptedProposedDocumentTemplateWorkflowParamsProvider provider;
	
	@Mock
	private DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(
        		CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACCEPTED_PROPOSED_DOCUMENT);
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
				.facilitiesReviewGroupDecisions(Map.of("id1", UnderlyingAgreementFacilityReviewDecision.builder()
						.type(CcaReviewDecisionType.REJECTED)
						.build()))
				.build();

		when(documentTemplateUnderlyingAgreementParamsProvider.constructTargetUnitDetailsTemplateParams(targetUnitDetails, version))
				.thenReturn(new HashMap<>(Map.of("targetUnitDetails", "test1")));
		when(documentTemplateUnderlyingAgreementParamsProvider.constructTemplateParams(una, Set.of("id1"), null, version))
				.thenReturn(new HashMap<>(Map.of("params", "test2")));

		// Invoke
		Map<String, Object> actual = provider.constructParams(payload);

		// Verify
		assertThat(actual).containsExactlyInAnyOrderEntriesOf(Map.of(
				"targetUnitDetails", "test1",
				"params", "test2"
		));
		verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
				.constructTargetUnitDetailsTemplateParams(targetUnitDetails, version);
		verify(documentTemplateUnderlyingAgreementParamsProvider, times(1))
				.constructTemplateParams(una, Set.of("id1"), null, version);
    }
}
