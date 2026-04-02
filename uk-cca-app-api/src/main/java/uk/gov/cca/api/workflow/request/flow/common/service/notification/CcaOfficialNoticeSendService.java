package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.notification.mail.constants.CcaEmailNotificationTemplateConstants;
import uk.gov.cca.api.notification.mail.constants.CcaNotificationTemplateName;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.core.service.SectorReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CcaOfficialNoticeSendService {

    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final SectorReferenceDetailsService sectorReferenceDetailsService;
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
                .findFirst()
                .orElseThrow(() -> new BusinessException(CcaErrorCode.SECTOR_ASSOCIATION_NO_CONTACT_FOUND));
        ccRecipientsEmails.add(sectorContact.getEmail());

        // Add Sector Consultants to cc List if exist
        defaultNoticeRecipients.stream()
                .filter(recipient -> recipient.getRecipientType().equals(NoticeRecipientType.SECTOR_CONSULTANT))
                .forEach(recipient -> ccRecipientsEmails.add(recipient.getEmail()));

        // Find to List, remove duplicates in case responsible and administrative email is the same and exclude specific recipient types
        List<NoticeRecipientType> excludedFromToRecipients = List.of(NoticeRecipientType.SECTOR_CONTACT,
                NoticeRecipientType.SECTOR_CONSULTANT);
        final Set<String> toRecipientsEmails = defaultNoticeRecipients.stream()
                .filter(recipient -> !excludedFromToRecipients.contains(recipient.getRecipientType()))
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
                                file -> fileDocumentService.getFileDTO(file.getUuid()).getFileContent())
                        )
                ).build();

        // Send
        this.notificationEmailService.notifyRecipients(emailData, new ArrayList<>(toRecipientsEmails), ccRecipientsEmails, bccRecipientsEmails);
    }

    public void sendOfficialNotice(List<FileInfoDTO> attachments, Request request, Long sectorAssociationId) {
        final SectorAssociationDTO sectorAssociationDetails = sectorReferenceDetailsService.getSectorAssociationDetails(sectorAssociationId);
        final SectorAssociationContactDTO sectorAssociationContact = sectorAssociationDetails.getSectorAssociationContact();
        final SectorAssociationDetailsDTO sectorDetails = sectorAssociationDetails.getSectorAssociationDetails();

        // Create email data
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(CcaEmailNotificationTemplateConstants.SECTOR_CONTACT, sectorAssociationContact.getFullName());
        templateParams.put(CcaEmailNotificationTemplateConstants.SECTOR_ASSOCIATION_NAME, sectorDetails.getCommonName());
        templateParams.put(CcaEmailNotificationTemplateConstants.SECTOR_ASSOCIATION_ACRONYM, sectorDetails.getAcronym());
        templateParams.put(CcaEmailNotificationTemplateConstants.CONTACT, notificationProperties.getEmail().getContactUsLink());

        EmailData emailData = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(CcaNotificationTemplateName.GENERIC_SECTOR_EMAIL_TEMPLATE)
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
        final List<String> toRecipientsEmails = List.of(sectorAssociationContact.getEmail());
        this.notificationEmailService.notifyRecipients(emailData, toRecipientsEmails);
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
