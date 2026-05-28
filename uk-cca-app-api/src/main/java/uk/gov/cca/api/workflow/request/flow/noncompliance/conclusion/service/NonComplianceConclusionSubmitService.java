package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
public class NonComplianceConclusionSubmitService {

    @Transactional
    public void applySaveAction(final NonComplianceConclusionSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = (NonComplianceConclusionSubmitRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.setNonComplianceConclusion(payload.getNonComplianceConclusion());
        requestTaskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask, final DecisionNotification decisionNotification) {
        NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = (NonComplianceConclusionSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        requestPayload.setNonComplianceConclusion(requestTaskPayload.getNonComplianceConclusion());
        requestPayload.setPenaltyReissueNeeded(false);
        requestPayload.setSectionsCompleted(requestTaskPayload.getSectionsCompleted());
        requestPayload.setNonComplianceAttachments(requestTaskPayload.getNonComplianceAttachments());
        requestPayload.setDecisionNotification(decisionNotification);
    }

    @Transactional
    public void complete(RequestTask requestTask, final boolean isReissuePenaltyNeeded) {
        NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = (NonComplianceConclusionSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        requestPayload.setNonComplianceConclusion(requestTaskPayload.getNonComplianceConclusion());
        requestPayload.setPenaltyReissueNeeded(isReissuePenaltyNeeded);
        requestPayload.setSectionsCompleted(requestTaskPayload.getSectionsCompleted());
        requestPayload.setNonComplianceAttachments(requestTaskPayload.getNonComplianceAttachments());
    }
}
