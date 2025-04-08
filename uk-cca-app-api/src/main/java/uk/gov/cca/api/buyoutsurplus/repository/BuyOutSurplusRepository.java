package uk.gov.cca.api.buyoutsurplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.buyoutsurplus.domain.BuyOutPaymentStatus;
import uk.gov.cca.api.buyoutsurplus.domain.BuyOutSurplusEntity;

import java.util.List;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface BuyOutSurplusRepository extends JpaRepository<BuyOutSurplusEntity, Long> {

    @Query(name = BuyOutSurplusEntity.NAMED_QUERY_FIND_ALL_BY_ACCOUNT_ID_AND_PAYMENT_STATUS_IN)
    List<BuyOutSurplusEntity> findAllByAccountIdAndPaymentStatusIn(Long accountId, Set<BuyOutPaymentStatus> paymentStatuses);
}
