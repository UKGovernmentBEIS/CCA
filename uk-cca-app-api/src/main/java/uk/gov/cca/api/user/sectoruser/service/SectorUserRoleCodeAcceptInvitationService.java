package uk.gov.cca.api.user.sectoruser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserWithAuthorityDTO;
import uk.gov.netz.api.authorization.core.service.RoleService;
import uk.gov.netz.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;

import java.util.HashSet;
import java.util.Set;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class SectorUserRoleCodeAcceptInvitationService {

    private final SectorUserRegisteredAcceptInvitationService sectorUserRegisteredAcceptInvitationService;
    private final UserAuthService userAuthService;
    private final RoleService roleService;

    @Transactional
    public UserInvitationStatus acceptInvitation(SectorUserWithAuthorityDTO sectorUserWithAuthorityDTO, String roleCode) {
        if(sectorUserWithAuthorityDTO.isEnabled() && getRoleCodes().contains(roleCode)) {
            if(userAuthService.hasUserPassword(sectorUserWithAuthorityDTO.getUserId())) {
                sectorUserRegisteredAcceptInvitationService
                        .acceptAuthorityAndNotify(sectorUserWithAuthorityDTO.getUserAuthorityId());
                return UserInvitationStatus.ACCEPTED;
            } else {
                return UserInvitationStatus.ALREADY_REGISTERED_SET_PASSWORD_ONLY;
            }
        } else {
            return UserInvitationStatus.PENDING_TO_REGISTERED_SET_REGISTER_FORM;
        }
    }

    public Set<String> getRoleCodes() {
        return new HashSet<>(roleService.getCodesByType(SECTOR_USER));
    }
}