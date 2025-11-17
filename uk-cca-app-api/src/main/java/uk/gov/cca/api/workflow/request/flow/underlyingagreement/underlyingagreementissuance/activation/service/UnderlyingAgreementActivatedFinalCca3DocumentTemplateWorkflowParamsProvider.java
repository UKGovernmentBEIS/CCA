package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@RequiredArgsConstructor
@Component
public class UnderlyingAgreementActivatedFinalCca3DocumentTemplateWorkflowParamsProvider implements 
		DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementRequestPayload>{

	private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
	private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
	
	@Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACTIVATED_FINAL_DOCUMENT_CCA3;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementRequestPayload payload) {
		final UnderlyingAgreementPayload proposedUnderlyingAgreement = payload.getUnderlyingAgreementProposed();
		final int version = 1;

		// Add target unit details from workflow data
		Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
				.constructTargetUnitDetailsTemplateParams(proposedUnderlyingAgreement.getUnderlyingAgreementTargetUnitDetails());

		params.putAll(
				documentTemplateUnderlyingAgreementParamsProvider.constructTemplateParams(
						proposedUnderlyingAgreement.getUnderlyingAgreement(),
						documentTemplateTransformationMapper.formatCurrentDate(),
						SchemeVersion.CCA_3, 
						version)
		);

    	return params;
    }
}
