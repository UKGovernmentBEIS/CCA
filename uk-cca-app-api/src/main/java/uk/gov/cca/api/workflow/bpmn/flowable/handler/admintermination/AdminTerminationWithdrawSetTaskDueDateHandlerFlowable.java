package uk.gov.cca.api.workflow.bpmn.flowable.handler.admintermination;

import lombok.RequiredArgsConstructor;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaRequestExpirationKey;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AdminTerminationWithdrawSetTaskDueDateHandlerFlowable implements JavaDelegate {

    private final RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final LocalDate expirationDate = ((Date) execution
                .getVariable(CcaBpmnProcessConstants.ADMIN_TERMINATION_EXPIRATION_DATE)).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        requestTaskTimeManagementService.setDueDateToTasks(requestId, CcaRequestExpirationKey.ADMIN_TERMINATION, expirationDate);
    }
}
