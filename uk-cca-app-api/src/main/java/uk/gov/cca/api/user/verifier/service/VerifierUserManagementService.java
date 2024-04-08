package uk.gov.cca.api.user.verifier.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.cca.api.user.core.service.UserSecuritySetupService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.user.verifier.domain.VerifierUserDTO;

@Service
@RequiredArgsConstructor
public class VerifierUserManagementService {

    private final VerifierAuthorityService verifierAuthorityService;
    private final VerifierUserAuthService verifierUserAuthService;
    private final UserSecuritySetupService userSecuritySetupService;

    public VerifierUserDTO getVerifierUserById(AppUser user, String userId) {
        validateUserBasedOnAuthUserRole(user, userId);
        return verifierUserAuthService.getVerifierUserById(userId);
    }

    public void updateVerifierUserById(AppUser appUser, String userId, VerifierUserDTO verifierUserDTO) {
        validateUserBasedOnAuthUserRole(appUser, userId);
        verifierUserAuthService.updateVerifierUser(userId, verifierUserDTO);
    }

    public void updateCurrentVerifierUser(AppUser appUser, VerifierUserDTO verifierUserDTO) {
        verifierUserAuthService.updateVerifierUser(appUser.getUserId(), verifierUserDTO);
    }

    public void resetVerifier2Fa(AppUser appUser, String userId) {
        validateUserBasedOnAuthUserRole(appUser, userId);
        userSecuritySetupService.resetUser2Fa(userId);
    }

    private void validateUserBasedOnAuthUserRole(AppUser appUser, String userId) {
        switch (appUser.getRoleType()) {
            case REGULATOR:
                validateUserIsVerifier(userId);
                break;
            case VERIFIER:
                validateUserHasAccessToVerificationBody(appUser, userId);
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format("User with role type %s can not access verifier user", appUser.getRoleType()));
        }
    }

    /** Validate if user has access to queried user's verification body. */
    private void validateUserHasAccessToVerificationBody(AppUser appUser, String userId) {
        Long verificationBodyId = appUser.getVerificationBodyId();

        if (!verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verificationBodyId)) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY);
        }
    }

    private void validateUserIsVerifier(String userId) {
        if (!verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_IS_NOT_VERIFIER);
        }
    }
}
