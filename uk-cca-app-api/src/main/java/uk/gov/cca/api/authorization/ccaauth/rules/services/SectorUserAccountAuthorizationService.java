package uk.gov.cca.api.authorization.ccaauth.rules.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.TargetUnitAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AccountAuthorizationService;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;


@Service
@RequiredArgsConstructor
public class SectorUserAccountAuthorizationService implements AccountAuthorizationService {
    private final TargetUnitAuthorityInfoProvider targetUnitAuthorityInfoProvider;
    private final SectorUserSectorAssociationAuthService sectorUserSectorAssociationAuthService;

    /**
     * checks that SECTOR_USER has access to account
     *
     * @param user      the user to authorize.
     * @param accountId the account to check permission on.
     * @return if the SECTOR_USER is authorized on account.
     */
    @Override
    public boolean isAuthorized(AppUser user, Long accountId) {
        final Long sectorAssociationId = targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId);
        return sectorUserSectorAssociationAuthService.isAuthorized(user, sectorAssociationId);
    }

    /**
     * checks that SECTOR_USER has the permissions to account
     *
     * @param user       the user to authorize.
     * @param accountId  the account to check permission on.
     * @param permission to check
     * @return if the SECTOR_USER has the permissions on the account
     */
    @Override
    public boolean isAuthorized(AppUser user, Long accountId, String permission) {
        final Long sectorAssociationId = targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId);
        return sectorUserSectorAssociationAuthService.isAuthorized(user, sectorAssociationId, permission);
    }

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }
}
