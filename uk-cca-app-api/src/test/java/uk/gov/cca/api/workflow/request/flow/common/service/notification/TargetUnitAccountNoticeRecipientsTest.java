package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountNoticeRecipientsTest {

    @InjectMocks
    private TargetUnitAccountNoticeRecipients service;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Test
    void getDefaultNoticeRecipients() {
        final long accountId = 1L;
        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .administrativeContactDetails(TargetUnitAccountContactDTO.builder()
                        .firstName("Administrative")
                        .lastName("Last Name")
                        .email("administrative@test.com")
                        .build())
                .responsiblePerson(TargetUnitAccountContactDTO.builder()
                        .firstName("Responsible")
                        .lastName("Last Name")
                        .email("responsiblePerson@test.com")
                        .build())
                .build();
        final SectorAssociationContactDTO sectorContact = SectorAssociationContactDTO.builder()
                .firstName("Sector")
                .lastName("Last Name")
                .email("sector@test.com")
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

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(accountReferenceDetailsService.getSectorAssociationContactByAccountId(accountId))
                .thenReturn(sectorContact);

        // Invoke
        List<DefaultNoticeRecipient> actual = service.getDefaultNoticeRecipients(accountId);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(accountReferenceDetailsService, times(1))
                .getSectorAssociationContactByAccountId(accountId);
    }

    @Test
    void getDefaultNoticeRecipients_with_target_unit_details() {
        final long accountId = 1L;
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails =
                UnderlyingAgreementTargetUnitDetails.builder()
                        .responsiblePersonDetails(UnderlyingAgreementTargetUnitResponsiblePerson.builder()
                                .firstName("ResponsibleUNA")
                                .lastName("Last NameUNA")
                                .email("responsiblePersonUNA@test.com")
                                .build())
                        .build();
        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .administrativeContactDetails(TargetUnitAccountContactDTO.builder()
                        .firstName("Administrative")
                        .lastName("Last Name")
                        .email("administrative@test.com")
                        .build())
                .responsiblePerson(TargetUnitAccountContactDTO.builder()
                        .firstName("Responsible")
                        .lastName("Last Name")
                        .email("responsiblePerson@test.com")
                        .build())
                .build();
        final SectorAssociationContactDTO sectorContact = SectorAssociationContactDTO.builder()
                .firstName("Sector")
                .lastName("Last Name")
                .email("sector@test.com")
                .build();

        final List<DefaultNoticeRecipient> expected = List.of(
                DefaultNoticeRecipient.builder()
                        .name("ResponsibleUNA Last NameUNA")
                        .email("responsiblePersonUNA@test.com")
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

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(accountReferenceDetailsService.getSectorAssociationContactByAccountId(accountId))
                .thenReturn(sectorContact);

        // Invoke
        List<DefaultNoticeRecipient> actual = service.getDefaultNoticeRecipients(accountId, targetUnitDetails);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(accountReferenceDetailsService, times(1))
                .getSectorAssociationContactByAccountId(accountId);
    }
}
