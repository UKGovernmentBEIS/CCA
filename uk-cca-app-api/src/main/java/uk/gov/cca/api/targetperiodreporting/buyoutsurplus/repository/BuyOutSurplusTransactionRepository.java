package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface BuyOutSurplusTransactionRepository extends JpaRepository<BuyOutSurplusTransaction, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({ @QueryHint(name = "javax.persistence.query.timeout", value = "5000") })
    @Query(name = BuyOutSurplusTransaction.NAMED_QUERY_FIND_ALL_BY_ACCOUNT_ID_AND_TARGET_PERIOD_TYPE)
    List<BuyOutSurplusTransaction> findAllByAccountIdAndTargetPeriodTypePessimistic(Long accountId, TargetPeriodType targetPeriodType);

    List<BuyOutSurplusTransaction> findAllByIdIn(Set<Long> ids);

    Optional<BuyOutSurplusTransaction> findBuyOutSurplusTransactionById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT bost FROM BuyOutSurplusTransaction bost where bost.id =:id")
    Optional<BuyOutSurplusTransaction> findByIdWithLock(Long id);

    @Query(value = "SELECT pd.accountId FROM BuyOutSurplusTransaction bost JOIN PerformanceDataEntity pd on bost.performanceDataId = pd.id WHERE bost.id = :transactionId")
    Optional<Long> findAccountIdByTransactionId(Long transactionId);

    Optional<BuyOutSurplusTransaction> findBuyOutSurplusTransactionByIdAndFileDocumentUuid(Long id, String documentUuid);

    Optional<BuyOutSurplusTransaction> findByPerformanceDataId(Long performanceDataId);
}
