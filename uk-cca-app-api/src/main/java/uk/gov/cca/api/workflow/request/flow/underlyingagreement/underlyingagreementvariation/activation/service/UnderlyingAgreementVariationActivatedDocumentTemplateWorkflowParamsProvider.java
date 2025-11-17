package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.utils.UnderlyingAgreementCalculateSchemeVersionsUtil;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivatedDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementVariationRequestPayload> {

    private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
    private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementVariationRequestPayload payload) {
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = payload.getUnderlyingAgreementProposed()
                .getUnderlyingAgreementTargetUnitDetails();
        final Map<SchemeVersion, Integer> versionMap = payload.getUnderlyingAgreementVersionMap();

        Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails);

        Map<SchemeVersion, Integer> activatedVersionMap = new EnumMap<>(SchemeVersion.class);
        UnderlyingAgreementCalculateSchemeVersionsUtil
                .calculateSchemeVersionsFromActiveFacilities(payload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities())
                .forEach(version -> activatedVersionMap.put(version, versionMap.getOrDefault(version, 0)  + 1));

        params.put("versionMap", documentTemplateTransformationMapper.constructVersionMap(activatedVersionMap));

        return params;
    }
}
