package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service;

import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.utils.UnderlyingAgreementCalculateSchemeVersionsUtil;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationAcceptedDocumentTemplateWorkflowParamsProvider implements
		DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementVariationRequestPayload> {

	private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
	private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

	@Override
	public String getContextActionType() {
		return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED;
	}

	@Override
	public Map<String, Object> constructParams(UnderlyingAgreementVariationRequestPayload payload) {
		// Find version to be terminated if all it's facilities have been excluded,
		Set<SchemeVersion> currentSchemeVersions = UnderlyingAgreementCalculateSchemeVersionsUtil
        		.calculateSchemeVersionsFromActiveFacilities(payload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities());
		Set<SchemeVersion> previousSchemeVersions = UnderlyingAgreementCalculateSchemeVersionsUtil
        		.calculateSchemeVersionsFromActiveFacilities(payload.getOriginalUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities());
		Set<SchemeVersion> terminatedSchemeVersions = SetUtils.difference(previousSchemeVersions, currentSchemeVersions);
		String terminatedSchemeVersion = terminatedSchemeVersions.isEmpty() ? "" : terminatedSchemeVersions.iterator().next().getDescription();
		
		// Add target unit details from final workflow data
		Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
				.constructTargetUnitDetailsTemplateParams(payload.getUnderlyingAgreementProposed().getUnderlyingAgreementTargetUnitDetails());
		
		params.putAll(Map.of(
				"versionMap", documentTemplateTransformationMapper.constructVersionMap(payload.getUnderlyingAgreementVersionMap()),
				"terminatedSchemeVersion", terminatedSchemeVersion)
		);
		
		return params;
	}
}
