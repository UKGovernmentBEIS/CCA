package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionIdentifier;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface BuyOutSurplusTransactionIdentifierRepository extends JpaRepository<BuyOutSurplusTransactionIdentifier, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BuyOutSurplusTransactionIdentifier> findByTargetPeriodType(TargetPeriodType targetPeriodType);
}
