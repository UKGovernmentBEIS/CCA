package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.gov.cca.api.account.domain.QTargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.QTargetPeriod;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataStatus;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.QPerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.utils.PerformanceAccountTemplateUtils;

@Repository
public class PerformanceAccountTemplateDataCustomRepository {

	@PersistenceContext
    private EntityManager entityManager;
    
	public SectorPerformanceAccountTemplateDataReportListDTO getSectorPerformanceAccountTemplateDataReportListBySearchCriteria(
			Long sectorAssociationId, SectorPerformanceAccountTemplateDataReportSearchCriteria criteria,
			Year targetPeriodYear) {
		QTargetUnitAccount targetUnitAccount = QTargetUnitAccount.targetUnitAccount;
		QTargetPeriod targetPeriod = QTargetPeriod.targetPeriod;
		QPerformanceAccountTemplateDataEntity performanceAccountTemplateDataEntity = QPerformanceAccountTemplateDataEntity.performanceAccountTemplateDataEntity;
        
        BooleanBuilder whereClause = new BooleanBuilder();
        
        whereClause.and(targetUnitAccount.sectorAssociationId.eq(sectorAssociationId));

        whereClause.and(targetPeriod.businessId.eq(criteria.getTargetPeriodType()).or(targetPeriod.isNull()));
        
        if(!ObjectUtils.isEmpty(criteria.getTargetUnitAccountBusinessId())) {
            whereClause.and(targetUnitAccount.businessId.likeIgnoreCase('%' + criteria.getTargetUnitAccountBusinessId() + '%'));
        }
        
        final Year nextTargetPeriodYear = targetPeriodYear.plusYears(1);
        final LocalDateTime acceptedDate = targetPeriodYear.atMonth(Month.DECEMBER).atDay(31).atTime(LocalTime.MAX);
		final LocalDateTime terminatedDateFrom = nextTargetPeriodYear.atMonth(Month.JANUARY).atDay(1).atTime(LocalTime.MIN);
		final LocalDateTime terminatedDateTo = PerformanceAccountTemplateUtils.TERMINATED_END_DATE_FOR_ELIGIBLE_ACCOUNTS_MONTH_DAY
				.atYear(nextTargetPeriodYear.getValue()).atTime(LocalTime.MIN);
		
        whereClause
        	.and(targetUnitAccount.acceptedDate.loe(acceptedDate))
        	.and(
                targetUnitAccount.status.eq(TargetUnitAccountStatus.LIVE)
	            .or(
	                targetUnitAccount.status.eq(TargetUnitAccountStatus.TERMINATED)
	                    .and(targetUnitAccount.terminatedDate.goe(terminatedDateFrom))
	                    .and(targetUnitAccount.terminatedDate.lt(terminatedDateTo))
	            )
        );
        
        if (criteria.getStatus() != null) {
            whereClause.and(
					criteria.getStatus().equals(PerformanceAccountTemplateDataStatus.OUTSTANDING)
							? performanceAccountTemplateDataEntity.isNull()
							: performanceAccountTemplateDataEntity.isNotNull());
        }
        
		if (criteria.getSubmissionType() != null) {
			whereClause.and(performanceAccountTemplateDataEntity.submissionType.eq(criteria.getSubmissionType()));
		}
        
        // query
        JPAQuery<SectorPerformanceAccountTemplateDataReportItemDTO> query = new JPAQuery<>(entityManager);

        JPAQuery<SectorPerformanceAccountTemplateDataReportItemDTO> jpaQuery = query.select(Projections.constructor(SectorPerformanceAccountTemplateDataReportItemDTO.class,
                targetUnitAccount.id,
                targetUnitAccount.businessId,
                targetUnitAccount.name,
                performanceAccountTemplateDataEntity.submissionDate,
                Expressions.constant(criteria.getTargetPeriodType()),
                Expressions.constant(targetPeriodYear),
                Expressions.cases()
	                .when(performanceAccountTemplateDataEntity.isNull())
	                .then(PerformanceAccountTemplateDataStatus.OUTSTANDING.name())
	                .otherwise(PerformanceAccountTemplateDataStatus.SUBMITTED.name()),
	            performanceAccountTemplateDataEntity.submissionType
            ))
            .from(targetUnitAccount)
            .leftJoin(performanceAccountTemplateDataEntity).on(performanceAccountTemplateDataEntity.accountId.eq(targetUnitAccount.id))
            .leftJoin(targetPeriod).on(targetPeriod.id.eq(performanceAccountTemplateDataEntity.targetPeriod.id))
            .where(whereClause)
            .orderBy(targetUnitAccount.businessId.asc())
            .offset((long)criteria.getPaging().getPageNumber() * criteria.getPaging().getPageSize())
            .limit(criteria.getPaging().getPageSize());
        
        return SectorPerformanceAccountTemplateDataReportListDTO.builder()
                .items(jpaQuery.fetch())
                .total(jpaQuery.fetchCount())
                .build();

    }
}
