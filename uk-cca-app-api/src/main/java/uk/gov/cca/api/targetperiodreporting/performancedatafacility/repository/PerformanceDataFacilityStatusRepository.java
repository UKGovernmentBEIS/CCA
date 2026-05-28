package uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;

import java.time.Year;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface PerformanceDataFacilityStatusRepository extends JpaRepository<PerformanceDataFacilityStatus, Long> {

    Optional<PerformanceDataFacilityStatus> findByFacilityIdAndTargetPeriodYear(Long facilityId, Year targetPeriodYear);
}
