package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.user.sectoruser.service.SectorUserAuthorityInfoService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeAccountProcessingDefaultNoticeRecipientsTest {

    @InjectMocks
    private Cca2ExtensionNoticeAccountProcessingDefaultNoticeRecipients service;

    @Mock
    private TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    @Mock
    private SectorUserAuthorityInfoService sectorUserAuthorityInfoService;

    @Test
    void getRecipients() {
        final Long accountId = 1L;
        final Long sectorId = 2L;
        final Request request = Request.builder()
                .payload(Cca2ExtensionNoticeAccountProcessingRequestPayload.builder()
                        .sectorAssociationId(sectorId)
                        .build())
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build())
                ).build();

        final DefaultNoticeRecipient administrative = DefaultNoticeRecipient.builder()
                .name("Administrative")
                .email("administrative@test.com")
                .recipientType(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                .build();
        final DefaultNoticeRecipient responsible = DefaultNoticeRecipient.builder()
                .name("Responsible")
                .email("responsiblePerson@test.com")
                .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                .build();
        List<DefaultNoticeRecipient> recipients = Stream.of(administrative, responsible).collect(Collectors.toList());
        List<AdditionalNoticeRecipientDTO> sectors = List.of(
                AdditionalNoticeRecipientDTO.builder()
                        .firstName("SectorFn")
                        .lastName("SectorLn")
                        .email("Sector@test.com")
                        .type(NoticeRecipientType.SECTOR_USER)
                        .build()
        );

        final DefaultNoticeRecipient consultant = DefaultNoticeRecipient.builder()
                .name("SectorFn SectorLn")
                .email("Sector@test.com")
                .recipientType(NoticeRecipientType.SECTOR_CONSULTANT)
                .build();

        when(targetUnitAccountNoticeRecipients.getDefaultNoticeRecipients(accountId))
                .thenReturn(recipients);
        when(sectorUserAuthorityInfoService.getConsultantSectorUsersNoticeRecipients(sectorId))
                .thenReturn(sectors);

        // Invoke
        List<DefaultNoticeRecipient> result = service.getRecipients(request);

        // Verify
        assertThat(result).containsExactlyInAnyOrder(administrative, responsible, consultant);
        verify(targetUnitAccountNoticeRecipients, times(1))
                .getDefaultNoticeRecipients(accountId);
        verify(sectorUserAuthorityInfoService, times(1))
                .getConsultantSectorUsersNoticeRecipients(sectorId);
    }

    @Test
    void getType() {
        assertThat(service.getType()).isEqualTo(CcaRequestType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING);
    }
}
