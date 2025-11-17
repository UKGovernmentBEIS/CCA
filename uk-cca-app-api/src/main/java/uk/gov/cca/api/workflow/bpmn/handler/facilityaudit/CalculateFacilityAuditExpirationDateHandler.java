package uk.gov.cca.api.workflow.bpmn.handler.facilityaudit;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaRequestExpirationKey;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service.CalculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService;
import uk.gov.netz.api.common.utils.DateUtils;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

import java.util.Date;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CalculateFacilityAuditExpirationDateHandler implements JavaDelegate {

    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    private final CalculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService calculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService;
    private final RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Override
    public void execute(DelegateExecution execution) {
        // Find most recent deadline if exists from task payload, and set it as due date
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);

        calculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService.calculateExpirationDate(requestId)
                .ifPresentOrElse(
                        deadline -> {
                            Date expirationDate = DateUtils.convertLocalDateToDate(deadline);
                            // set expiration timers
                            Map<String, Object> expirationVars = requestExpirationVarsBuilder.buildExpirationVars(CcaRequestExpirationKey.FACILITY_AUDIT, expirationDate);
                            execution.setVariables(expirationVars);
                            // set task due date
                            requestTaskTimeManagementService.setDueDateToTasks(requestId, CcaRequestExpirationKey.FACILITY_AUDIT, deadline);
                        },
                        () -> execution.setVariable(CcaBpmnProcessConstants.FACILITY_AUDIT_EXPIRATION_DATE, null));
    }
}
