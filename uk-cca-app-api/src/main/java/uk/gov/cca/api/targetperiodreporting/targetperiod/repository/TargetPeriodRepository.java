package uk.gov.cca.api.targetperiodreporting.targetperiod.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Repository
public interface TargetPeriodRepository extends JpaRepository<TargetPeriod, Long> {

  @Transactional(readOnly = true)
  Optional<TargetPeriod> findByBusinessId(TargetPeriodType businessId);
}

