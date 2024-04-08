package uk.gov.cca.api.user.verifier.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.cca.api.user.core.service.UserInvitationTokenVerificationService;
import uk.gov.netz.api.token.JwtTokenActionEnum;

@Service
@RequiredArgsConstructor
public class VerifierUserTokenVerificationService {

    private final UserInvitationTokenVerificationService userInvitationTokenVerificationService;

    public AuthorityInfoDTO verifyInvitationTokenForPendingAuthority(String invitationToken) {
        return userInvitationTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitationToken, JwtTokenActionEnum.VERIFIER_INVITATION);
    }
}
