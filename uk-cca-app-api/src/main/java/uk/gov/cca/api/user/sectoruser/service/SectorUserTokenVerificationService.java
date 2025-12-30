package uk.gov.cca.api.user.sectoruser.service;


import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityService;
import uk.gov.cca.api.token.CcaJwtTokenAction;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.token.JwtTokenService;

@Service
@RequiredArgsConstructor
public class SectorUserTokenVerificationService {
	private final JwtTokenService jwtTokenService;
    private final CcaAuthorityService authorityService;

    public CcaAuthorityInfoDTO verifyInvitationToken(String invitationToken, AppUser currentUser) {
    	CcaAuthorityInfoDTO authorityInfo = verifyInvitationTokenForPendingAuthority(invitationToken);
    	verifyInvitedUserIsTheCurrentUserIfExist(authorityInfo, currentUser);
    	return authorityInfo;
    }

	private CcaAuthorityInfoDTO verifyInvitationTokenForPendingAuthority(String invitationToken) {
		String authorityUuid = jwtTokenService.resolveTokenActionClaim(invitationToken, CcaJwtTokenAction.SECTOR_USER_INVITATION);
        return authorityService.findCcaAuthorityByUuidAndStatusPending(authorityUuid)
        		.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
	}
	
	private void verifyInvitedUserIsTheCurrentUserIfExist(CcaAuthorityInfoDTO authorityInfo, AppUser currentUser) {
		if(currentUser != null && 
				!currentUser.getUserId().equals(authorityInfo.getUserId())) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
	}
}
