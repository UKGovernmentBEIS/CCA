package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.notification.mail.constants.CcaEmailNotificationTemplateConstants;
import uk.gov.cca.api.notification.mail.constants.CcaNotificationTemplateName;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings({"unchecked", "rawtypes"})
public class NonComplianceOfficialNoticeSendService {

    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final NotificationEmailService notificationEmailService;
    private final FileAttachmentService fileAttachmentService;
    private final NotificationProperties notificationProperties;
    private final CcaOfficialNoticeSendService officialNoticeSendService;

    public void sendOfficialNotice(List<FileInfoDTO> attachments, Request request, List<String> ccRecipientsEmails, List<String> bccRecipientsEmails) {
        List<DefaultNoticeRecipient> defaultNoticeRecipients = officialNoticeSendService.getOfficialNoticeToDefaultRecipients(request);

        final Set<String> toRecipientsEmails = defaultNoticeRecipients.stream()
                .map(DefaultNoticeRecipient::getEmail)
                .collect(Collectors.toSet());

        // Get Target Unit business id
        final String businessId = accountReferenceDetailsService.getTargetUnitAccountDetails(request.getAccountId()).getBusinessId();

        // Create email data
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(CcaEmailNotificationTemplateConstants.RESPONSIBLE_USER, getRecipientNameByType(defaultNoticeRecipients, NoticeRecipientType.RESPONSIBLE_PERSON));
        templateParams.put(CcaEmailNotificationTemplateConstants.TARGET_UNIT_ID, businessId);
        templateParams.put(CcaEmailNotificationTemplateConstants.CONTACT, notificationProperties.getEmail().getContactUsLink());

        EmailData emailData = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(CcaNotificationTemplateName.GENERIC_EMAIL_TEMPLATE)
                        .competentAuthority(request.getCompetentAuthority())
                        .templateParams(templateParams)
                        .build())
                .attachments(attachments.stream()
                        .collect(Collectors.toMap(
                                FileInfoDTO::getName,
                                file -> fileAttachmentService.getFileDTO(file.getUuid()).getFileContent())
                        )
                ).build();

        // Send
        this.notificationEmailService.notifyRecipients(emailData, new ArrayList<>(toRecipientsEmails), ccRecipientsEmails, bccRecipientsEmails);
    }

    private String getRecipientNameByType(List<DefaultNoticeRecipient> defaultNoticeRecipients,
                                          NoticeRecipientType recipientType) {
        return defaultNoticeRecipients.stream()
                .filter(recipient -> recipient.getRecipientType().equals(recipientType))
                .findFirst().map(DefaultNoticeRecipient::getName)
                .orElseThrow(() ->
                        switch (recipientType) {
                            case RESPONSIBLE_PERSON -> new BusinessException(CcaErrorCode.TARGET_UNIT_ACCOUNT_RESPONSIBLE_PERSON_CONTACT_NOT_FOUND);
                            case ADMINISTRATIVE_CONTACT -> new BusinessException(CcaErrorCode.TARGET_UNIT_ACCOUNT_ADMINISTRATIVE_CONTACT_NOT_FOUND);
                            default -> throw new IllegalArgumentException("Unexpected default recipient type: " + recipientType);
                        }
                );
    }
}
