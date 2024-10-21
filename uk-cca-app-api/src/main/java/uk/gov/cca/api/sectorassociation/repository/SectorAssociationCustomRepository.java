package uk.gov.cca.api.sectorassociation.repository;

import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;

import java.util.Optional;

public interface SectorAssociationCustomRepository {

    Optional<SectorAssociation> findByIdForUpdate(Long id);
}
