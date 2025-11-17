package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaNotificationTemplateWorkflowTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaNotificationTemplateWorkflowTaskType.FACILITY_AUDIT;

@ExtendWith(MockitoExtension.class)
class FacilityAuditRequestExpirationReminderServiceTest {

    @InjectMocks
    private FacilityAuditRequestExpirationReminderService service;

    @Mock
    private RequestService requestService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private NotificationEmailService notificationEmailService;

    @Mock
    private NotificationProperties notificationProperties;

    @Mock
    private WebAppProperties webAppProperties;

    @Test
    void sendExpirationReminderNotification() {
        final String requestId = "AUDT-2022-1";
        final Long facilityId = 1L;
        final Date deadline = new Date();
        final String webUrl = "http://localhost:4202";

        final FacilityAuditRequestPayload payload = FacilityAuditRequestPayload.builder()
                .regulatorAssignee("bbb2820b-cbc6-4923-b3f1-8de409ea34c1")
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.builder().code(FACILITY_AUDIT.name()).build())
                .requestResources(List.of(RequestResource.builder().resourceType(CcaResourceType.FACILITY).resourceId(facilityId.toString()).build()))
                .payload(payload).build();

        final String regulatorAssignee = request.getPayload().getRegulatorAssignee();

        final UserInfoDTO regulatorAssigneeUser = UserInfoDTO.builder().userId(regulatorAssignee).build();

        final FacilityBaseInfoDTO facilityBaseInfoDTO = FacilityBaseInfoDTO.builder()
                .id(facilityId)
                .facilityBusinessId("facilityBusinessId")
                .siteName("Facility Site Name")
                .build();
        final NotificationTemplateExpirationReminderParams notificationTemplateExpirationReminderParams =
                NotificationTemplateExpirationReminderParams.builder()
                        .workflowTask(CcaNotificationTemplateWorkflowTaskType.
                                fromRequestType(request.getType().getCode()).getDescription())
                        .recipient(regulatorAssigneeUser)
                        .expirationTime(ExpirationReminderType.FIRST_REMINDER.getDescription())
                        .expirationTimeLong(ExpirationReminderType.FIRST_REMINDER.getDescriptionLong())
                        .deadline(deadline)
                        .build();
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(facilityDataQueryService.getFacilityBaseInfo(facilityId)).thenReturn(facilityBaseInfoDTO);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(webAppProperties.getUrl()).thenReturn(webUrl);

        // invoke
        service.sendExpirationReminderNotification(requestId, notificationTemplateExpirationReminderParams);

        // verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(facilityDataQueryService, times(1)).getFacilityBaseInfo(facilityId);
        verify(notificationProperties, times(1)).getEmail();
        verify(notificationEmailService, times(1)).notifyRecipient(any(), any());
        verify(webAppProperties, times(1)).getUrl();
    }
}
