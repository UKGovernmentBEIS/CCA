package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeVersionsHelperService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementActivatedDocumentTemplateWorkflowParamsProvider implements
		DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementRequestPayload> {

    private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
    private final UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;
	
	@Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACTIVATED;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementRequestPayload payload) {

        UnderlyingAgreementTargetUnitDetails targetUnitDetails = payload.getUnderlyingAgreementProposed()
                .getUnderlyingAgreementTargetUnitDetails();
        
        Set<String> schemeVersions = underlyingAgreementSchemeVersionsHelperService
        		.calculateSchemeVersionsFromActiveFacilities(
        				payload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities())
        		.stream()
        		.map(SchemeVersion::getDescription)
        		.collect(Collectors.toSet());

        Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails);
        
        params.put("versionMap", schemeVersions.stream().collect(Collectors.toMap(v -> v, v -> "v1")));
        
        return params;
    }
}
