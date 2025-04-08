package uk.gov.cca.api.buyoutsurplus.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import uk.gov.cca.api.buyoutsurplus.domain.BuyOutTransactionIdentifier;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;

import java.util.Optional;

@Repository
public interface BuyOutTransactionIdentifierRepository extends JpaRepository<BuyOutTransactionIdentifier, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BuyOutTransactionIdentifier> findByTargetPeriodType(TargetPeriodType targetPeriodType);
}
