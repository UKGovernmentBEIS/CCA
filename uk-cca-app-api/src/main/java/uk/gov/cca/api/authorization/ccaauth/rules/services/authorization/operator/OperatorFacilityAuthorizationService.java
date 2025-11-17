package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.operator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.FacilityAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.authorization.rules.services.authorization.operator.OperatorAccountAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.operator.OperatorResourceTypeAuthorizationService;

@Service
@RequiredArgsConstructor
@Order(300)
public class OperatorFacilityAuthorizationService implements OperatorResourceTypeAuthorizationService {

	private final OperatorAccountAuthorizationService operatorAccountAuthorizationService;
	private final FacilityAuthorityInfoProvider facilityAuthorityInfoProvider;

    @Override
    public boolean isAuthorized(AppUser user, AuthorizationCriteria criteria) {
        long facilityId = Long.parseLong(criteria.getRequestResources().get(CcaResourceType.FACILITY));
        if (criteria.getPermission() == null) {
            return isAuthorized(user, facilityId);
        } else {
            return isAuthorized(user, facilityId, criteria.getPermission());
        }
    }

    @Override
    public boolean isApplicable(AuthorizationCriteria criteria) {
        return criteria.getRequestResources().containsKey(CcaResourceType.FACILITY);
    }

    public boolean isAuthorized(AppUser user, Long facilityId) {
    	Long accountId = facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId);
        return operatorAccountAuthorizationService.isAuthorized(user, accountId);
    }

    public boolean isAuthorized(AppUser user, Long facilityId, String permission) {
    	Long accountId = facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId);
        return operatorAccountAuthorizationService.isAuthorized(user, accountId, permission);
    }
}
