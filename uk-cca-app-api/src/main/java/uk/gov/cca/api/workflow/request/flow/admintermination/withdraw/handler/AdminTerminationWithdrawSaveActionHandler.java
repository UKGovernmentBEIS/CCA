package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.service.AdminTerminationWithdrawService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTerminationWithdrawSaveActionHandler implements RequestTaskActionHandler<AdminTerminationWithdrawSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;

    private final AdminTerminationWithdrawService adminTerminationWithdrawService;


    @Override
    public void process(Long requestTaskId,
                        String requestTaskActionType,
                        AppUser appUser,
                        AdminTerminationWithdrawSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        adminTerminationWithdrawService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.ADMIN_TERMINATION_WITHDRAW_SAVE_APPLICATION);
    }
}
