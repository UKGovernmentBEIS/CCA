package uk.gov.cca.api.workflow.request.flow.admintermination.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.service.AdminTerminationSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;
@Component
@RequiredArgsConstructor
public class AdminTerminationSubmitSaveActionHandler implements RequestTaskActionHandler<AdminTerminationSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AdminTerminationSubmitService adminTerminationSubmitService;

    @Override
    public RequestTaskPayload process(Long requestTaskId,
                        String requestTaskActionType,
                        AppUser appUser,
                        AdminTerminationSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        adminTerminationSubmitService.applySaveAction(payload, requestTask);
        
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.ADMIN_TERMINATION_SAVE_APPLICATION);
    }
}
