package uk.gov.cca.api.facility.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import uk.gov.cca.api.facility.domain.FacilityIdentifier;

import java.util.Optional;

import static uk.gov.cca.api.facility.domain.FacilityIdentifier.NAMED_QUERY_FIND_FACILITY_IDENTIFIER_BY_SECTOR_ASSOCIATION_ID;

public interface FacilityIdentifierRepository extends JpaRepository<FacilityIdentifier, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(name = NAMED_QUERY_FIND_FACILITY_IDENTIFIER_BY_SECTOR_ASSOCIATION_ID)
    Optional<FacilityIdentifier> findFacilityIdentifierBySectorAssociationId(Long sectorAssociationId);
}
