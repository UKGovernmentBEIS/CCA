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
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.core.service.SectorReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.service.UnderlyingAgreementDefaultNoticeRecipients;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;

import java.util.ArrayList;
import java.util.HashMap;
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

@SuppressWarnings({ "unchecked", "rawtypes" })
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

    @Mock
    private SectorReferenceDetailsService sectorReferenceDetailsService;


    @BeforeEach
    void setUp() {
        requestDefaultNoticeRecipients.add(underlyingAgreementDefaultNoticeRecipients);
    }

    @Test
    void sendOfficialNoticeToSector() {
        final Long sectorAssociationId = 1L;
        final String fileUuid = UUID.randomUUID().toString();
        final String fileName = "fileName";
        final byte[] fileContent = "content".getBytes();
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;

        final List<FileInfoDTO> attachments = List.of(FileInfoDTO.builder().name(fileName).uuid(fileUuid).build());
        final FileDTO fileDTO = FileDTO.builder().fileContent(fileContent).build();
        final Request request = Request.builder()
                .type(RequestType.builder()
                        .code(CcaRequestType.SECTOR_MOA)
                        .build())
                .build();
        addAccountResourceToRequest(sectorAssociationId, request);
        addCaResourceToRequest(CompetentAuthorityEnum.ENGLAND, request);

        final SectorAssociationContactDTO sectorAssociationContact = SectorAssociationContactDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("sector_contact@test.com")
                .build();

        final String sectorName = "Aerospace_3";
        final String sectorAcronym = "ADS_3";
        final SectorAssociationDTO sectorAssociationDTO = SectorAssociationDTO.builder()
                .sectorAssociationDetails(SectorAssociationDetailsDTO.builder()
                        .acronym(sectorAcronym)
                        .commonName(sectorName)
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build())
                .sectorAssociationContact(sectorAssociationContact)
                .build();

        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(CcaEmailNotificationTemplateConstants.SECTOR_CONTACT, sectorAssociationContact.getFullName());
        templateParams.put(CcaEmailNotificationTemplateConstants.SECTOR_ASSOCIATION_NAME, sectorAssociationDTO.getSectorAssociationDetails().getCommonName());
        templateParams.put(CcaEmailNotificationTemplateConstants.SECTOR_ASSOCIATION_ACRONYM, sectorAssociationDTO.getSectorAssociationDetails().getAcronym());
        templateParams.put(CcaEmailNotificationTemplateConstants.CONTACT, "/contact-us");
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);

        final EmailData emailData = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(CcaNotificationTemplateName.GENERIC_SECTOR_EMAIL_TEMPLATE)
                        .competentAuthority(competentAuthority)
                        .templateParams(templateParams)
                        .build())
                .attachments(Map.of(fileName, fileContent)).build();

        when(sectorReferenceDetailsService.getSectorAssociationDetails(sectorAssociationId)).thenReturn(sectorAssociationDTO);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn("/contact-us");
        when(fileDocumentService.getFileDTO(fileUuid))
                .thenReturn(fileDTO);

        // Invoke
        service.sendOfficialNotice(attachments, request, sectorAssociationId);

        // Verify
        verify(sectorReferenceDetailsService, times(1))
                .getSectorAssociationDetails(sectorAssociationId);
        verify(fileDocumentService, times(1))
                .getFileDTO(fileUuid);
        verify(notificationEmailService, times(1))
                .notifyRecipients(emailData, List.of(sectorAssociationContact.getEmail()));
    }

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
                        .code(CcaRequestType.UNDERLYING_AGREEMENT)
                        .build())
                .build();
        addAccountResourceToRequest(accountId, request);
        addCaResourceToRequest(CompetentAuthorityEnum.ENGLAND, request);
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
                        List.of("administrative@test.com", "responsiblePerson@test.com"),
                        List.of("sector1@example.com", "sector@test.com"),
                        List.of());
    }

    @Test
    void sendOfficialNotice_with_consultants() {
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
                        .code(CcaRequestType.UNDERLYING_AGREEMENT)
                        .build())
                .build();
        addAccountResourceToRequest(accountId, request);
        addCaResourceToRequest(CompetentAuthorityEnum.ENGLAND, request);
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
                        .build(),
                DefaultNoticeRecipient.builder()
                        .name("Sector Consultant Last Name1")
                        .email("consultant1@test.com")
                        .recipientType(NoticeRecipientType.SECTOR_CONSULTANT)
                        .build(),
                DefaultNoticeRecipient.builder()
                        .name("Sector Consultant Last Name2")
                        .email("consultant2@test.com")
                        .recipientType(NoticeRecipientType.SECTOR_CONSULTANT)
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
                        List.of("administrative@test.com", "responsiblePerson@test.com"),
                        List.of("sector1@example.com", "sector@test.com", "consultant1@test.com", "consultant2@test.com"),
                        List.of());
    }

    @Test
    void getOfficialNoticeToDefaultRecipients() {
        final Request request = Request.builder()
                .type(RequestType.builder()
                        .code(CcaRequestType.UNDERLYING_AGREEMENT)
                        .build())
                .build();
        addAccountResourceToRequest(1L, request);

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
        final Long accountId = 1L;
        final Request request = Request.builder()
                .type(RequestType.builder()
                        .code(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION)
                        .build())
                .build();
        addAccountResourceToRequest(accountId, request);

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
