package uk.gov.cca.api.user.sectoruser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.userinfoapi.AuthenticationStatus;

@Service
@RequiredArgsConstructor
public class SectorUserRegistrationService {

    private final SectorUserAuthService sectorUserAuthService;
    private final SectorUserAuthorityService sectorUserAuthorityService;

    /**
     * Registers a new user with status {@link AuthenticationStatus#PENDING} and
     * adds him as {@link uk.gov.cca.api.common.domain.CcaRoleTypeConstants#SECTOR_USER}to the provided Sector Association.
     *
     * @param sectorUserInvitationDTO the {@link SectorUserInvitationDTO}
     * @param sectorAssociationId     the sector association id
     * @param currentUser             the logged-in {@link AppUser}
     */
    @Transactional
    public String registerUserToSectorAssociationWithStatusPending(SectorUserInvitationDTO sectorUserInvitationDTO,
                                                                 Long sectorAssociationId, AppUser currentUser) {
        String roleCode = sectorUserInvitationDTO.getRoleCode();

        String userId =
                sectorUserAuthService.registerSectorUser(
                        sectorUserInvitationDTO.getEmail(),
                        sectorUserInvitationDTO.getFirstName(),
                        sectorUserInvitationDTO.getLastName());

        String authorityUuid =
                sectorUserAuthorityService.createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, sectorUserInvitationDTO.getContactType(), userId, currentUser);

        return authorityUuid;
    }
}
