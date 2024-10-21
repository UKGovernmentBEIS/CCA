package uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers;

import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

public interface SectorAssociationAuthorityInfoProvider {

    CompetentAuthorityEnum getSectorAssociationCa(Long sectorAssociationId);
}
