package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import uk.gov.netz.api.authorization.core.domain.Authority;

public interface SectorAuthorityDeleteValidator {
	
    void validateDeletion(Authority authority);

}
