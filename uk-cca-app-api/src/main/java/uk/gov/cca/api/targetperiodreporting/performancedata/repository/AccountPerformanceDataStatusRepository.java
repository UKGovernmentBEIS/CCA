package uk.gov.cca.api.targetperiodreporting.performancedata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;

@Repository
@Transactional(readOnly = true)
public interface AccountPerformanceDataStatusRepository extends JpaRepository<AccountPerformanceDataStatus, Long> {

	@Query(name = AccountPerformanceDataStatus.NAMED_QUERY_FIND_ELIGIBLE_ACCOUNTS_FOR_PRIMARY_PERFORMANCE_DATA_REPORTING_BY_SECTOR)
	List<TargetUnitAccountBusinessInfoDTO> findEligibleAccountsForPrimaryPerformanceDataReportingBySector(Long sectorAssociationId,
																										  Long targetPeriodId);

	@Query(name = AccountPerformanceDataStatus.NAMED_QUERY_FIND_ELIGIBLE_ACCOUNTS_FOR_SECONDARY_PERFORMANCE_DATA_REPORTING_BY_SECTOR)
	List<TargetUnitAccountBusinessInfoDTO> findEligibleAccountsForSecondaryPerformanceDataReportingBySector(Long sectorAssociationId,
																											Long targetPeriodId);

	@EntityGraph(attributePaths = { "targetPeriod", "lastPerformanceData" })
	Optional<AccountPerformanceDataStatus> findWithDetailsByAccountIdAndTargetPeriodBusinessId(Long accountId,
			TargetPeriodType targetPeriodType);

	Optional<AccountPerformanceDataStatus> findByAccountIdAndTargetPeriodBusinessId(Long accountId,
			TargetPeriodType targetPeriodType);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT tapds FROM AccountPerformanceDataStatus tapds join fetch tapds.lastPerformanceData WHERE tapds.accountId = :accountId and tapds.targetPeriod.businessId = :targetPeriodType")
	Optional<AccountPerformanceDataStatus> findByAccountIdAndTargetPeriodBusinessIdForUpdate(Long accountId,
			TargetPeriodType targetPeriodType);

}
