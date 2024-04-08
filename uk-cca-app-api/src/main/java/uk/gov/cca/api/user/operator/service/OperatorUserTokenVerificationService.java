package uk.gov.cca.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.cca.api.user.core.service.UserInvitationTokenVerificationService;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.token.JwtTokenActionEnum;
import uk.gov.netz.api.token.JwtTokenService;

@Service
@RequiredArgsConstructor
public class OperatorUserTokenVerificationService {

    private final UserAuthService userAuthService;
    private final JwtTokenService jwtTokenService;
    private final UserInvitationTokenVerificationService userInvitationTokenVerificationService;

    public String verifyRegistrationToken(String token) {
        String userEmail = jwtTokenService.resolveTokenActionClaim(token, JwtTokenActionEnum.USER_REGISTRATION);

        if (userAuthService.getUserByEmail(userEmail).isPresent()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_REGISTERED);
        }

        return userEmail;
    }

    public AuthorityInfoDTO verifyInvitationTokenForPendingAuthority(String invitationToken) {
        return userInvitationTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitationToken, JwtTokenActionEnum.OPERATOR_INVITATION);
    }
}
