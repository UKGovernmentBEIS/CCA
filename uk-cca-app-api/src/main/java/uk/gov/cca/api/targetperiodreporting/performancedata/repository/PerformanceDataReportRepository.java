package uk.gov.cca.api.targetperiodreporting.performancedata.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;
import uk.gov.cca.api.account.domain.QTargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.targetperiod.domain.QTargetPeriod;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.QAccountPerformanceDataStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.QPerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountsPerformanceReportDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountsPerformanceReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountsPerformanceReportSearchCriteria;

import java.time.LocalDate;

@Repository
public class PerformanceDataReportRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public SectorAccountsPerformanceReportDTO getSectorAccountsPerformanceReportBySearchCriteria(Long sectorAssociationId, SectorAccountsPerformanceReportSearchCriteria criteria) {
        QTargetUnitAccount targetUnitAccount = QTargetUnitAccount.targetUnitAccount;
        QTargetPeriod targetPeriod = QTargetPeriod.targetPeriod;
        QAccountPerformanceDataStatus accountPerformanceDataStatus = QAccountPerformanceDataStatus.accountPerformanceDataStatus;
        QPerformanceDataEntity performanceData = QPerformanceDataEntity.performanceDataEntity;
        
        BooleanBuilder whereClause = new BooleanBuilder();
        
        whereClause.and(targetUnitAccount.sectorAssociationId.eq(sectorAssociationId));
        whereClause.and( (
                targetUnitAccount.status.eq(TargetUnitAccountStatus.LIVE)
                    .and(targetPeriod.endDate.goe(Expressions.dateTemplate(LocalDate.class, "CAST({0} AS DATE)", targetUnitAccount.acceptedDate)))
            ).or(
                targetUnitAccount.status.eq(TargetUnitAccountStatus.TERMINATED)
                    .and(targetPeriod.endDate.goe(Expressions.dateTemplate(LocalDate.class, "CAST({0} AS DATE)", targetUnitAccount.acceptedDate)))
                    .and(targetPeriod.performanceDataStartDate.loe(Expressions.dateTemplate(LocalDate.class, "CAST({0} AS DATE)",targetUnitAccount.terminatedDate)))
                    .and(targetPeriod.buyOutStartDate.gt(Expressions.dateTemplate(LocalDate.class, "CAST({0} AS DATE)",targetUnitAccount.terminatedDate)))
            )
        );
        
        // optional filters
        if(!ObjectUtils.isEmpty(criteria.getTargetUnitBusinessId())) {
            whereClause.and(targetUnitAccount.businessId.likeIgnoreCase('%' + criteria.getTargetUnitBusinessId() + '%'));
        }
        
        if(criteria.getSubmissionType() != null) {
            whereClause.and(performanceData.submissionType.eq(criteria.getSubmissionType()));
        }
        
        if (criteria.getPerformanceOutcome() != null) {
            whereClause.and(
                    criteria.getPerformanceOutcome().equals(TargetPeriodResultType.OUTSTANDING) ? accountPerformanceDataStatus.accountId.isNull()
                            : performanceData.performanceOutcome.eq(criteria.getPerformanceOutcome()));

        }
        
        // query
        JPAQuery<SectorAccountsPerformanceReportItemDTO> query = new JPAQuery<>(entityManager);

        JPAQuery<SectorAccountsPerformanceReportItemDTO> jpaQuery = query.select(Projections.constructor(SectorAccountsPerformanceReportItemDTO.class,
                targetUnitAccount.id,
                targetUnitAccount.businessId,
                targetUnitAccount.name,
                performanceData.submissionDate,
                performanceData.reportVersion.coalesce(0),
                Expressions.cases()
                .when(performanceData.performanceOutcome.isNull())
                .then(TargetPeriodResultType.OUTSTANDING)
                .otherwise(performanceData.performanceOutcome),
                performanceData.submissionType,
                accountPerformanceDataStatus.locked
            ))
            .from(targetUnitAccount)
            .innerJoin(targetPeriod).on(targetPeriod.businessId.eq(criteria.getTargetPeriodType()))
            .leftJoin(accountPerformanceDataStatus).on(accountPerformanceDataStatus.accountId.eq(targetUnitAccount.id).and(accountPerformanceDataStatus.targetPeriod.eq(targetPeriod)))
            .leftJoin(accountPerformanceDataStatus.lastPerformanceData, performanceData)
            .where(whereClause)
            .orderBy(targetUnitAccount.businessId.asc())
            .offset(criteria.getPaging().getPageNumber() * criteria.getPaging().getPageSize())
            .limit(criteria.getPaging().getPageSize());
        
        return SectorAccountsPerformanceReportDTO.builder()
                .performanceReportItems(jpaQuery.fetch())
                .total(jpaQuery.fetchCount())
                .build();

    }

}
