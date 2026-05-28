package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
@RequiredArgsConstructor
public class EnforcementResponseNoticeSubmitService {

    private final RequestService requestService;

    @Transactional
    public void applySaveAction(final NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload requestTaskPayload = (NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.setEnforcementResponseNotice(payload.getEnforcementResponseNotice());
        requestTaskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final DecisionNotification decisionNotification) {

        NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload requestTaskPayload = (NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();

        requestPayload.setEnforcementResponseNotice(requestTaskPayload.getEnforcementResponseNotice());
        requestPayload.setSectionsCompleted(requestTaskPayload.getSectionsCompleted());
        requestPayload.setNonComplianceAttachments(requestTaskPayload.getNonComplianceAttachments());
        requestPayload.setDecisionNotification(decisionNotification);
    }

    @Transactional
    public void requestPeerReview(final RequestTask requestTask, final String selectedPeerReviewer,
                                  final String regulatorReviewer) {

        final NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload requestTaskPayload = (NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();

        requestPayload.setEnforcementResponseNotice(requestTaskPayload.getEnforcementResponseNotice());
        requestPayload.setNonComplianceAttachments(requestTaskPayload.getNonComplianceAttachments());
        requestPayload.setRegulatorReviewer(regulatorReviewer);
        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
        requestPayload.setSectionsCompleted(requestTaskPayload.getSectionsCompleted());
    }

    @Transactional
    public void resetForPenaltyReissue(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        requestPayload.setEnforcementResponseNotice(null);
    }
}
