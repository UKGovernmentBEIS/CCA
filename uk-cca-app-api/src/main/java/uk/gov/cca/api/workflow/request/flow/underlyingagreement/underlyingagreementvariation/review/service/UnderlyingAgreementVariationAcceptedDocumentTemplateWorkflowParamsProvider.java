package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationAcceptedDocumentTemplateWorkflowParamsProvider implements
		DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementVariationRequestPayload> {

	private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

	@Override
	public String getContextActionType() {
		return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED;
	}

	@Override
	public Map<String, Object> constructParams(UnderlyingAgreementVariationRequestPayload payload) {
		final int version = payload.getUnderlyingAgreementVersion();

		// Add target unit details from final workflow data
		return documentTemplateUnderlyingAgreementParamsProvider
				.constructTargetUnitDetailsTemplateParams(payload.getUnderlyingAgreementProposed().getUnderlyingAgreementTargetUnitDetails(), version);
	}
}
