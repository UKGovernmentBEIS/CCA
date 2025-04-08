package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.sectoruser;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.TargetUnitAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AccountAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;


@Service
@Order(200)
@RequiredArgsConstructor
public class SectorUserAccountAuthorizationService extends AccountAuthorizationService implements SectorUserResourceTypeAuthorizationService {
    private final TargetUnitAuthorityInfoProvider targetUnitAuthorityInfoProvider;
    private final SectorUserSectorAssociationAuthorizationService sectorUserSectorAssociationAuthorizationService;

    @Override
    public boolean isAuthorized(AppUser user, AuthorizationCriteria criteria) {
        if (criteria.getPermission() == null) {
            return isAuthorized(user, criteria.getAccountId());
        } else {
            return isAuthorized(user, criteria.getAccountId(), criteria.getPermission());
        }
    }

    /**
     * checks that SECTOR_USER has access to account
     *
     * @param user      the user to authorize.
     * @param accountId the account to check permission on.
     * @return if the SECTOR_USER is authorized on account.
     */
    public boolean isAuthorized(AppUser user, Long accountId) {
        final Long sectorAssociationId = targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId);
        return sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorAssociationId);
    }

    /**
     * checks that SECTOR_USER has the permissions to account
     *
     * @param user       the user to authorize.
     * @param accountId  the account to check permission on.
     * @param permission to check
     * @return if the SECTOR_USER has the permissions on the account
     */
    public boolean isAuthorized(AppUser user, Long accountId, String permission) {
        final Long sectorAssociationId = targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId);
        return sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorAssociationId, permission);
    }
}
