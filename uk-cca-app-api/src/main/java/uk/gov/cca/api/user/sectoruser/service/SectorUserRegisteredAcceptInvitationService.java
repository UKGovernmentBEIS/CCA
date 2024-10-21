package uk.gov.cca.api.user.sectoruser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityService;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class SectorUserRegisteredAcceptInvitationService {

    private final SectorUserAuthorityService sectorUserAuthorityService;
    private final UserRoleTypeService userRoleTypeService;
    private final UserAuthService userAuthService;
    private final SectorUserNotificationGateway sectorUserNotificationGateway;

    public void acceptAuthorityAndNotify(Long authorityId) {
        // accept authority
        final Authority authority = sectorUserAuthorityService.acceptAuthority(authorityId);

        //create user role type if not exist
        userRoleTypeService.createUserRoleTypeIfNotExist(authority.getUserId(), SECTOR_USER);

        final UserInfoDTO inviteeUser = userAuthService.getUserByUserId(authority.getUserId());
        final UserInfoDTO inviterUser = userAuthService.getUserByUserId(authority.getCreatedBy());

        // Notify invitee and inviter
        sectorUserNotificationGateway.notifyInviteeAcceptedInvitation(inviteeUser);
        sectorUserNotificationGateway.notifyInviterAcceptedInvitation(inviteeUser, inviterUser);
    }

}
