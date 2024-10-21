package uk.gov.cca.api.authorization.ccaauth.rules.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SectorAssociationAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.authorization.RegulatorCompAuthAuthorizationService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@Service
@RequiredArgsConstructor
public class RegulatorSectorAssociationAuthService implements UserRoleTypeSectorAssociationAuthService {

    private final SectorAssociationAuthorityInfoProvider sectorAssociationAuthorityInfoProvider;
    private final RegulatorCompAuthAuthorizationService regulatorCompAuthAuthorizationService;

    @Override
    public boolean isAuthorized(AppUser user, Long sectorAssociationId) {
        CompetentAuthorityEnum accountCompetentAuthority = sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorAssociationId);
        return regulatorCompAuthAuthorizationService.isAuthorized(user, accountCompetentAuthority);
    }

    @Override
    public boolean isAuthorized(AppUser user, Long sectorAssociationId, String permission) {
        CompetentAuthorityEnum accountCompetentAuthority = sectorAssociationAuthorityInfoProvider.getSectorAssociationCa(sectorAssociationId);
        return regulatorCompAuthAuthorizationService.isAuthorized(user, accountCompetentAuthority, permission);
    }

    @Override
    public String getRoleType() {
        return REGULATOR;
    }
}
