package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.run.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeRunRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeRunService {

    private final RequestService requestService;

    @Transactional
    public void accountProcessingCompleted(final String requestId, final Long accountId, final Cca2ExtensionNoticeAccountState accountState) {
        final Request request = requestService.findRequestById(requestId);
        final Cca2ExtensionNoticeRunRequestPayload requestPayload = (Cca2ExtensionNoticeRunRequestPayload) request.getPayload();

        requestPayload.getAccountStates().put(accountId, accountState);
    }

    @Transactional
    public void complete(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final Cca2ExtensionNoticeRunRequestPayload requestPayload = (Cca2ExtensionNoticeRunRequestPayload) request.getPayload();
        final Map<Long, Cca2ExtensionNoticeAccountState> accountStates = requestPayload.getAccountStates();

        // Update metadata
        Cca2ExtensionNoticeRunRequestMetadata metadata = (Cca2ExtensionNoticeRunRequestMetadata) request.getMetadata();
        metadata.setAccountStates(accountStates);

        // Update status
        if(accountStates.values().stream().anyMatch(acc -> !acc.isSucceeded())) {
            requestService.updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
        }
    }
}
