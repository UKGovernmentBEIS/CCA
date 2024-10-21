package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityUpdateDTO;

import java.util.List;

public interface SectorUserAuthorityUpdateValidator {
	
    void validateUpdate(List<SectorUserAuthorityUpdateDTO> sectorUserAuthorityUpdateDTOS, Long sectorAssociationId);
}
