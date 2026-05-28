package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
@RequiredArgsConstructor
public class NonComplianceAppealOutcomeService {

    @Transactional
    public void save(final NonComplianceAppealOutcomeSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        NonComplianceAppealOutcomeSubmitRequestTaskPayload requestTaskPayload = (NonComplianceAppealOutcomeSubmitRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.setAppealOutcome(payload.getAppealOutcome());
        requestTaskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void complete(RequestTask requestTask) {
        NonComplianceAppealOutcomeSubmitRequestTaskPayload requestTaskPayload = (NonComplianceAppealOutcomeSubmitRequestTaskPayload) requestTask.getPayload();
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) requestTask.getRequest().getPayload();
        requestPayload.setAppealOutcome(requestTaskPayload.getAppealOutcome());
        requestPayload.setSectionsCompleted(requestTaskPayload.getSectionsCompleted());
        requestPayload.setNonComplianceAttachments(requestTaskPayload.getNonComplianceAttachments());
    }
}
