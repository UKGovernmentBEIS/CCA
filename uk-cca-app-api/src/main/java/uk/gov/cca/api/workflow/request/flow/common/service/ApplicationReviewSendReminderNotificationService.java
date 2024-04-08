package uk.gov.cca.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;
import uk.gov.cca.api.workflow.request.core.service.RequestService;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.NotificationTemplateWorkflowTaskType;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ApplicationReviewSendReminderNotificationService {
    
    private final RequestService requestService;
    private final UserAuthService userAuthService;
    private final RequestExpirationReminderService requestExpirationReminderService;

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
        
        UserInfoDTO regulatorAssigneeUser = userAuthService.getUserByUserId(regulatorAssignee);
        
        requestExpirationReminderService.sendExpirationReminderNotification(requestId, 
                NotificationTemplateExpirationReminderParams.builder()
                        .workflowTask(NotificationTemplateWorkflowTaskType.fromRequestType(request.getType().name())
                                .getDescription())
                    .recipient(regulatorAssigneeUser)
                    .expirationTime(expirationType.getDescription())
                    .expirationTimeLong(expirationType.getDescriptionLong())
                    .deadline(deadline)
                    .build());
    }
}
