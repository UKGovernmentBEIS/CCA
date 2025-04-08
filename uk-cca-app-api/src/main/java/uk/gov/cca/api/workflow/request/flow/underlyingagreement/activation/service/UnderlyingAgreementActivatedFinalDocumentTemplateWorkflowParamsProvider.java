package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@RequiredArgsConstructor
@Component
public class UnderlyingAgreementActivatedFinalDocumentTemplateWorkflowParamsProvider implements
		DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementRequestPayload> {

	private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
	private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
	
	@Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACTIVATED_FINAL_DOCUMENT;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementRequestPayload payload) {
		final UnderlyingAgreementPayload proposedUnderlyingAgreement = payload.getUnderlyingAgreementProposed();
		final int version = 1;

		// Add target unit details from workflow data
		Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
				.constructTargetUnitDetailsTemplateParams(proposedUnderlyingAgreement.getUnderlyingAgreementTargetUnitDetails(), version);

		params.putAll(
				documentTemplateUnderlyingAgreementParamsProvider.constructTemplateParams(
						proposedUnderlyingAgreement.getUnderlyingAgreement(),
						documentTemplateTransformationMapper.formatCurrentDate(),
						version)
		);

    	return params;
    }
}
