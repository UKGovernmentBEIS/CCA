package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdminTerminationFinalDecisionSubmittedDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<AdminTerminationRequestPayload> {

	private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
	
    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINAL_DECISION_FINALISED;
    }

    @Override
    public Map<String, Object> constructParams(AdminTerminationRequestPayload payload) {
        return Map.of(
                "reasonDetails", payload.getAdminTerminationReasonDetails(),
                "versionMap", documentTemplateTransformationMapper.constructVersionMap(payload.getUnderlyingAgreementVersionMap())
        );
    }
}
