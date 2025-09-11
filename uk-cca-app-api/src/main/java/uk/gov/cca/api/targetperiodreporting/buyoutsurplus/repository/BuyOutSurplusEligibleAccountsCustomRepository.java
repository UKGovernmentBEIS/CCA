package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.util.List;

@Repository
public class BuyOutSurplusEligibleAccountsCustomRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public List<TargetUnitAccountBusinessInfoDTO> findAccountsWithPerformanceDataPendingBuyOut(TargetPeriodType targetPeriodType) {
		TypedQuery<TargetUnitAccountBusinessInfoDTO> query = entityManager.createQuery(
				"SELECT new uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO(tu.id, tu.businessId, tu.name) " +
				"FROM TargetUnitAccount tu " +
				"JOIN AccountPerformanceDataStatus apds ON apds.accountId = tu.id " +
				"WHERE NOT EXISTS ( select 1 from BuyOutSurplusProcessedData bos where bos.performanceDataId = apds.lastPerformanceData.id ) " +
				"AND apds.targetPeriod.businessId = :targetPeriodType ", TargetUnitAccountBusinessInfoDTO.class );
		query.setParameter("targetPeriodType", targetPeriodType);

		return query.getResultList();
	}
}
