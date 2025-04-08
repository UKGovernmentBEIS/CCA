package uk.gov.cca.api.authorization.ccaauth.rules.services.authorization.regulator;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SectorAssociationAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.authorization.rules.services.authorization.regulator.RegulatorCompetentAuthorityAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.regulator.RegulatorResourceTypeAuthorizationService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Service
@Order(300)
@RequiredArgsConstructor
public class RegulatorSectorAssociationAuthorizationService implements RegulatorResourceTypeAuthorizationService {

    private final SectorAssociationAuthorityInfoProvider sectorAssociationAuthorityInfoProvider;
    private final RegulatorCompetentAuthorityAuthorizationService regulatorCompetentAuthorityAuthorizationService;

    @Override
    public boolean isAuthorized(AppUser user, AuthorizationCriteria criteria) {
        long sectorId = Long.parseLong(criteria.getRequestResources().get(CcaResourceType.SECTOR_ASSOCIATION));
        if (criteria.getPermission() == null) {
            return isAuthorized(user, sectorId);
        } else {
            return isAuthorized(user, sectorId, criteria.getPermission());
        }
    }

    @Override
    public boolean isApplicable(AuthorizationCriteria criteria) {
        return criteria.getRequestResources().containsKey(CcaResourceType.SECTOR_ASSOCIATION);
    }

    public boolean isAuthorized(AppUser user, Long sectorAssociationId) {
        CompetentAuthorityEnum accountCompetentAuthority = sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorAssociationId);
        return regulatorCompetentAuthorityAuthorizationService.isAuthorized(user, accountCompetentAuthority);
    }

    public boolean isAuthorized(AppUser user, Long sectorAssociationId, String permission) {
        CompetentAuthorityEnum accountCompetentAuthority = sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorAssociationId);
        return regulatorCompetentAuthorityAuthorizationService.isAuthorized(user, accountCompetentAuthority, permission);
    }
}
