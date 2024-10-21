package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestExpirationReminderService;
import uk.gov.netz.api.userinfoapi.UserInfoApi;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class SendReminderNotificationService {

    private final UserInfoApi userInfoApi;
    private final CcaRequestExpirationReminderService requestExpirationReminderService;

    public void sendFirstReminderNotification(Request request, Date deadline, String assignee) {
        sendReminderNotification(request, deadline, ExpirationReminderType.FIRST_REMINDER, assignee);
    }

    public void sendSecondReminderNotification(Request request, Date deadline, String assignee) {
        sendReminderNotification(request, deadline, ExpirationReminderType.SECOND_REMINDER, assignee);
    }

    private void sendReminderNotification(Request request, Date deadline, ExpirationReminderType expirationType, String assignee) {
        if (assignee == null) {
            return;
        }

        UserInfoDTO assigneeUser = userInfoApi.getUserByUserId(assignee);

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
