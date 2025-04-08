package uk.gov.cca.api.user.sectoruser.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityDetailsDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserRegistrationWithCredentialsDTO;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserMapper;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserRegistrationMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.user.application.UserRoleTypeAuthService;
import uk.gov.netz.api.user.core.service.auth.AuthService;
import uk.gov.netz.api.userinfoapi.AuthenticationStatus;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class SectorUserAuthService implements UserRoleTypeAuthService<SectorUserDTO> {

    private final AuthService authService;
    private final SectorUserMapper sectorUserMapper;
    private final SectorUserRegistrationMapper sectorUserRegistrationMapper;

    @Override
    public String  getRoleType() {
        return SECTOR_USER;
    }

    @Override
    public SectorUserDTO getUserById(String userId) {
        return sectorUserMapper.toSectorUserDTO(authService.getUserRepresentationById(userId));
    }
    
    @Override
	public SectorUserDTO getCurrentUserDTO(AppUser currentUser) {
		return getUserById(currentUser.getUserId());
	}

    /**
     * Registers a new user in keycloak with status {@link AuthenticationStatus#PENDING}
     * and {@link UserRepresentation#setEnabled} false.
     *
     * @param email     the user email
     * @param firstName the user first name
     * @param lastName  the user last name
     * @return the  user id (from keycloak)
     */
    public String registerSectorUser(String email, String firstName, String lastName) {
        UserRepresentation userRepresentation = sectorUserMapper.toUserRepresentation(email, firstName, lastName);
        return authService.saveUser(userRepresentation);
    }

    /**
     * Updates the keycloak user identified by the {@code userId} using the properties
     * provided in {@code sectorUserInvitation}
     * and set the status to {@link AuthenticationStatus#PENDING}.
     *
     * @param sectorUserInvitation {@link SectorUserInvitationDTO}
     */
    public void updateUser(SectorUserInvitationDTO sectorUserInvitation) {
        UserRepresentation userRepresentation = sectorUserMapper.toUserRepresentation(sectorUserInvitation.getEmail(),
                sectorUserInvitation.getFirstName(), sectorUserInvitation.getLastName());
        authService.saveUser(userRepresentation);
    }

    public void updateSectorUser(SectorUserAuthorityDetailsDTO updatedSectorUserDTO) {
        UserRepresentation updatedUser = this.sectorUserMapper.toUserRepresentation(updatedSectorUserDTO);
        authService.saveUser(updatedUser);
    }

    public SectorUserDTO enableAndUpdateUserAndSetPassword(
            SectorUserRegistrationWithCredentialsDTO sectorUserRegistrationWithCredentialsDTO, String userId) {
        UserRepresentation keycloakUser = authService.getUserRepresentationById(userId);

        UserRepresentation userRepresentation = sectorUserRegistrationMapper
                .toUserRepresentation(sectorUserRegistrationWithCredentialsDTO, keycloakUser.getEmail(), keycloakUser.getId());

        authService.enableAndSaveUserAndSetPassword(userRepresentation, sectorUserRegistrationWithCredentialsDTO.getPassword());

        return sectorUserMapper.toSectorUserDTO(userRepresentation);
    }

    public SectorUserDTO setUserPassword(String userId, String password) {
        UserRepresentation userRepresentation = authService.setUserPassword(userId, password);
        return sectorUserMapper.toSectorUserDTO(userRepresentation);
    }
}
