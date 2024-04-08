package uk.gov.cca.api.authorization.rules.services.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.providers.AccountAuthorityInfoProvider;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.Optional;

/**
 * Service that checks if a VERIFIER user is authorized on an account.
 */
@Service
@RequiredArgsConstructor
public class VerifierAccountAuthorizationService implements AccountAuthorizationService {

    private final AccountAuthorityInfoProvider accountAuthorityInfoProvider;
    private final VerifierVerificationBodyAuthorizationService verifierVerificationBodyAuthorizationService;
    private final VerifierAccountAccessService verifierAccountAccessService;

    /**
     * Checks that VERIFIER has access to account.
     * @param user the user to authorize
     * @param accountId the account to check permission on
     * @return if the VERIFIER is authorized on account.
     */
    @Override
    public boolean isAuthorized(AppUser user, Long accountId) {
        final boolean authorized = this.checkAuthorizedAccount(user, accountId);
        if (!authorized) {
            return false;
        }

        Optional<Long> accountVerificationBodyOptional = accountAuthorityInfoProvider.getAccountVerificationBodyId(accountId);
        return accountVerificationBodyOptional
            .map(accountVerificationBody -> verifierVerificationBodyAuthorizationService.isAuthorized(user, accountVerificationBody))
            .orElse(false);
    }

    /**
     * Checks that VERIFIER has the permissions to account.
     * @param user the user to authorize
     * @param accountId the account to check permission on
     * @param permission to check
     * @return if the VERIFIER has the permissions on the account
     */
    @Override
    public boolean isAuthorized(AppUser user, Long accountId, String permission) {
        final boolean authorized = this.checkAuthorizedAccount(user, accountId);
        if (!authorized) {
            return false;
        }

        Optional<Long> accountVerificationBodyOptional = accountAuthorityInfoProvider.getAccountVerificationBodyId(accountId);
        return accountVerificationBodyOptional
            .map(accountVerificationBody -> verifierVerificationBodyAuthorizationService.isAuthorized(user, accountVerificationBody, permission))
            .orElse(false);
    }

    private boolean checkAuthorizedAccount(final AppUser user, final Long accountId) {
        return verifierAccountAccessService.findAuthorizedAccountIds(user).contains(accountId);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.VERIFIER;
    }
}
