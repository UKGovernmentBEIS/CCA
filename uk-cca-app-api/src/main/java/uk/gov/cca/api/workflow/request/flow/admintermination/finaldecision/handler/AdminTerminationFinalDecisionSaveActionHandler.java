package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service.AdminTerminationFinalDecisionService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTerminationFinalDecisionSaveActionHandler implements RequestTaskActionHandler<AdminTerminationFinalDecisionSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AdminTerminationFinalDecisionService adminTerminationFinalDecisionService;

    @Override
    public void process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                        AdminTerminationFinalDecisionSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        adminTerminationFinalDecisionService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.ADMIN_TERMINATION_FINAL_DECISION_SAVE_APPLICATION);
    }
}
