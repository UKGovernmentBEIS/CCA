package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface BuyOutSurplusProcessedDataRepository extends JpaRepository<BuyOutSurplusProcessedData, Long> {

    Optional<BuyOutSurplusProcessedData> findByPerformanceDataId(Long performanceDataId);
}
