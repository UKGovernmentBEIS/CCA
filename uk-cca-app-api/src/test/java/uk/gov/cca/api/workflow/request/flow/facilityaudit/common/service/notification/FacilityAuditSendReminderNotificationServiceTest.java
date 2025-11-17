package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaNotificationTemplateWorkflowTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service.FacilityAuditRequestExpirationReminderService;
import uk.gov.netz.api.userinfoapi.UserInfoApi;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;

import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaNotificationTemplateWorkflowTaskType.FACILITY_AUDIT;

@ExtendWith(MockitoExtension.class)
class FacilityAuditSendReminderNotificationServiceTest {

    @InjectMocks
    private FacilityAuditSendReminderNotificationService service;

    @Mock
    private RequestService requestService;

    @Mock
    private UserInfoApi userInfoApi;

    @Mock
    private FacilityAuditRequestExpirationReminderService requestExpirationReminderService;

    @Test
    void sendFirstReminderNotification() {

        final String requestId = "AUDT-2022-1";

        final Date deadline = new Date();

        final FacilityAuditRequestPayload payload = FacilityAuditRequestPayload.builder()
                .regulatorAssignee("bbb2820b-cbc6-4923-b3f1-8de409ea34c1")
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.builder().code(FACILITY_AUDIT.name()).build())
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

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(userInfoApi.getUserByUserId(regulatorAssignee)).thenReturn(regulatorAssigneeUser);

        // Invoke
        service.sendFirstReminderNotification(requestId, deadline);

        // Verify
        verify(userInfoApi, times(1)).getUserByUserId(regulatorAssignee);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestExpirationReminderService, times(1)).sendExpirationReminderNotification(requestId, params);
    }

    @Test
    void sendSecondReminderNotification() {

        final String requestId = "AUDT-2022-1";

        final Date deadline = new Date();

        final FacilityAuditRequestPayload payload = FacilityAuditRequestPayload.builder()
                .regulatorAssignee("bbb2820b-cbc6-4923-b3f1-8de409ea34c1")
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.builder().code(FACILITY_AUDIT.name()).build())
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

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(userInfoApi.getUserByUserId(regulatorAssignee)).thenReturn(regulatorAssigneeUser);

        // Invoke
        service.sendSecondReminderNotification(requestId, deadline);

        // Verify
        verify(userInfoApi, times(1)).getUserByUserId(regulatorAssignee);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestExpirationReminderService, times(1)).sendExpirationReminderNotification(requestId, params);
    }
}
