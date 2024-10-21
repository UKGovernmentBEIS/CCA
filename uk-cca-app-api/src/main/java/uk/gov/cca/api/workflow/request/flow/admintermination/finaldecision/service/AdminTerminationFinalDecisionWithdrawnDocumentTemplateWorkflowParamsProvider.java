package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdminTerminationFinalDecisionWithdrawnDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<AdminTerminationRequestPayload> {

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINAL_DECISION_WITHDRAWN;
    }

    @Override
    public Map<String, Object> constructParams(AdminTerminationRequestPayload payload) {
        return Map.of(
                "explanation", payload.getAdminTerminationFinalDecisionReasonDetails().getExplanation(),
                "submissionDate", payload.getSubmitSubmissionDate()
        );
    }
}
