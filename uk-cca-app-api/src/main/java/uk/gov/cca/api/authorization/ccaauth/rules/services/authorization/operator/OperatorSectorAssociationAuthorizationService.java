package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.operator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.core.annotation.Order;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.TargetUnitAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.authorization.rules.services.authorization.operator.OperatorAccountAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.operator.OperatorResourceTypeAuthorizationService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Order(200)
public class OperatorSectorAssociationAuthorizationService implements OperatorResourceTypeAuthorizationService {

    private final OperatorAccountAuthorizationService operatorAccountAuthorizationService;
    private final TargetUnitAuthorityInfoProvider targetUnitAuthorityInfoProvider;

    @Override
    public boolean isAuthorized(AppUser user, AuthorizationCriteria criteria) {
        if (criteria.getPermission() == null) {
            long sectorId = Long.parseLong(criteria.getRequestResources().get(CcaResourceType.SECTOR_ASSOCIATION));
            List<Long> accountIdsBySector = targetUnitAuthorityInfoProvider
                    .getAllTargetUnitAccountIdsBySectorAssociationId(sectorId);

            return accountIdsBySector.stream()
                    .anyMatch(accountId -> operatorAccountAuthorizationService.isAuthorized(user, accountId));
        } else {
            return false;
        }
    }

    @Override
    public boolean isApplicable(AuthorizationCriteria criteria) {
        return criteria.getRequestResources().containsKey(CcaResourceType.SECTOR_ASSOCIATION);
    }
}