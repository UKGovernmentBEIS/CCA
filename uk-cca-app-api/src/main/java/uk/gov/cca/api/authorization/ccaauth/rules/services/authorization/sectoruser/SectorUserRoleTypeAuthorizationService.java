package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.sectoruser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.domain.CcaRoleTypeConstants;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.authorization.rules.services.authorization.RoleTypeAuthorizationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorUserRoleTypeAuthorizationService implements RoleTypeAuthorizationService {
    private final List<SectorUserResourceTypeAuthorizationService> sectorUserResourceTypeAuthorizationServices;
    
    @Override
    public boolean isAuthorized(AppUser user, AuthorizationCriteria criteria) {
        return sectorUserResourceTypeAuthorizationServices.stream()
                .filter(resourceTypeAuthorizationService -> resourceTypeAuthorizationService.isApplicable(criteria))
                .findFirst()
                .map(resourceTypeAuthorizationService -> resourceTypeAuthorizationService.isAuthorized(user, criteria))
                .orElse(false);
    }

    @Override
    public String getRoleType() {
        return CcaRoleTypeConstants.SECTOR_USER;
    }
}
