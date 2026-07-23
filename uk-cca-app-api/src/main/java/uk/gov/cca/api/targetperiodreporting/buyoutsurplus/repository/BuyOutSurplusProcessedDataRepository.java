package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface BuyOutSurplusProcessedDataRepository extends JpaRepository<BuyOutSurplusProcessedData, Long> {

    Optional<BuyOutSurplusProcessedData> findByPerformanceDataIdAndPerformanceDataResourceType(
    		Long performanceDataId, PerformanceDataResourceType resourceType);
}
