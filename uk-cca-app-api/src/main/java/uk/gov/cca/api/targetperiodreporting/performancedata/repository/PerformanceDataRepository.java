package uk.gov.cca.api.targetperiodreporting.performancedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;

@Repository
@Transactional(readOnly = true)
public interface PerformanceDataRepository extends JpaRepository<PerformanceDataEntity, Long> {

}
