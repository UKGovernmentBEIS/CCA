package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.util.List;
import java.util.Set;

@Repository
public class BuyOutSurplusEligibleAccountsCustomRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public List<TargetUnitAccountBusinessInfoDTO> findAccountsWithPerformanceDataPendingBuyOut(TargetPeriodType targetPeriodType) {
		TypedQuery<TargetUnitAccountBusinessInfoDTO> query = entityManager.createQuery(
				"SELECT new uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO(tu.id, tu.businessId, tu.name) " +
				"FROM TargetUnitAccount tu " +
				"JOIN AccountPerformanceDataStatus apds ON apds.accountId = tu.id " +
				"WHERE NOT EXISTS ( select 1 from BuyOutSurplusProcessedData bos where bos.performanceDataId = apds.lastPerformanceData.id and bos.performanceDataResourceType = uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType.ACCOUNT ) " +
				"AND apds.targetPeriod.businessId = :targetPeriodType ", TargetUnitAccountBusinessInfoDTO.class );
		query.setParameter("targetPeriodType", targetPeriodType);
		return query.getResultList();
	}

	public List<TargetUnitAccountBusinessInfoDTO> findAccountsWithFacilityPerformanceDataPendingBuyOut(Set<TargetPeriodType> targetPeriods) {
		TypedQuery<TargetUnitAccountBusinessInfoDTO> query = entityManager.createQuery(
				"SELECT DISTINCT new uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO(tu.id, tu.businessId, tu.name) " +
				"FROM TargetUnitAccount tu " +
				"JOIN FacilityData fd ON fd.accountId = tu.id " +
				"JOIN PerformanceDataFacilityStatus pdfs ON pdfs.facilityId = fd.id " +
				"WHERE pdfs.lastPerformanceData.submissionType IS NOT NULL " +
				"AND NOT EXISTS ( select 1 from BuyOutSurplusProcessedData bos " +
					"WHERE bos.performanceDataId = pdfs.lastPerformanceData.id " +
					"AND bos.performanceDataResourceType = uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType.FACILITY ) " +
				"AND pdfs.targetPeriod.businessId in (:targetPeriods) ", TargetUnitAccountBusinessInfoDTO.class );
		query.setParameter("targetPeriods", targetPeriods);
		return query.getResultList();
	}
}
