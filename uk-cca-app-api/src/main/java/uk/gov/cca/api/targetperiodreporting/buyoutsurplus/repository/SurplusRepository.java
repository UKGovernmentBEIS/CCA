package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface SurplusRepository extends JpaRepository<SurplusEntity, Long> {

	@Query(value = "SELECT new uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO(se.targetPeriod.businessId, se.surplusGained, " +
			"(se.surplusHistoryList IS NOT EMPTY) ) " +
			"FROM SurplusEntity se " +
			"WHERE se.accountId = :accountId")
	List<SurplusGainedDTO> getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(Long accountId);

	Optional<SurplusEntity> findByAccountIdAndTargetPeriod_BusinessId(Long accountId, TargetPeriodType targetPeriodType);
}
