package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaNotificationTemplateWorkflowTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service.FacilityAuditRequestExpirationReminderService;
import uk.gov.netz.api.userinfoapi.UserInfoApi;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class FacilityAuditSendReminderNotificationService {

    private final UserInfoApi userInfoApi;
    private final RequestService requestService;
    private final FacilityAuditRequestExpirationReminderService requestExpirationReminderService;

    public void sendFirstReminderNotification(String requestId, Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.FIRST_REMINDER);
    }

    public void sendSecondReminderNotification(String requestId, Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.SECOND_REMINDER);
    }

    private void sendReminderNotification(String requestId, Date deadline, ExpirationReminderType expirationType) {
        final Request request = requestService.findRequestById(requestId);
        final String regulatorAssignee = request.getPayload().getRegulatorAssignee();

        if (regulatorAssignee == null) {
            return;
        }

        UserInfoDTO assigneeUser = userInfoApi.getUserByUserId(regulatorAssignee);

        requestExpirationReminderService.sendExpirationReminderNotification(request.getId(),
                NotificationTemplateExpirationReminderParams.builder()
                        .workflowTask(
                                CcaNotificationTemplateWorkflowTaskType.
                                        fromRequestType(request.getType().getCode()).getDescription())
                        .recipient(assigneeUser)
                        .expirationTime(expirationType.getDescription())
                        .expirationTimeLong(expirationType.getDescriptionLong())
                        .deadline(deadline)
                        .build());
    }
}
