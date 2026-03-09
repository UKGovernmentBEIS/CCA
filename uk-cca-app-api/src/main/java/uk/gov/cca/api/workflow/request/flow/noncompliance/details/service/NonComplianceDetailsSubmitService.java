package uk.gov.cca.api.workflow.request.flow.noncompliance.details.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitSaveRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
public class NonComplianceDetailsSubmitService {

    @Transactional
    public void applySaveAction(final NonComplianceDetailsSubmitSaveRequestTaskActionPayload payload, RequestTask requestTask) {
        NonComplianceDetailsSubmitRequestTaskPayload requestTaskPayload = (NonComplianceDetailsSubmitRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.setNonComplianceDetails(payload.getNonComplianceDetails());
        requestTaskPayload.setSectionsCompleted(payload.getSectionsCompleted());
    }

    @Transactional
    public void submitDetails(RequestTask requestTask) {
        NonComplianceDetailsSubmitRequestTaskPayload taskPayload = (NonComplianceDetailsSubmitRequestTaskPayload) requestTask.getPayload();

        final Request request = requestTask.getRequest();
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();

        requestPayload.setNonComplianceDetails(taskPayload.getNonComplianceDetails());
        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
    }
}
