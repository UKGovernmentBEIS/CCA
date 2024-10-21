package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.common.transform.TargetUnitDetailsMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.cca.api.account.domain.dto.NoticeRecipientType.ADMINISTRATIVE_CONTACT;
import static uk.gov.cca.api.account.domain.dto.NoticeRecipientType.RESPONSIBLE_PERSON;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountNoticeRecipients {

    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private static final TargetUnitDetailsMapper TARGET_UNIT_DETAILS_MAPPER = Mappers.getMapper(TargetUnitDetailsMapper.class);

    public List<NoticeRecipientDTO> getNoticeRecipients(final Long accountId) {
        List<NoticeRecipientDTO> defaultNoticeRecipients = new ArrayList<>();

        TargetUnitAccountDetailsDTO accountDetails = accountReferenceDetailsService
                .getTargetUnitAccountDetails(accountId);

        TargetUnitAccountContactDTO responsiblePerson = accountDetails.getResponsiblePerson();
        defaultNoticeRecipients.add(TARGET_UNIT_DETAILS_MAPPER.toNoticeRecipientDTO(responsiblePerson, RESPONSIBLE_PERSON));

        TargetUnitAccountContactDTO administrative = accountDetails.getAdministrativeContactDetails();
        defaultNoticeRecipients.add(TARGET_UNIT_DETAILS_MAPPER.toNoticeRecipientDTO(administrative, ADMINISTRATIVE_CONTACT));

        SectorAssociationContactDTO sectorContact = accountReferenceDetailsService
                .getSectorAssociationContactByAccountId(accountId);
        defaultNoticeRecipients.add(TARGET_UNIT_DETAILS_MAPPER.toSectorAssociationNoticeRecipientDTO(sectorContact));

        return defaultNoticeRecipients;
    }

    public List<NoticeRecipientDTO> getNoticeRecipients(final Long accountId,
                                                        final UnderlyingAgreementTargetUnitDetails targetUnitDetails) {
        List<NoticeRecipientDTO> defaultNoticeRecipients = new ArrayList<>();

        // Get from Target Unit Account param
        UnderlyingAgreementTargetUnitResponsiblePerson responsiblePerson = targetUnitDetails.getResponsiblePersonDetails();
        defaultNoticeRecipients.add(TARGET_UNIT_DETAILS_MAPPER.toResponsiblePersonNoticeRecipientDTO(responsiblePerson));

        // Get from persistent Target Unit Account
        TargetUnitAccountContactDTO administrative = accountReferenceDetailsService
                .getTargetUnitAccountDetails(accountId).getAdministrativeContactDetails();
        defaultNoticeRecipients.add(TARGET_UNIT_DETAILS_MAPPER.toNoticeRecipientDTO(administrative, ADMINISTRATIVE_CONTACT));

        SectorAssociationContactDTO sectorContact = accountReferenceDetailsService
                .getSectorAssociationContactByAccountId(accountId);
        defaultNoticeRecipients.add(TARGET_UNIT_DETAILS_MAPPER.toSectorAssociationNoticeRecipientDTO(sectorContact));

        return defaultNoticeRecipients;
    }

    public List<DefaultNoticeRecipient> getDefaultNoticeRecipients(final Long accountId) {
        return this.getNoticeRecipients(accountId).stream().map(recipient ->
                DefaultNoticeRecipient.builder()
                        .name(recipient.getFirstName() + " " + recipient.getLastName())
                        .email(recipient.getEmail())
                        .recipientType(recipient.getType())
                        .build()
        ).collect(Collectors.toList());
    }

    public List<DefaultNoticeRecipient> getDefaultNoticeRecipients(final Long accountId,
                                                                   final UnderlyingAgreementTargetUnitDetails targetUnitDetails) {
        return this.getNoticeRecipients(accountId, targetUnitDetails).stream().map(recipient ->
                        DefaultNoticeRecipient.builder()
                                .name(recipient.getFirstName() + " " + recipient.getLastName())
                                .email(recipient.getEmail())
                                .recipientType(recipient.getType())
                                .build()
                ).collect(Collectors.toList());
    }
}
