package uk.gov.cca.api.targetperiodreporting.performancedata.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import uk.gov.cca.api.account.domain.QTargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataInfo;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.QAccountPerformanceDataStatus;

import java.util.List;
import java.util.Set;

@Repository
public class AccountPerformanceDataStatusCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<AccountPerformanceDataInfo> findAccountsWithPerformanceDataByTargetPeriod(TargetPeriodType targetPeriodType) {
        JPAQuery<AccountPerformanceDataInfo> query = findAccountsWithPerformanceDataForTargetPeriodByQuery(targetPeriodType);

        return query.fetch();
    }

    public List<AccountPerformanceDataInfo> findAccountsWithPerformanceDataByTargetPeriodAndAccountIdIn(TargetPeriodType targetPeriodType,
                                                                                                        Set<Long> accountIds) {
        QAccountPerformanceDataStatus performanceDataStatus = QAccountPerformanceDataStatus.accountPerformanceDataStatus;

        JPAQuery<AccountPerformanceDataInfo> query = findAccountsWithPerformanceDataForTargetPeriodByQuery(targetPeriodType);

        return query.where(performanceDataStatus.accountId.in(accountIds)).fetch();
    }

    private JPAQuery<AccountPerformanceDataInfo> findAccountsWithPerformanceDataForTargetPeriodByQuery(TargetPeriodType targetPeriodType) {
        QTargetUnitAccount targetUnitAccount = QTargetUnitAccount.targetUnitAccount;
        QAccountPerformanceDataStatus performanceDataStatus = QAccountPerformanceDataStatus.accountPerformanceDataStatus;

        return new JPAQuery<>(entityManager)
                .select(Projections.constructor(
                        AccountPerformanceDataInfo.class,
                        targetUnitAccount.id,
                        targetUnitAccount.businessId,
                        performanceDataStatus.lastPerformanceData.id))
                .from(performanceDataStatus)
                .join(targetUnitAccount).on(targetUnitAccount.id.eq(performanceDataStatus.accountId))
                .where(performanceDataStatus.targetPeriod.businessId.eq(targetPeriodType)
                        .and(targetUnitAccount.status.eq(TargetUnitAccountStatus.LIVE)));
    }
}
