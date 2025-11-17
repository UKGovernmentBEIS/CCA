package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.handler;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Map;
import java.util.Set;

@Service
public class AuditDetailsCorrectiveActionsSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        return AuditDetailsCorrectiveActionsSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_PAYLOAD)
                .sectionsCompleted(Map.of())
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT);
    }
}
