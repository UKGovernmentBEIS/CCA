package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;

@Repository
@Transactional(readOnly = true)
public interface BuyOutSurplusCalculationRepository extends JpaRepository<BuyOutSurplusCalculation, Long> {

    List<BuyOutSurplusCalculation> findAllByAccountIdAndTargetPeriodIdIn(Long accountId, List<Long> targetPeriodIds);
}