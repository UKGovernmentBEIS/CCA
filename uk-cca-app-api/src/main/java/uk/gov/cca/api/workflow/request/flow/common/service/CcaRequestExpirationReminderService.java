package uk.gov.cca.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.notification.mail.constants.CcaEmailNotificationTemplateConstants;
import uk.gov.netz.api.account.domain.dto.AccountInfoDTO;
import uk.gov.netz.api.account.service.AccountQueryService;
import uk.gov.netz.api.notification.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.netz.api.notification.mail.domain.EmailData;
import uk.gov.netz.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notification.mail.service.NotificationEmailService;
import uk.gov.netz.api.notification.template.constants.NotificationTemplateName;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CcaRequestExpirationReminderService {

    private final RequestService requestService;
    private final AccountQueryService accountQueryService;
    private final NotificationEmailService notificationEmailService;
    private final NotificationProperties notificationProperties;

    public void sendExpirationReminderNotification(String requestId, NotificationTemplateExpirationReminderParams expirationParams) {
        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();
        final AccountInfoDTO accountInfo = accountQueryService.getAccountInfoDTOById(accountId);

        final Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(EmailNotificationTemplateConstants.ACCOUNT_NAME, accountInfo.getName());
        templateParams.put(EmailNotificationTemplateConstants.ACCOUNT_BUSINESS_ID, accountInfo.getBusinessId());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_ID, request.getId());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW, request.getType().getDescription());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_TASK, expirationParams.getWorkflowTask());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_USER, expirationParams.getRecipient().getFullName());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME, expirationParams.getExpirationTime());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME_LONG, expirationParams.getExpirationTimeLong());
        templateParams.put(EmailNotificationTemplateConstants.WORKFLOW_DEADLINE, expirationParams.getDeadline());
        templateParams.put(CcaEmailNotificationTemplateConstants.CONTACT, notificationProperties.getEmail().getContactUsLink());

        final EmailData emailData = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .competentAuthority(request.getCompetentAuthority())
                        .templateName(NotificationTemplateName.GENERIC_EXPIRATION_REMINDER)
                        .templateParams(templateParams)
                        .build())
                .build();

        notificationEmailService.notifyRecipient(emailData, expirationParams.getRecipient().getEmail());
    }
}
