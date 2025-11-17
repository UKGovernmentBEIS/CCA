package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.sectoruser;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.TargetUnitAuthorityInfoProvider;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.FacilityAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AccountAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;

@Service
@Order(300)
@RequiredArgsConstructor
public class SectorUserFacilityAuthorizationService extends AccountAuthorizationService implements SectorUserResourceTypeAuthorizationService {

	private final TargetUnitAuthorityInfoProvider targetUnitAuthorityInfoProvider;
	private final FacilityAuthorityInfoProvider facilityAuthorityInfoProvider;
    private final SectorUserSectorAssociationAuthorizationService sectorUserSectorAssociationAuthorizationService;

    @Override
    public boolean isAuthorized(AppUser user, AuthorizationCriteria criteria) {
    	long facilityId = Long.parseLong(criteria.getRequestResources().get(CcaResourceType.FACILITY));
        if (criteria.getPermission() == null) {
            return isAuthorized(user, facilityId);
        } else {
            return isAuthorized(user, facilityId, criteria.getPermission());
        }
    }

    public boolean isAuthorized(AppUser user, Long facilityId) {
    	final Long accountId = facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId);
        final Long sectorAssociationId = targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId);
        return sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorAssociationId);
    }

    public boolean isAuthorized(AppUser user, Long facilityId, String permission) {
    	final Long accountId = facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId);
        final Long sectorAssociationId = targetUnitAuthorityInfoProvider.getAccountSectorAssociationId(accountId);
        return sectorUserSectorAssociationAuthorizationService.isAuthorized(user, sectorAssociationId, permission);
    }
    
    @Override
    public boolean isApplicable(AuthorizationCriteria criteria) {
        return criteria.getRequestResources().containsKey(CcaResourceType.FACILITY);
    }
}
