package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service.Cca3ExistingFacilitiesMigrationAccountProcessingActivationUploadAttachmentService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivationUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final Cca3ExistingFacilitiesMigrationAccountProcessingActivationUploadAttachmentService cca3ExistingFacilitiesMigrationAccountProcessingActivationUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        cca3ExistingFacilitiesMigrationAccountProcessingActivationUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public String getType() {
        return CcaRequestTaskActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_UPLOAD_ATTACHMENT;
    }
}
