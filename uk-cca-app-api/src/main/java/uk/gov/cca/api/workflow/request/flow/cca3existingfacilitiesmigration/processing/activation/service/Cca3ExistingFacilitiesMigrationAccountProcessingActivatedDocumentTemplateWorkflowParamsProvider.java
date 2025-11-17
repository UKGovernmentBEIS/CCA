package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.Map;

@Component
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivatedDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload> {

    @Override
    public Map<String, Object> constructParams(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload) {
        int version = 1;
        return Map.of("version", "v" + version);
    }

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.EXISTING_FACILITIES_MIGRATION_UNDERLYING_AGREEMENT_ACTIVATED_CCA3;
    }
}
