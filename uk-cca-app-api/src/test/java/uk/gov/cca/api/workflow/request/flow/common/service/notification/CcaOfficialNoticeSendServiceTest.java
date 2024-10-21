package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.notification.mail.constants.CcaEmailNotificationTemplateConstants;
import uk.gov.cca.api.notification.mail.constants.CcaNotificationTemplateName;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.UnderlyingAgreementDefaultNoticeRecipients;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.api.notification.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notification.mail.domain.EmailData;
import uk.gov.netz.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notification.mail.service.NotificationEmailService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaOfficialNoticeSendServiceTest {

    @InjectMocks
    private CcaOfficialNoticeSendService service;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private NotificationEmailService notificationEmailService;

    @Mock
    private FileDocumentService fileDocumentService;

    @Mock
    private NotificationProperties notificationProperties;

    @Spy
    private ArrayList<RequestDefaultNoticeRecipients> requestDefaultNoticeRecipients;

    @Mock
    private TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    @Mock
    private UnderlyingAgreementDefaultNoticeRecipients underlyingAgreementDefaultNoticeRecipients;

    @BeforeEach
    void setUp() {
        requestDefaultNoticeRecipients.add(underlyingAgreementDefaultNoticeRecipients);
    }

    @Test
    void sendOfficialNotice() {
        final long accountId = 1L;
        final String businessId = "businessId";
        final String fileUuid = UUID.randomUUID().toString();
        final String fileName = "fileName";
        final byte[] fileContent = "content".getBytes();
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;

        final List<FileInfoDTO> attachments = List.of(FileInfoDTO.builder().name(fileName).uuid(fileUuid).build());
        final FileDTO fileDTO = FileDTO.builder().fileContent(fileContent).build();
        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.builder()
                        .code(CcaRequestType.UNDERLYING_AGREEMENT)
                        .build())
                .competentAuthority(competentAuthority)
                .build();
        List<String> ccRecipientsEmails = new ArrayList<>();
        ccRecipientsEmails.add("sector1@example.com");

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
                        .build(),
                DefaultNoticeRecipient.builder()
                        .name("Sector Last Name")
                        .email("sector@test.com")
                        .recipientType(NoticeRecipientType.SECTOR_CONTACT)
                        .build()
        );

        final EmailData emailData = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(CcaNotificationTemplateName.GENERIC_EMAIL_TEMPLATE)
                        .competentAuthority(competentAuthority)
                        .templateParams(Map.of(
                                CcaEmailNotificationTemplateConstants.RESPONSIBLE_USER, "Responsible Last Name",
                                CcaEmailNotificationTemplateConstants.ADMINISTRATIVE_USER, "Administrative Last Name",
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
        when(fileDocumentService.getFileDTO(fileUuid))
                .thenReturn(fileDTO);
        when(underlyingAgreementDefaultNoticeRecipients.getType())
                .thenReturn(CcaRequestType.UNDERLYING_AGREEMENT);
        when(underlyingAgreementDefaultNoticeRecipients.getRecipients(request))
                .thenReturn(defaultRecipients);

        // Invoke
        service.sendOfficialNotice(attachments, request, ccRecipientsEmails);

        // Verify
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(fileDocumentService, times(1))
                .getFileDTO(fileUuid);
        verify(notificationEmailService, times(1))
                .notifyRecipients(
                        emailData,
                        List.of("responsiblePerson@test.com", "administrative@test.com"),
                        List.of("sector1@example.com", "sector@test.com"),
                        List.of());
    }

    @Test
    void getOfficialNoticeToDefaultRecipients() {
        final long accountId = 1L;
        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.builder()
                        .code(CcaRequestType.UNDERLYING_AGREEMENT)
                        .build())
                .build();

        final List<DefaultNoticeRecipient> expected = List.of(
                DefaultNoticeRecipient.builder()
                        .name("Responsible Last Name")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build(),
                DefaultNoticeRecipient.builder()
                        .name("Administrative Last Name")
                        .email("administrative@test.com")
                        .recipientType(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                        .build(),
                DefaultNoticeRecipient.builder()
                        .name("Sector Last Name")
                        .email("sector@test.com")
                        .recipientType(NoticeRecipientType.SECTOR_CONTACT)
                        .build()
        );

        when(underlyingAgreementDefaultNoticeRecipients.getType())
                .thenReturn(CcaRequestType.UNDERLYING_AGREEMENT);
        when(underlyingAgreementDefaultNoticeRecipients.getRecipients(request))
                .thenReturn(expected);

        // Invoke
        List<DefaultNoticeRecipient> actual = service.getOfficialNoticeToDefaultRecipients(request);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(underlyingAgreementDefaultNoticeRecipients, times(1))
                .getType();
        verify(underlyingAgreementDefaultNoticeRecipients, times(1))
                .getRecipients(request);
        verifyNoInteractions(targetUnitAccountNoticeRecipients);
    }

    @Test
    void getOfficialNoticeToDefaultRecipients_default_service() {
        final long accountId = 1L;
        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.builder()
                        .code(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION)
                        .build())
                .build();

        final List<DefaultNoticeRecipient> expected = List.of(
                DefaultNoticeRecipient.builder()
                        .name("Responsible Last Name")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build(),
                DefaultNoticeRecipient.builder()
                        .name("Administrative Last Name")
                        .email("administrative@test.com")
                        .recipientType(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                        .build(),
                DefaultNoticeRecipient.builder()
                        .name("Sector Last Name")
                        .email("sector@test.com")
                        .recipientType(NoticeRecipientType.SECTOR_CONTACT)
                        .build()
        );

        when(underlyingAgreementDefaultNoticeRecipients.getType())
                .thenReturn(CcaRequestType.UNDERLYING_AGREEMENT);
        when(targetUnitAccountNoticeRecipients.getDefaultNoticeRecipients(accountId))
                .thenReturn(expected);

        // Invoke
        List<DefaultNoticeRecipient> actual = service.getOfficialNoticeToDefaultRecipients(request);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(underlyingAgreementDefaultNoticeRecipients, times(1))
                .getType();
        verify(underlyingAgreementDefaultNoticeRecipients, never())
                .getRecipients(any());
        verify(targetUnitAccountNoticeRecipients, times(1))
                .getDefaultNoticeRecipients(accountId);
    }
}
