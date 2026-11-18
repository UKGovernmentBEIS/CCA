package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeVersionsHelperService;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationCompletedDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementVariationRequestPayload> {

    private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
    private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
    private final UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementVariationRequestPayload payload) {
        // Add target unit details from workflow data
        Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
                .constructTargetUnitDetailsTemplateParams(payload.getUnderlyingAgreementProposed().getUnderlyingAgreementTargetUnitDetails());

        // Add flag for variation regulator led
        final boolean isRegulatorLed = RoleTypeConstants.REGULATOR.equals(payload.getInitiatorRoleType());
        params.put("isRegulatorLed", isRegulatorLed);

        // Add determination additional info
        final String additionalInformation = isRegulatorLed
                ? payload.getRegulatorLedDetermination().getAdditionalInformation()
                : payload.getDetermination().getDetermination().getAdditionalInformation();
        params.put("additionalInformation", additionalInformation);

        // Add document versions based on original una
        final Map<SchemeVersion, Integer> versionMap = payload.getUnderlyingAgreementVersionMap();
        Map<SchemeVersion, Integer> activatedVersionMap = new EnumMap<>(SchemeVersion.class);
        underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(
        		payload.getOriginalUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities())
                	.forEach(version -> activatedVersionMap.put(version, versionMap.get(version)));

        params.put("versionMap", documentTemplateTransformationMapper.constructVersionMap(activatedVersionMap));

        return params;
    }

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_COMPLETED;
    }
}
