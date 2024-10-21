package uk.gov.cca.api.user.sectoruser.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

@Service
@RequiredArgsConstructor
public class SectorUserInvitationService {

    private final UserAuthService authUserService;
    private final SectorUserRegistrationService sectorUserRegistrationService;
    private final ExistingSectorUserInvitationService existingSectorUserInvitationService;
    private final SectorUserNotificationGateway sectorUserNotificationGateway;

    /**
     * Invites a new user to join a sector with a specified role.
     *
     * @param sectorAssociationId     the sector id
     * @param sectorUserInvitationDTO the {@link SectorUserInvitationDTO}
     * @param currentUser             the current logged-in {@link AppUser}
     */
    @Transactional
    public void inviteUserToSectorAssociation(Long sectorAssociationId, String sectorAssociationName, SectorUserInvitationDTO sectorUserInvitationDTO, AppUser currentUser) {

        Optional<UserInfoDTO> registeredEmail = authUserService.getUserByEmail(sectorUserInvitationDTO.getEmail());

        String authorityUuid = registeredEmail
                .map(userRepresentation -> addExistingUserToSectorAssociation(userRepresentation, sectorUserInvitationDTO, sectorAssociationId, currentUser))
                .orElseGet(() -> sectorUserRegistrationService.registerUserToSectorAssociationWithStatusPending(sectorUserInvitationDTO, sectorAssociationId, currentUser));
        
        sectorUserNotificationGateway.notifyInvitedUser(sectorUserInvitationDTO, sectorAssociationName, authorityUuid);
    }

    private String addExistingUserToSectorAssociation(UserInfoDTO userDTO, SectorUserInvitationDTO sectorUserInvitationDTO,
                                                      Long sectorAssociationId, AppUser currentUser) {
        return existingSectorUserInvitationService.addExistingUserToSectorAssociation(sectorUserInvitationDTO, sectorAssociationId, userDTO.getUserId(), currentUser);
    }
}
