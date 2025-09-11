package uk.gov.cca.api.workflow.request.flow.facilitycertification.run.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunSummary;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.transform.FacilityCertificationRunMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FacilityCertificationRunService {

    private final RequestService requestService;
    private static final FacilityCertificationRunMapper MAPPER = Mappers.getMapper(FacilityCertificationRunMapper.class);

    @Transactional
    public void submit(final String requestId) {
        final Request request = requestService.findRequestById(requestId);

        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);
    }

    @Transactional
    public void accountProcessingCompleted(final String requestId, final Long accountId, final FacilityCertificationAccountState accountState) {
        final Request request = requestService.findRequestById(requestId);
        final FacilityCertificationRunRequestPayload requestPayload = (FacilityCertificationRunRequestPayload) request.getPayload();

        requestPayload.getFacilityCertificationAccountStates().put(accountId, accountState);
    }

    @Transactional
    public void complete(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        FacilityCertificationRunRequestPayload requestPayload = (FacilityCertificationRunRequestPayload) request.getPayload();
        FacilityCertificationRunRequestMetadata metadata = (FacilityCertificationRunRequestMetadata) request.getMetadata();
        final Map<Long, FacilityCertificationAccountState> accountStates = requestPayload.getFacilityCertificationAccountStates();

        // Update payload
        FacilityCertificationRunSummary runSummary = MAPPER.toFacilityCertificationRunSummary(accountStates);
        requestPayload.setRunSummary(runSummary);

        // Update metadata
        metadata.setTotalAccounts(runSummary.getTotalAccounts());
        metadata.setFailedAccounts(runSummary.getFailedAccounts());
        metadata.setFacilitiesCertified(runSummary.getFacilitiesCertified());

        // Update status
        if(runSummary.getFailedAccounts() > 0) {
            requestService.updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
        }
    }
}
