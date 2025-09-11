package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.repository;

import java.time.Year;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataEntity;

@Repository
@Transactional(readOnly = true)
public interface PerformanceAccountTemplateDataRepository
		extends JpaRepository<PerformanceAccountTemplateDataEntity, Long> {

	/**
	 * Returns the report version for the given params, or 0 if no entry found
	 * @param accountId
	 * @param targetPeriodYear
	 * @return the report version for the given params, or 0 if no entry found
	 */
	@Query(name = PerformanceAccountTemplateDataEntity.NAMED_QUERY_FIND_REPORT_VERSION_BY_ACCOUNT_ID_AND_TARGET_PERIOD_YEAR)
	int findReportVersionByAccountIdAndTargetPeriodYear(Long accountId, Year targetPeriodYear);
	
	Optional<PerformanceAccountTemplateDataEntity> findByTargetPeriodYearAndAccountId(Year targetPeriodYear, Long accountId);

        @EntityGraph(attributePaths = { "targetPeriod"})
	Optional<PerformanceAccountTemplateDataEntity> findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(Long accountId,
			TargetPeriodType targetPeriodType);
}
