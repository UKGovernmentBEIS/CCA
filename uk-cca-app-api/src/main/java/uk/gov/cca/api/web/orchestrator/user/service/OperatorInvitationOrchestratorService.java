package uk.gov.cca.api.web.orchestrator.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserInvitationDTO;
import uk.gov.cca.api.user.operator.service.CcaOperatorUserInvitationService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OperatorInvitationOrchestratorService {

    private final CcaOperatorUserInvitationService operatorUserInvitationService;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;

    /**
     * Invites a new user to join a sector with a specified role.
     *
     * @param accountId         the target unit id
     * @param userInvitationDTO the {@link SectorUserInvitationDTO}
     * @param currentUser       the current logged-in {@link AppUser}
     */
    @Transactional
    public void inviteUserToAccount(Long accountId, CcaOperatorUserInvitationDTO userInvitationDTO, AppUser currentUser) {

        String accountName = targetUnitAccountQueryService.getAccountName(accountId);
        Objects.requireNonNull(accountName);

        operatorUserInvitationService.inviteUserToTargetUnit(accountId, accountName, userInvitationDTO, currentUser);

    }
}
