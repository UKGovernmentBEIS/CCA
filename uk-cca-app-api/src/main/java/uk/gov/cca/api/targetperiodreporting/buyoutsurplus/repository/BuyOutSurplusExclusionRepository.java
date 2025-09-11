package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusExclusion;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface BuyOutSurplusExclusionRepository extends JpaRepository<BuyOutSurplusExclusion, Long> {

	boolean existsByAccountId(Long accountId);

	void deleteByAccountId(Long accountId);

	@Query(value = "select bose.accountId from BuyOutSurplusExclusion bose")
	List<Long> findAllAccountIds();
}
