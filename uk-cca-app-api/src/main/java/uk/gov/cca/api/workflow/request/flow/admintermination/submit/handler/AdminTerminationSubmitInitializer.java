package uk.gov.cca.api.workflow.request.flow.admintermination.submit.handler;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class AdminTerminationSubmitInitializer implements InitializeRequestTaskHandler {
    @Override
    public RequestTaskPayload initializePayload(Request request) {
        return AdminTerminationSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_SUBMIT_PAYLOAD)
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_SUBMIT);
    }
}
