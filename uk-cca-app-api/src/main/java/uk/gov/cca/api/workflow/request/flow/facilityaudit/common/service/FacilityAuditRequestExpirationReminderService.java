package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.notification.mail.constants.CcaEmailNotificationTemplateConstants;
import uk.gov.cca.api.notification.mail.constants.CcaNotificationTemplateName;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.netz.api.workflow.utils.NotificationTemplateConstants;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FacilityAuditRequestExpirationReminderService {

    private final RequestService requestService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final NotificationEmailService notificationEmailService;
    private final NotificationProperties notificationProperties;
    private final WebAppProperties webAppProperties;

    public void sendExpirationReminderNotification(String requestId, NotificationTemplateExpirationReminderParams expirationParams) {
        final Request request = requestService.findRequestById(requestId);
        final Long facilityId = request.getRequestResources().stream()
                .filter(requestResource -> requestResource.getResourceType().equals(CcaResourceType.FACILITY))
                .map(RequestResource::getResourceId)
                .map(Long::parseLong)
                .findFirst()
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        final FacilityBaseInfoDTO facilityBaseInfo = facilityDataQueryService.getFacilityBaseInfo(facilityId);

        final Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(CcaEmailNotificationTemplateConstants.FACILITY_NAME, facilityBaseInfo.getSiteName());
        templateParams.put(CcaEmailNotificationTemplateConstants.FACILITY_BUSINESS_ID, facilityBaseInfo.getFacilityBusinessId());
        templateParams.put(NotificationTemplateConstants.WORKFLOW_ID, request.getId());
        templateParams.put(NotificationTemplateConstants.WORKFLOW, request.getType().getDescription());
        templateParams.put(NotificationTemplateConstants.WORKFLOW_TASK, expirationParams.getWorkflowTask());
        templateParams.put(NotificationTemplateConstants.WORKFLOW_USER, expirationParams.getRecipient().getFullName());
        templateParams.put(NotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME, expirationParams.getExpirationTime());
        templateParams.put(NotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME_LONG, expirationParams.getExpirationTimeLong());
        templateParams.put(NotificationTemplateConstants.WORKFLOW_DEADLINE, expirationParams.getDeadline());
        templateParams.put(NotificationTemplateConstants.HOME_URL, webAppProperties.getUrl());
        templateParams.put(CcaEmailNotificationTemplateConstants.CONTACT, notificationProperties.getEmail().getContactUsLink());

        final EmailData<EmailNotificationTemplateData> emailData = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .competentAuthority(request.getCompetentAuthority())
                        .templateName(CcaNotificationTemplateName.FACILITY_AUDIT_GENERIC_EXPIRATION_REMINDER_TEMPLATE)
                        .templateParams(templateParams)
                        .build())
                .build();

        notificationEmailService.notifyRecipient(emailData, expirationParams.getRecipient().getEmail());
    }
}
