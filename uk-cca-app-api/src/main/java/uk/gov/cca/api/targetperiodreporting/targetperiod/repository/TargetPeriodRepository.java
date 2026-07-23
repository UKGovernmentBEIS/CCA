package uk.gov.cca.api.targetperiodreporting.targetperiod.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Repository
@Transactional(readOnly = true)
public interface TargetPeriodRepository extends JpaRepository<TargetPeriod, Long> {

  Optional<TargetPeriod> findByBusinessId(TargetPeriodType businessId);
  
  List<TargetPeriod> findAllBySchemeVersion(SchemeVersion schemeVersion);

  List<TargetPeriod> findByBusinessIdIn(Set<TargetPeriodType> businessIds);
  
  List<TargetPeriod> findByBuyOutStartDateLessThanEqualOrderByBuyOutStartDateDesc(LocalDate date);
}

