package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityBuyOutSurplusProcessedDataInfo;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Repository
@Transactional(readOnly = true)
public interface BuyOutSurplusProcessedDataRepository extends JpaRepository<BuyOutSurplusProcessedData, Long> {

    Optional<BuyOutSurplusProcessedData> findByPerformanceDataIdAndPerformanceDataResourceType(
    		Long performanceDataId, PerformanceDataResourceType resourceType);

    @Query(name = BuyOutSurplusProcessedData.NAMED_QUERY_FIND_LATEST_BUY_OUT_SURPLUS_PROCESSED_FACILITIES_PRIMARY_PERFORMANCE_DATA_BY_FACILITY_ID_IN_AND_TARGET_PERIOD)
    List<FacilityBuyOutSurplusProcessedDataInfo> findLatestBuyOutSurplusProcessedFacilitiesPrimaryPerformanceDataByTargetPeriodType(Set<Long> facilityIds, TargetPeriodType targetPeriodType);
}
