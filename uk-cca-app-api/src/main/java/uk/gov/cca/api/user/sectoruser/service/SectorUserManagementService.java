package uk.gov.cca.api.user.sectoruser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.rules.services.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityUpdateService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityDetailsDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.user.core.service.UserSecuritySetupService;
import uk.gov.netz.api.user.core.service.auth.AuthService;

@Service
@RequiredArgsConstructor
public class SectorUserManagementService {

	private final SectorUserAuthorityService authorityService;
    private final SectorUserAuthService sectorUserAuthService;
    private final SectorUserAuthorityUpdateService sectorUserAuthorityUpdateService;
    private final UserSecuritySetupService userSecuritySetupService;
    private final SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;
    private final SectorUserMapper sectorUserMapper;
    private final AuthService authService;

    /**
     * Returns the Sector User.
     *
     * @param sectorAssociationId sector association id
     * @param userId              Keycloak user id
     * @return {@link SectorUserDTO}
     */
    public SectorUserAuthorityDetailsDTO getSectorUserBySectorAssociationIdAndUserId(Long sectorAssociationId, String userId) {
        // Validate editing sector user
        CcaAuthorityDetails ccaAuthorityDetails = authorityService.getSectorUserAuthorityDetails(userId, sectorAssociationId);

        return sectorUserMapper.toSectorUserDTO(authService.getUserRepresentationById(userId), ccaAuthorityDetails);
    }

    public void updateSectorUser(Long sectorAssociationId, String userId, SectorUserAuthorityDetailsDTO updatedSectorUserDTO) {
        // update sector user by an already authorized admin regulator/sector user
        boolean hasEditSectorUserScope = true;
        // Validate and update CcaAuthorityDetails
        sectorUserAuthorityUpdateService.updateSectorUserAuthorityDetails(sectorAssociationId, userId, updatedSectorUserDTO.getContactType(), updatedSectorUserDTO.getOrganisationName(), hasEditSectorUserScope);

        // update sector user
        sectorUserAuthService.updateSectorUser(updatedSectorUserDTO);
    }

    public void updateCurrentSectorUser(AppUser appUser, Long sectorAssociationId, SectorUserAuthorityDetailsDTO updatedSectorUserDTO) {
        boolean hasEditSectorUserScope = sectorAssociationAuthorizationResourceService
                .hasUserScopeToSectorAssociation(appUser, Scope.EDIT_USER, sectorAssociationId);

        final String userId = appUser.getUserId();

        // Validate and update CcaAuthorityDetails
        sectorUserAuthorityUpdateService.updateSectorUserAuthorityDetails(sectorAssociationId, userId, updatedSectorUserDTO.getContactType(), updatedSectorUserDTO.getOrganisationName(), hasEditSectorUserScope);

        // update sector user
        sectorUserAuthService.updateSectorUser(updatedSectorUserDTO);
    }

    public void resetSectorUser2Fa(Long sectorAssociationId, String userId) {
        // Validate editing sector user
    	authorityService.getSectorUserAuthority(userId, sectorAssociationId);
        userSecuritySetupService.resetUser2Fa(userId);
    }
}
