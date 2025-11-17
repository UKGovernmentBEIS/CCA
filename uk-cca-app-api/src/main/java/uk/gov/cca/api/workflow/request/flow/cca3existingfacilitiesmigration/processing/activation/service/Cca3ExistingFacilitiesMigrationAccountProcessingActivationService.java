package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivationService {

    private final RequestService requestService;

    @Transactional
    public void applySaveAction(final Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload taskPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload) requestTask.getPayload();

        taskPayload.setActivationDetails(payload.getActivationDetails());
        taskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final CcaDecisionNotification decisionNotification) {
        final Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload taskPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();

        request.setSubmissionDate(LocalDateTime.now());

        requestPayload.setActivationDetails(taskPayload.getActivationDetails());
        requestPayload.setActivationAttachments(taskPayload.getActivationAttachments());
        requestPayload.setDecisionNotification(decisionNotification);
    }

    @Transactional
    public void cancel(final String requestId) {
        final Request request = requestService.findRequestById(requestId);

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCELLED,
                request.getPayload().getRegulatorAssignee());
    }
}
