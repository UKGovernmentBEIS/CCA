package uk.gov.cca.api.workflow.request.flow.admintermination.submit.handler;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.transform.AdminTerminationSubmitMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class AdminTerminationSubmitInitializer implements InitializeRequestTaskHandler {

    private static final AdminTerminationSubmitMapper ADMIN_TERMINATION_SUBMIT_MAPPER = Mappers.getMapper(AdminTerminationSubmitMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        return ADMIN_TERMINATION_SUBMIT_MAPPER
                .toApplicationSubmitRequestTaskPayload((AdminTerminationRequestPayload) request.getPayload());
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_SUBMIT);
    }
}
