package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.run.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationRunService {

    private final RequestService requestService;

    @Transactional
    public void accountProcessingCompleted(final String requestId, final Long accountId, final Cca3FacilityMigrationAccountState accountState) {
        final Request request = requestService.findRequestById(requestId);
        final Cca3ExistingFacilitiesMigrationRunRequestPayload requestPayload =
                (Cca3ExistingFacilitiesMigrationRunRequestPayload) request.getPayload();

        requestPayload.getAccountStates().put(accountId, accountState);
    }

    @Transactional
    public void complete(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        Cca3ExistingFacilitiesMigrationRunRequestPayload requestPayload =
                (Cca3ExistingFacilitiesMigrationRunRequestPayload) request.getPayload();
        final Map<Long, Cca3FacilityMigrationAccountState> accountStates = requestPayload.getAccountStates();

        // Update metadata
        Cca3ExistingFacilitiesMigrationRunRequestMetadata metadata =
                (Cca3ExistingFacilitiesMigrationRunRequestMetadata) request.getMetadata();
        metadata.setAccountStates(accountStates);

        // Update status
        if(metadata.getFailedAccounts() > 0) {
            requestService.updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
        }
    }
}
