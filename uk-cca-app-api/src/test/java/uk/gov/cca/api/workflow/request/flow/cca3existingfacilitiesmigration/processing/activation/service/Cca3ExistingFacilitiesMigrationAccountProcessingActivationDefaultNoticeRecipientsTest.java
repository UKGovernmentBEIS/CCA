package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.user.sectoruser.service.SectorUserAuthorityInfoService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingActivationDefaultNoticeRecipientsTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivationDefaultNoticeRecipients service;

    @Mock
    private TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    @Mock
    private SectorUserAuthorityInfoService sectorUserAuthorityInfoService;

    @Test
    void getRecipients() {
        final Long accountId = 1L;
        final Long sectorId = 2L;
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                                .sectorAssociationId(sectorId)
                                .build())
                        .requestResources(List.of(RequestResource.builder()
                                .resourceType(ResourceType.ACCOUNT)
                                .resourceId(accountId.toString())
                                .build())
                        ).build())
                .build();

        final NoticeRecipientDTO administrative = NoticeRecipientDTO.builder()
                .firstName("AdministrativeFn")
                .lastName("AdministrativeLn")
                .email("administrative@test.com")
                .type(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                .build();
        final NoticeRecipientDTO responsible = NoticeRecipientDTO.builder()
                .firstName("ResponsibleFn")
                .lastName("ResponsibleLn")
                .email("responsiblePerson@test.com")
                .type(NoticeRecipientType.RESPONSIBLE_PERSON)
                .build();
        final NoticeRecipientDTO sector = NoticeRecipientDTO.builder()
                .firstName("SectorFn")
                .lastName("SectorLn")
                .email("Sector@test.com")
                .type(NoticeRecipientType.SECTOR_CONSULTANT)
                .build();
        List<NoticeRecipientDTO> recipients = Stream.of(administrative, responsible).collect(Collectors.toList());
        final List<AdditionalNoticeRecipientDTO> sectors = List.of(
                AdditionalNoticeRecipientDTO.builder()
                        .firstName("SectorFn")
                        .lastName("SectorLn")
                        .email("Sector@test.com")
                        .type(NoticeRecipientType.SECTOR_USER)
                        .build()
        );

        when(targetUnitAccountNoticeRecipients.getNoticeRecipients(accountId))
                .thenReturn(recipients);
        when(sectorUserAuthorityInfoService.getConsultantSectorUsersNoticeRecipients(sectorId))
                .thenReturn(sectors);

        // Invoke
        List<NoticeRecipientDTO> result = service.getRecipients(requestTask);

        // Verify
        assertThat(result).containsExactlyInAnyOrder(administrative, responsible, sector);
        verify(targetUnitAccountNoticeRecipients, times(1))
                .getNoticeRecipients(accountId);
        verify(sectorUserAuthorityInfoService, times(1))
                .getConsultantSectorUsersNoticeRecipients(sectorId);
    }

    @Test
    void getTypes() {
        assertThat(service.getTypes()).containsExactly(CcaRequestTaskType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION);
    }
}
