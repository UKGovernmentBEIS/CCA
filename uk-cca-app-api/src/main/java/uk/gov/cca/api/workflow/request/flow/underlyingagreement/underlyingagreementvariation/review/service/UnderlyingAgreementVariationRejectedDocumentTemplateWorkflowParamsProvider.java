package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeVersionsHelperService;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationRejectedDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementVariationRequestPayload> {

    private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
    private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
    private final UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_REJECTED;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementVariationRequestPayload payload) {
        // Add target unit details from workflow data
        Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
                .constructTargetUnitDetailsTemplateParams(payload.getUnderlyingAgreementProposed().getUnderlyingAgreementTargetUnitDetails());

        // Find applicable scheme versions
        final Map<SchemeVersion, Integer> versionMap = payload.getUnderlyingAgreementVersionMap();
        Map<SchemeVersion, Integer> activeVersionMap = new EnumMap<>(SchemeVersion.class);
        underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(
        		payload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities())
                	.forEach(version -> activeVersionMap.put(version, versionMap.get(version)));
        
        params.putAll(Map.of(
        		"reason", payload.getDetermination().getDetermination().getReason(),
        		"versionMap", documentTemplateTransformationMapper.constructVersionMap(activeVersionMap))
        		);

        return params;
    }
}
