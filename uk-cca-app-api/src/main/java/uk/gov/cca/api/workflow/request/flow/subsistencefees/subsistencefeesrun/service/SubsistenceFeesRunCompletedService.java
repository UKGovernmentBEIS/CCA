package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunUpdateService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.transform.SubsistenceFeesRunMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunCompletedService {

    private final RequestService requestService;
    private final SubsistenceFeesRunUpdateService subsistenceFeesRunUpdateService;
    private static final SubsistenceFeesRunMapper SUBSISTENCE_FEES_RUN_MAPPER = Mappers
            .getMapper(SubsistenceFeesRunMapper.class);

    @Transactional
    public void completeSubsistenceFeesRun(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final SubsistenceFeesRunRequestPayload payload = (SubsistenceFeesRunRequestPayload) request.getPayload();
        final SubsistenceFeesRunRequestMetadata metadata = (SubsistenceFeesRunRequestMetadata) request.getMetadata();
        String requestActionType = CcaRequestActionType.SUBSISTENCE_FEES_RUN_COMPLETED;
        String requestStatus = RequestStatuses.COMPLETED;

        // Persist subsistence fees run details if at least one report exists
        if (metadata.getSentInvoices() > 0L) {
            subsistenceFeesRunUpdateService.finalizeSubsistenceFeesRun(payload.getRunId());
        } else {
            subsistenceFeesRunUpdateService.deleteSubsistenceFeesRun(payload.getRunId());
        }

        // Update status if completed with failures
        if (metadata.getFailedInvoices() > 0) {
            requestActionType = CcaRequestActionType.SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES;
            requestStatus = CcaRequestStatuses.COMPLETED_WITH_FAILURES;
            requestService.updateRequestStatus(requestId, requestStatus);
        }

        // Add timeline event
        final SubsistenceFeesRunCompletedRequestActionPayload requestActionPayload =
                SUBSISTENCE_FEES_RUN_MAPPER.toCompletedActionPayload(payload, metadata, request.getId(), requestStatus);

        requestService.addActionToRequest(request,
                requestActionPayload,
                requestActionType,
                payload.getSubmitterId());
    }

}
