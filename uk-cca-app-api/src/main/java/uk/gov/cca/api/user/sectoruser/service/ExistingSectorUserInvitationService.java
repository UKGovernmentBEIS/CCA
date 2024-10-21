package uk.gov.cca.api.user.sectoruser.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorAuthorityQueryService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;

@Log4j2
@Service
@RequiredArgsConstructor
public class ExistingSectorUserInvitationService {

    private final SectorUserAuthorityService sectorUserAuthorityService;
    private final SectorUserAuthService sectorUserAuthService;
    private final SectorAuthorityQueryService sectorAuthorityQueryService;

    @Transactional
    public String addExistingUserToSectorAssociation(SectorUserInvitationDTO sectorUserInvitationDTO,
                                                     Long sectorAssociationId, String userId, AppUser currentUser) {

        log.debug("Adding existing sector user '{}' to sector association '{}'", () -> userId, () -> sectorAssociationId);

        checkInvitedUserStatusAndRole(userId, sectorUserInvitationDTO);

        String authorityUuid =
                sectorUserAuthorityService.createPendingAuthorityForSectorUser(sectorAssociationId, sectorUserInvitationDTO.getRoleCode(), sectorUserInvitationDTO.getContactType(), userId, currentUser);

        return authorityUuid;
    }

    private void checkInvitedUserStatusAndRole(String userId, SectorUserInvitationDTO sectorUserInvitationDTO) {
        if (sectorAuthorityQueryService.existsAuthorityNotForSectorAssociation(userId)) {
            log.error("User '{}' already exists in CCA", () -> userId);
            throw new BusinessException(CcaErrorCode.AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_THAN_SECTOR_USER);
        }

        sectorUserAuthService.updateUser(sectorUserInvitationDTO);
    }
}
