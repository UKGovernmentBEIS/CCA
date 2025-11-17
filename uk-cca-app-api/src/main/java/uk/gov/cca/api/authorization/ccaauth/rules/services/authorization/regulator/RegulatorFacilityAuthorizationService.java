package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.regulator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.FacilityAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.providers.AccountAuthorityInfoProvider;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.authorization.rules.services.authorization.regulator.RegulatorCompetentAuthorityAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.regulator.RegulatorResourceTypeAuthorizationService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Service
@Order(400)
@RequiredArgsConstructor
public class RegulatorFacilityAuthorizationService implements RegulatorResourceTypeAuthorizationService  {

	private final FacilityAuthorityInfoProvider facilityAuthorityInfoProvider;
	private final AccountAuthorityInfoProvider accountAuthorityInfoProvider;
    private final RegulatorCompetentAuthorityAuthorizationService regulatorCompetentAuthorityAuthorizationService;

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
        CompetentAuthorityEnum accountCompetentAuthority = accountAuthorityInfoProvider.getAccountCa(accountId);
        return regulatorCompetentAuthorityAuthorizationService.isAuthorized(user, accountCompetentAuthority);
    }

    public boolean isAuthorized(AppUser user, Long facilityId, String permission) {
    	Long accountId = facilityAuthorityInfoProvider.getAccountIdByFacilityId(facilityId);
        CompetentAuthorityEnum accountCompetentAuthority = accountAuthorityInfoProvider.getAccountCa(accountId);
        return regulatorCompetentAuthorityAuthorizationService.isAuthorized(user, accountCompetentAuthority, permission);
    }
}
