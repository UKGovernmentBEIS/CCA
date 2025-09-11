package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestExpirationReminderService;
import uk.gov.netz.api.userinfoapi.UserInfoApi;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;

import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaNotificationTemplateWorkflowTaskType.ADMIN_TERMINATION;

@ExtendWith(MockitoExtension.class)
class SendReminderNotificationServiceTest {

    @InjectMocks
    private SendReminderNotificationService service;

    @Mock
    private UserInfoApi userInfoApi;

    @Mock
    private CcaRequestExpirationReminderService requestExpirationReminderService;

    @Test
    void sendFirstReminderNotification() {

        final String requestId = "ATER-2022-1";

        final Date deadline = new Date();

        final AdminTerminationRequestPayload payload = AdminTerminationRequestPayload.builder()
                .regulatorAssignee("bbb2820b-cbc6-4923-b3f1-8de409ea34c1")
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.builder().code(ADMIN_TERMINATION.name()).build())
                .payload(payload).build();

        final String regulatorAssignee = request.getPayload().getRegulatorAssignee();

        final UserInfoDTO regulatorAssigneeUser = UserInfoDTO.builder().userId(regulatorAssignee).build();

        final NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
                .workflowTask(CcaNotificationTemplateWorkflowTaskType.
                        fromRequestType(request.getType().getCode()).getDescription())
                .recipient(regulatorAssigneeUser)
                .expirationTime(ExpirationReminderType.FIRST_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.FIRST_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build();

        when(userInfoApi.getUserByUserId(regulatorAssignee)).thenReturn(regulatorAssigneeUser);

        // Invoke
        service.sendFirstReminderNotification(request, deadline, regulatorAssignee);

        // Verify
        verify(userInfoApi, times(1)).getUserByUserId(regulatorAssignee);
        verify(requestExpirationReminderService, times(1)).sendExpirationReminderNotification(requestId, params);
    }

    @Test
    void sendSecondReminderNotification() {

        final String requestId = "ATER-2022-1";

        final Date deadline = new Date();

        final AdminTerminationRequestPayload payload = AdminTerminationRequestPayload.builder()
                .regulatorAssignee("bbb2820b-cbc6-4923-b3f1-8de409ea34c1")
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.builder().code(ADMIN_TERMINATION.name()).build())
                .payload(payload).build();

        final String regulatorAssignee = request.getPayload().getRegulatorAssignee();

        final UserInfoDTO regulatorAssigneeUser = UserInfoDTO.builder().userId(regulatorAssignee).build();

        final NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
                .workflowTask(CcaNotificationTemplateWorkflowTaskType.
                        fromRequestType(request.getType().getCode()).getDescription())
                .recipient(regulatorAssigneeUser)
                .expirationTime(ExpirationReminderType.SECOND_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.SECOND_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build();

        when(userInfoApi.getUserByUserId(regulatorAssignee)).thenReturn(regulatorAssigneeUser);

        // Invoke
        service.sendSecondReminderNotification(request, deadline, regulatorAssignee);

        // Verify
        verify(userInfoApi, times(1)).getUserByUserId(regulatorAssignee);
        verify(requestExpirationReminderService, times(1)).sendExpirationReminderNotification(requestId, params);
    }

}
