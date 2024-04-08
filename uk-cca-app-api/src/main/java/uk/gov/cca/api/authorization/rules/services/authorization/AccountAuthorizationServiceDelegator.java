package uk.gov.cca.api.authorization.rules.services.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.List;
import java.util.Optional;

/**
 * Service that delegates account related authorization to {@link RoleType} based services.
 */
@Service
@RequiredArgsConstructor
public class AccountAuthorizationServiceDelegator {
    private final List<AccountAuthorizationService> accountAuthorizationServices;

    /**
     * checks that user has access to account
     * @param user the user to authorize.
     * @param accountId the account to check permission on.
     * @return if the user is authorized on account.
     */
    public boolean isAuthorized(AppUser user, Long accountId) {
        return getUserService(user)
                .map(accountAuthorizationService -> accountAuthorizationService.isAuthorized(user, accountId)).orElse(false);
    }

    /**
     * checks that user has the permissions to account
     * @param user the user to authorize.
     * @param accountId the account to check permission on.
     * @param permission to check
     * @return if the user has the permissions on the account
     */
    public boolean isAuthorized(AppUser user, Long accountId, String permission) {
        return getUserService(user)
                .map(accountAuthorizationService -> accountAuthorizationService.isAuthorized(user, accountId, permission)).orElse(false);
    }

    private Optional<AccountAuthorizationService> getUserService(AppUser user) {
        return accountAuthorizationServices.stream()
                .filter(accountAuthorizationService -> accountAuthorizationService.getRoleType().equals(user.getRoleType()))
                .findAny();
    }
}
