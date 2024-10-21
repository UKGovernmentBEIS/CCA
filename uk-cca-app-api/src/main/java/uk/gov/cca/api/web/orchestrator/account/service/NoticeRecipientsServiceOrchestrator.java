package uk.gov.cca.api.web.orchestrator.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.user.operator.service.OperatorUserAuthorityInfoService;
import uk.gov.cca.api.user.sectoruser.service.SectorUserAuthorityInfoService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeRecipientsServiceOrchestrator {

    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final OperatorUserAuthorityInfoService operatorUserAuthorityInfoService;
    private final SectorUserAuthorityInfoService sectorUserAuthorityInfoService;

    public List<AdditionalNoticeRecipientDTO> getAdditionalNoticeRecipients(AppUser currentUser, Long accountId) {
        List<AdditionalNoticeRecipientDTO> additionalNoticeRecipients = new ArrayList<>(
                operatorUserAuthorityInfoService.getCandidateOperatorNoticeRecipients(currentUser, accountId));

        Long sectorAssociationId = targetUnitAccountQueryService.getAccountSectorAssociationId(accountId);

        additionalNoticeRecipients.addAll(
                sectorUserAuthorityInfoService.getCandidateSectorUsersNoticeRecipients(currentUser, sectorAssociationId));

        return additionalNoticeRecipients.stream()
                .sorted(Comparator.comparing(NoticeRecipientDTO::getFirstName)).toList();
    }
}
