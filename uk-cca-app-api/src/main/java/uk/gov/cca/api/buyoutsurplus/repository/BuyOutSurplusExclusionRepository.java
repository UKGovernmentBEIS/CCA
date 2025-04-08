package uk.gov.cca.api.buyoutsurplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.buyoutsurplus.domain.BuyOutSurplusExclusion;

@Transactional(readOnly = true)
public interface BuyOutSurplusExclusionRepository extends JpaRepository<BuyOutSurplusExclusion, Long> {

	boolean existsByAccountId(Long accountId);

}
