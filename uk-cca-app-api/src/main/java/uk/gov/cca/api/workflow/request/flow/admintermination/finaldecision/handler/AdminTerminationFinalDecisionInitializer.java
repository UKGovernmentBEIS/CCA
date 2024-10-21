package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.handler;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class AdminTerminationFinalDecisionInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        return AdminTerminationFinalDecisionRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_FINAL_DECISION_PAYLOAD)
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_FINAL_DECISION);
    }
}
