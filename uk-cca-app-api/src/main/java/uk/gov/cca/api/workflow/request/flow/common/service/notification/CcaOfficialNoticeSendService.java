package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.notification.mail.constants.CcaEmailNotificationTemplateConstants;
import uk.gov.cca.api.notification.mail.constants.CcaNotificationTemplateName;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.api.notification.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notification.mail.domain.EmailData;
import uk.gov.netz.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notification.mail.service.NotificationEmailService;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CcaOfficialNoticeSendService {

    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final NotificationEmailService notificationEmailService;
    private final FileDocumentService fileDocumentService;
    private final NotificationProperties notificationProperties;
    private final List<RequestDefaultNoticeRecipients> requestDefaultNoticeRecipients;
    private final TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    public void sendOfficialNotice(List<FileInfoDTO> attachments, Request request, List<String> ccRecipientsEmails) {
        this.sendOfficialNotice(attachments, request, ccRecipientsEmails, Collections.emptyList());
    }

    public void sendOfficialNotice(List<FileInfoDTO> attachments, Request request, List<String> ccRecipientsEmails, List<String> bccRecipientsEmails) {
        List<DefaultNoticeRecipient> defaultNoticeRecipients = getOfficialNoticeToDefaultRecipients(request);

        // Add Sector Contact to cc List
        DefaultNoticeRecipient sectorContact = defaultNoticeRecipients.stream()
                .filter(recipient -> recipient.getRecipientType().equals(NoticeRecipientType.SECTOR_CONTACT))
                        .findFirst().orElseThrow(() -> new BusinessException(CcaErrorCode.SECTOR_ASSOCIATION_NO_CONTACT_FOUND));
        ccRecipientsEmails.add(sectorContact.getEmail());

        // Find to List
        final List<String> toRecipientsEmails = defaultNoticeRecipients.stream()
                .filter(recipient -> !recipient.getRecipientType().equals(NoticeRecipientType.SECTOR_CONTACT))
                .map(DefaultNoticeRecipient::getEmail)
                .toList();

        // Get Target Unit business id
        final String businessId = accountReferenceDetailsService.getTargetUnitAccountDetails(request.getAccountId()).getBusinessId();

        // Create email data
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(CcaEmailNotificationTemplateConstants.RESPONSIBLE_USER, getRecipientNameByType(defaultNoticeRecipients, NoticeRecipientType.RESPONSIBLE_PERSON));
        templateParams.put(CcaEmailNotificationTemplateConstants.ADMINISTRATIVE_USER, getRecipientNameByType(defaultNoticeRecipients, NoticeRecipientType.ADMINISTRATIVE_CONTACT));
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
                                file -> fileDocumentService.getFileDTO(file.getUuid()).getFileContent())
                        )
                ).build();

        // Send
        this.notificationEmailService.notifyRecipients(emailData, toRecipientsEmails, ccRecipientsEmails, bccRecipientsEmails);
    }

    public List<DefaultNoticeRecipient> getOfficialNoticeToDefaultRecipients(Request request) {
        List<DefaultNoticeRecipient> defaultNoticeRecipients = new ArrayList<>();

        // Get default recipients from request otherwise get from target unit account
        requestDefaultNoticeRecipients.stream()
                .filter(service -> service.getType().equals(request.getType().getCode()))
                .findFirst().ifPresentOrElse(
                        service -> defaultNoticeRecipients.addAll(service.getRecipients(request)),
                        () -> defaultNoticeRecipients.addAll(targetUnitAccountNoticeRecipients
                                .getDefaultNoticeRecipients(request.getAccountId()))
                );

        return defaultNoticeRecipients;
    }

    private String getRecipientNameByType(List<DefaultNoticeRecipient> defaultNoticeRecipients,
                                          NoticeRecipientType recipientType) {
        return defaultNoticeRecipients.stream()
                .filter(recipient -> recipient.getRecipientType().equals(recipientType))
                .findFirst().map(DefaultNoticeRecipient::getName)
                .orElseThrow(() ->
                    switch (recipientType) {
                        case RESPONSIBLE_PERSON -> new BusinessException(CcaErrorCode.TARGET_UNIT_ACCOUNT_RESPONSIBLE_PERSON_CONTACT_NOT_FOUND);
                        case SECTOR_CONTACT -> new BusinessException(CcaErrorCode.SECTOR_ASSOCIATION_NO_CONTACT_FOUND);
                        case ADMINISTRATIVE_CONTACT -> new BusinessException(CcaErrorCode.TARGET_UNIT_ACCOUNT_ADMINISTRATIVE_CONTACT_NOT_FOUND);
                        default -> throw new IllegalArgumentException("Unexpected default recipient type: " + recipientType);
                    }
                );
    }
}
