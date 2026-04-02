package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NoticeOfIntentSubmitSaveRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
public class NoticeOfIntentSubmitService {

    @Transactional
    public void applySaveAction(final NoticeOfIntentSubmitSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = (NonComplianceNoticeOfIntentSubmitRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.setNoticeOfIntent(payload.getNoticeOfIntent());
        requestTaskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final DecisionNotification decisionNotification) {

        NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = (NonComplianceNoticeOfIntentSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();

        requestPayload.setNoticeOfIntent(requestTaskPayload.getNoticeOfIntent());
        requestPayload.setSectionsCompleted(requestTaskPayload.getSectionsCompleted());
        requestPayload.setNonComplianceAttachments(requestTaskPayload.getNonComplianceAttachments());
        requestPayload.setDecisionNotification(decisionNotification);
    }

    @Transactional
    public void requestPeerReview(final RequestTask requestTask, final String selectedPeerReviewer,
                                  final String regulatorReviewer) {

        final NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = (NonComplianceNoticeOfIntentSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();

        requestPayload.setNoticeOfIntent(requestTaskPayload.getNoticeOfIntent());
        requestPayload.setNonComplianceAttachments(requestTaskPayload.getNonComplianceAttachments());
        requestPayload.setRegulatorReviewer(regulatorReviewer);
        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
        requestPayload.setSectionsCompleted(requestTaskPayload.getSectionsCompleted());
    }
}
