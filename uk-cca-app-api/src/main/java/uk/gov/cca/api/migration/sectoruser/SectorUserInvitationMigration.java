package uk.gov.cca.api.migration.sectoruser;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.migration.DryRunException;
import uk.gov.cca.api.migration.ExecutionMode;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserInvitationService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class SectorUserInvitationMigration extends MigrationBaseService {
    
    private final UserAuthService authUserService;
    private final SectorUserInvitationService sectorUserInvitationService;

    private final SectorUserInvitationMapper sectorUserInvitationMapper;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    
    private final UserBuilder userBuilder;

    @Override
    public String getResource() {
        return "sector-users-invite";
    }

    @Override
    public void migrateDryRun(String input) {
        List<String> failedEntries = sendUserInvitations(input, ExecutionMode.DRY);
        failedEntries.add("CAUTION: Execution in DRY mode leads to no invitations being send.");
        throw new DryRunException(failedEntries);
    }

    @Override
    public List<String> migrate(String input) {
        return sendUserInvitations(input, ExecutionMode.COMMIT);
    }

    public List<String> sendUserInvitations(String input, ExecutionMode executionMode) {
        if (StringUtils.isEmpty(input)) {
            return List.of("Please insert details for at least one user invitation");
        }

        List<String> failedEntries = new ArrayList<>();
        AtomicInteger failedCounter = new AtomicInteger(0);

        List<SectorUserInvitationVO> sectorUserInvitations = Arrays.stream(input.split(";"))
                .map(val -> val.split("\\|"))
                .map(parts -> sectorUserInvitationMapper.toSectorUserInvitation(parts, failedEntries))
                .toList();

        for (SectorUserInvitationVO userInvitation : sectorUserInvitations) {
            if (userInvitation != null) {
                inviteUserToSectorAssociation(userInvitation, failedEntries, failedCounter, executionMode);
            } else {
                failedCounter.incrementAndGet();
            }
        }

        if (executionMode == ExecutionMode.COMMIT) {
            failedEntries
                    .add(failedCounter.get() + "/" + sectorUserInvitations.size() + " invitations sending failed.");
        }

        return failedEntries;
    }

    private void inviteUserToSectorAssociation(SectorUserInvitationVO userInvitation, List<String> failedEntries,
            AtomicInteger failedCounter, ExecutionMode executionMode) {
        Long rowId = userInvitation.getRowId();
        final String sectorAcronym = userInvitation.getSectorAcronym();

        Optional<Long> sectorId = sectorAssociationQueryService.getSectorAssociationIdByAcronym(sectorAcronym);
        if (sectorId.isEmpty()) {
            failedEntries.add(SectorUserInvitationHelper.constructErrorMessage(rowId, String.format("No sector association found with acronym %s ", sectorAcronym)));
            failedCounter.incrementAndGet();
            return;
        }
        
        Optional<UserInfoDTO> inviterUserOptional = authUserService.getUserByEmail(userInvitation.getInviterEmail());
        if (inviterUserOptional.isEmpty()) {
            failedEntries.add(SectorUserInvitationHelper.constructErrorMessage(rowId, String.format("No inviter user found with email %s ", userInvitation.getInviterEmail())));
            failedCounter.incrementAndGet();
            return;
        }

        AppUser regulatorAdminUser = userBuilder.constructRegulatorAdministrator(inviterUserOptional.get(), CompetentAuthorityEnum.ENGLAND, failedEntries, rowId);
        SectorUserInvitationDTO sectorUserInvitationDTO = userBuilder.constructInvitedSectorUser(userInvitation, failedEntries);

        if (regulatorAdminUser != null && sectorUserInvitationDTO != null) {
            doInviteUserToSectorAssociation(sectorId.get(), sectorUserInvitationDTO, regulatorAdminUser, rowId, failedEntries, failedCounter, executionMode);
        } else {
            failedCounter.incrementAndGet();
        }
    }
    
    private void doInviteUserToSectorAssociation(Long sectorId, SectorUserInvitationDTO userInvitationDTO, AppUser inviter, Long rowId, List<String> failedEntries, AtomicInteger failedCounter,
            ExecutionMode executionMode) {
        try {
            switch (executionMode) {
            case COMMIT:
                sectorUserInvitationService.inviteUserToSectorAssociation(sectorId, userInvitationDTO, inviter);
                break;
            case DRY:
                // mock (async actions + keyclock request cannot be rollbacked)
                break;
            default:
                break;
            }
        } catch (Exception ex) {
            failedEntries.add(
                    SectorUserInvitationHelper.constructErrorMessage(rowId, String.format("Error during user invitation - %s", ex.getMessage())));
            failedCounter.incrementAndGet();
        }
    }

}
