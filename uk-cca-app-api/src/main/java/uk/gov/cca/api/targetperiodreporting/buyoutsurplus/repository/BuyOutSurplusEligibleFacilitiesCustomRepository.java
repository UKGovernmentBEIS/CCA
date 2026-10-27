package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;

@Repository
public class BuyOutSurplusEligibleFacilitiesCustomRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public List<FacilityPerformanceDataWithBuyOutProcessedStatus> findFacilitiesPerformanceDataWithBuyOutProcessedStatus(Long accountId, List<Long> targetPeriodIds) {
		TypedQuery<FacilityPerformanceDataWithBuyOutProcessedStatus> query = entityManager.createQuery(
				"SELECT new uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus(fd.id, fd.facilityBusinessId, fd.siteName, pdfs.lastPerformanceData,  pdfs.locked, (bos.id IS NOT NULL)) " +
				"FROM FacilityData fd " +
				"JOIN PerformanceDataFacilityStatus pdfs ON pdfs.facilityId = fd.id " +
				"LEFT JOIN BuyOutSurplusProcessedData bos ON bos.performanceDataId = pdfs.lastPerformanceData.id " +
		        "AND bos.performanceDataResourceType = uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType.FACILITY " +
				"WHERE fd.accountId = :accountId " +
				"AND pdfs.targetPeriod.id in :targetPeriodIds " +
				"AND pdfs.lastPerformanceData.submissionType IS NOT NULL ", FacilityPerformanceDataWithBuyOutProcessedStatus.class );
		query.setParameter("accountId", accountId);
		query.setParameter("targetPeriodIds", targetPeriodIds);
		return query.getResultList();
	}
}
