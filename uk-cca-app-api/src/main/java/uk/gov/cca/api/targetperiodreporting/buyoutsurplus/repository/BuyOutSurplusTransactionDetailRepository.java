package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface BuyOutSurplusTransactionDetailRepository extends JpaRepository<BuyOutSurplusTransactionDetail, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select tr from BuyOutSurplusTransactionDetail tr where tr.id = :id")
    Optional<BuyOutSurplusTransactionDetail> findByIdForUpdate(Long id);

    @Query(value = "select new uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO("
            + "t.id, fd.facilityBusinessId, fd.siteName, t.paymentStatus, cast(function('jsonb_extract_path_text', t.buyOutSurplusContainer, 'invoicedBuyOutFee') as BigDecimal), t.buyOutFee, (t.transactionHistoryList IS NOT EMPTY)) "
            + "from BuyOutSurplusTransactionDetail t "
            + "inner join PerformanceDataFacilityEntity pf on pf.id = t.performanceDataId "
            + "inner join FacilityData fd on fd.id = pf.facilityId "
            + "where t.transaction.id = :transactionId "
            + "and t.transaction.performanceDataResourceType = PerformanceDataResourceType.FACILITY "
            + "and (LOWER(fd.facilityBusinessId) like CONCAT('%',:term,'%') or LOWER(fd.siteName) like CONCAT('%',:term,'%')) "
            + "and (:paymentStatus is null or t.paymentStatus = :paymentStatus) ")
    Page<FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO> findBySearchCriteria(
            Pageable pageable, Long transactionId, String term, BuyOutSurplusPaymentStatus paymentStatus);

    List<BuyOutSurplusTransactionDetail> findAllByIdIn(Set<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<BuyOutSurplusTransactionDetail> findAllByIdInAndTransaction_Id(Set<Long> ids, Long transactionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<BuyOutSurplusTransactionDetail> findAllByPaymentStatusInAndTransaction_Id(Set<BuyOutSurplusPaymentStatus> paymentStatuses, Long transactionId);

    Optional<BuyOutSurplusTransactionDetail> findByPerformanceDataIdAndTransaction_PerformanceDataResourceType(Long performanceDataId, PerformanceDataResourceType resourceType);

    List<BuyOutSurplusTransactionDetail> findAllByPerformanceDataIdInAndTransaction_PerformanceDataResourceType(Set<Long> performanceDataIds, PerformanceDataResourceType resourceType);
}
