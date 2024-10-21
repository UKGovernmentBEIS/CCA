package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@RequiredArgsConstructor
@Component
public class UnderlyingAgreementVariationAcceptedProposedDocumentTemplateWorkflowParamsProvider implements
		DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementVariationRequestPayload> {

	private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
	
	@Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PROPOSED_DOCUMENT;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementVariationRequestPayload payload) {
		final UnderlyingAgreementVariationPayload underlyingAgreement = payload.getUnderlyingAgreement();
		final int version = payload.getUnderlyingAgreementVersion() + 1;

		// Add target unit details from workflow data
		Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
				.constructTargetUnitDetailsTemplateParams(underlyingAgreement.getUnderlyingAgreementTargetUnitDetails(), version);

    	Set<String> rejectedFacilityIds = payload.getFacilitiesReviewGroupDecisions().entrySet().stream()
				.filter(entry -> CcaReviewDecisionType.REJECTED.equals(entry.getValue().getType()))
            	.map(Map.Entry::getKey)
            	.collect(Collectors.toSet());
		params.putAll(documentTemplateUnderlyingAgreementParamsProvider.constructTemplateParams(
				underlyingAgreement.getUnderlyingAgreement(),
				rejectedFacilityIds,
				null,
				version
		));

    	return params;
    }
}
