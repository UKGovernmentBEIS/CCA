package uk.gov.cca.api.sectorassociation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;

@Repository
public interface SectorAssociationContactRepository extends JpaRepository<SectorAssociationContact, Long> {

}