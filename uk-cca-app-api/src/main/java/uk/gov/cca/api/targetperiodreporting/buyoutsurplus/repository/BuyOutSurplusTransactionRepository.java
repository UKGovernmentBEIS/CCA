package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface BuyOutSurplusTransactionRepository extends JpaRepository<BuyOutSurplusTransaction, Long> {

    @Query(name = BuyOutSurplusTransaction.NAMED_QUERY_FIND_ALL_BY_ACCOUNT_ID_AND_TARGET_PERIOD_TYPE)
    List<BuyOutSurplusTransaction> findAllByAccountIdAndTargetPeriodType(Long accountId, TargetPeriodType targetPeriodType);

    List<BuyOutSurplusTransaction> findAllByIdIn(Set<Long> ids);

    Optional<BuyOutSurplusTransaction> findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(Long id, PerformanceDataResourceType  performanceDataResourceType);

    @Query(value = "SELECT tr.accountId FROM BuyOutSurplusTransaction tr WHERE tr.id = :transactionId")
    Optional<Long> findAccountIdByTransactionId(Long transactionId);

    Optional<BuyOutSurplusTransaction> findBuyOutSurplusTransactionByIdAndFileDocumentUuid(Long id, String documentUuid);

    @Query(name = BuyOutSurplusTransaction.NAMED_QUERY_FIND_TRANSACTION_DETAILS_SUMMARY_BY_ACCOUNT_AND_TARGET_PERIODS)
    List<BuyOutSurplusTransactionDetailSummary> findTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(Long accountId, List<Long> targetPeriodIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BuyOutSurplusTransaction> findBuyOutSurplusTransactionById(Long id);
}
