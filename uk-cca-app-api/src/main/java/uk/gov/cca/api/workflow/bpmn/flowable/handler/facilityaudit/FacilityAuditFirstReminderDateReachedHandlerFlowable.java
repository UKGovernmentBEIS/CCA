package uk.gov.cca.api.workflow.bpmn.flowable.handler.facilityaudit;

import lombok.RequiredArgsConstructor;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service.notification.FacilityAuditSendReminderNotificationService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class FacilityAuditFirstReminderDateReachedHandlerFlowable implements JavaDelegate {

    private final FacilityAuditSendReminderNotificationService reminderNotificationService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate = (Date) execution.getVariable(CcaBpmnProcessConstants.FACILITY_AUDIT_EXPIRATION_DATE);
        reminderNotificationService.sendFirstReminderNotification(requestId, expirationDate);
    }
}
