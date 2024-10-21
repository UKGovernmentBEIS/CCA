package uk.gov.cca.api.account.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cca.api.account.domain.TargetUnitAccountIdentifier;

import java.util.Optional;

import static uk.gov.cca.api.account.domain.TargetUnitAccountIdentifier.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_IDENTIFIER;

@Repository
public interface TargetUnitAccountIdentifierRepository extends JpaRepository<TargetUnitAccountIdentifier, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(name = NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_IDENTIFIER)
    Optional<TargetUnitAccountIdentifier> findTargetUnitAccountIdentifierBySectorAssociationId(Long sectorAssociationId);
}
