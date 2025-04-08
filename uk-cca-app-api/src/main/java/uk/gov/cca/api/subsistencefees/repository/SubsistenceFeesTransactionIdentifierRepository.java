package uk.gov.cca.api.subsistencefees.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesTransactionIdentifier;

import java.util.Optional;

@Repository
public interface SubsistenceFeesTransactionIdentifierRepository extends JpaRepository<SubsistenceFeesTransactionIdentifier, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SubsistenceFeesTransactionIdentifier> findByMoaType(MoaType moaType);
}
