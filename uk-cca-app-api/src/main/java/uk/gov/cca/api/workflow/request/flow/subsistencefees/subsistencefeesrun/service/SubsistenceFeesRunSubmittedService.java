package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunUpdateService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunSubmittedService {

    private final RequestService requestService;
    private final SubsistenceFeesRunUpdateService subsistenceFeesRunUpdateService;

    @Transactional
    public void subsistenceFeesRunSubmitted(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final SubsistenceFeesRunRequestPayload requestPayload = (SubsistenceFeesRunRequestPayload) request.getPayload();
        final SubsistenceFeesRunRequestMetadata metadata = (SubsistenceFeesRunRequestMetadata) request.getMetadata();

        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);

        CompetentAuthorityEnum competentAuthority = request.getCompetentAuthority();
        Year chargingYear = metadata.getChargingYear();
        final long runId = subsistenceFeesRunUpdateService.createSubsistenceFeesRun(requestId, competentAuthority, chargingYear);
        requestPayload.setRunId(runId);

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.SUBSISTENCE_FEES_RUN_SUBMITTED,
                requestPayload.getSubmitterId());
    }
}
