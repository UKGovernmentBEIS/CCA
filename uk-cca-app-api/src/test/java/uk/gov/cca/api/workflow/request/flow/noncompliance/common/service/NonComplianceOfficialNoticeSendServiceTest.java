package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.notification.mail.constants.CcaEmailNotificationTemplateConstants;
import uk.gov.cca.api.notification.mail.constants.CcaNotificationTemplateName;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class NonComplianceOfficialNoticeSendServiceTest {

    @InjectMocks
    private NonComplianceOfficialNoticeSendService service;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private NotificationEmailService notificationEmailService;

    @Mock
    private NotificationProperties notificationProperties;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private CcaOfficialNoticeSendService officialNoticeSendService;

    @Test
    void sendOfficialNotice() {
        final Long accountId = 1L;
        final String businessId = "businessId";
        final String fileUuid = UUID.randomUUID().toString();
        final String fileName = "fileName";
        final byte[] fileContent = "content".getBytes();
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;

        final List<FileInfoDTO> attachments = List.of(FileInfoDTO.builder().name(fileName).uuid(fileUuid).build());
        final FileDTO fileDTO = FileDTO.builder().fileContent(fileContent).build();
        final Request request = Request.builder()
                .type(RequestType.builder()
                        .code(CcaRequestType.NON_COMPLIANCE)
                        .build())
                .build();
        addAccountResourceToRequest(accountId, request);
        addCaResourceToRequest(CompetentAuthorityEnum.ENGLAND, request);
        List<String> ccRecipientsEmails = new ArrayList<>();
        ccRecipientsEmails.add("operator1@example.com");

        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .businessId(businessId)
                .build();

        final List<DefaultNoticeRecipient> defaultRecipients = List.of(
                DefaultNoticeRecipient.builder()
                        .name("Responsible Last Name")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build(),
                DefaultNoticeRecipient.builder()
                        .name("Administrative Last Name")
                        .email("administrative@test.com")
                        .recipientType(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                        .build()
        );

        final EmailData emailData = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(CcaNotificationTemplateName.GENERIC_EMAIL_TEMPLATE)
                        .competentAuthority(competentAuthority)
                        .templateParams(Map.of(
                                CcaEmailNotificationTemplateConstants.RESPONSIBLE_USER, "Responsible Last Name",
                                CcaEmailNotificationTemplateConstants.TARGET_UNIT_ID, businessId,
                                CcaEmailNotificationTemplateConstants.CONTACT, "/contact-us"
                        ))
                        .build())
                .attachments(Map.of(fileName, fileContent)).build();

        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn("/contact-us");
        when(fileAttachmentService.getFileDTO(fileUuid)).thenReturn(fileDTO);
        when(officialNoticeSendService.getOfficialNoticeToDefaultRecipients(request))
                .thenReturn(defaultRecipients);

        // Invoke
        service.sendOfficialNotice(attachments, request, ccRecipientsEmails, List.of());

        // Verify
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(fileAttachmentService, times(1))
                .getFileDTO(fileUuid);
        verify(notificationEmailService, times(1))
                .notifyRecipients(
                        emailData,
                        List.of("administrative@test.com", "responsiblePerson@test.com"),
                        List.of("operator1@example.com"),
                        List.of());
    }

    private void addAccountResourceToRequest(Long accountId, Request request) {
        RequestResource accountResource = RequestResource.builder()
                .resourceType(ResourceType.ACCOUNT)
                .resourceId(accountId.toString())
                .request(request)
                .build();

        request.getRequestResources().add(accountResource);
    }

    private void addCaResourceToRequest(CompetentAuthorityEnum ca, Request request) {
        RequestResource accountResource = RequestResource.builder()
                .resourceType(ResourceType.CA)
                .resourceId(ca.name())
                .request(request)
                .build();

        request.getRequestResources().add(accountResource);
    }
}
