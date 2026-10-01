package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface BuyOutSurplusCalculationRepository extends JpaRepository<BuyOutSurplusCalculation, Long> {

    List<BuyOutSurplusCalculation> findAllByAccountId(Long accountId);
}
