package uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;

@Repository
@Transactional(readOnly = true)
public interface PerformanceDataFacilityRepository extends JpaRepository<PerformanceDataFacilityEntity, Long> {
}
