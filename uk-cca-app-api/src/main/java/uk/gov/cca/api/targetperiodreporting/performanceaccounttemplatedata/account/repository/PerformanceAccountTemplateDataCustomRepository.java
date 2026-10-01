package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.account.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;

import com.querydsl.core.types.dsl.BooleanExpression;
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
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.account.domain.QPerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.account.utils.PerformanceAccountTemplateUtils;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.PerformanceAccountTemplateDataStatus;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;

@Repository
public class PerformanceAccountTemplateDataCustomRepository {

	@PersistenceContext
    private EntityManager entityManager;
    
	public SectorPerformanceAccountTemplateDataReportListDTO getSectorPerformanceAccountTemplateDataReportListBySearchCriteria(
			Long sectorAssociationId, SectorPerformanceAccountTemplateDataReportSearchCriteria criteria) {
		QTargetUnitAccount targetUnitAccount = QTargetUnitAccount.targetUnitAccount;
		QPerformanceAccountTemplateDataEntity performanceAccountTemplateDataEntity = QPerformanceAccountTemplateDataEntity.performanceAccountTemplateDataEntity;
        
        BooleanBuilder whereClause = new BooleanBuilder();
        
        whereClause.and(targetUnitAccount.sectorAssociationId.eq(sectorAssociationId));
        
        if(!ObjectUtils.isEmpty(criteria.getTerm())) {
            whereClause.and(targetUnitAccount.businessId.likeIgnoreCase('%' + criteria.getTerm() + '%'));
        }
        
        final Year nextTargetPeriodYear = criteria.getTargetPeriodYear().plusYears(1);
        final LocalDateTime acceptedDate = criteria.getTargetPeriodYear().atMonth(Month.DECEMBER).atDay(31).atTime(LocalTime.MAX);
		final LocalDateTime terminatedDateFrom = nextTargetPeriodYear.atMonth(Month.JANUARY).atDay(1).atTime(LocalTime.MIN);
		final LocalDateTime terminatedDateTo = PerformanceAccountTemplateUtils.TERMINATED_END_DATE_FOR_ELIGIBLE_ACCOUNTS_MONTH_DAY
				.atYear(nextTargetPeriodYear.getValue()).atTime(LocalTime.MIN);

		BooleanExpression submitted = criteria.getStatus() != PerformanceAccountTemplateDataStatus.OUTSTANDING
				? performanceAccountTemplateDataEntity.isNotNull()
				: Expressions.FALSE;

		BooleanExpression outstanding = criteria.getStatus() != PerformanceAccountTemplateDataStatus.SUBMITTED
				? performanceAccountTemplateDataEntity.isNull()
					.and(targetUnitAccount.acceptedDate.loe(acceptedDate))
					.and(targetUnitAccount.status.eq(TargetUnitAccountStatus.LIVE)
							.or(targetUnitAccount.status.eq(TargetUnitAccountStatus.TERMINATED)
											.and(targetUnitAccount.terminatedDate.goe(terminatedDateFrom))
											.and(targetUnitAccount.terminatedDate.lt(terminatedDateTo))
							)
					)
				: Expressions.FALSE;

		whereClause.and(submitted.or(outstanding));
        
        // query
        JPAQuery<SectorPerformanceAccountTemplateDataReportItemDTO> query = new JPAQuery<>(entityManager);

        JPAQuery<SectorPerformanceAccountTemplateDataReportItemDTO> jpaQuery = query.select(Projections.constructor(SectorPerformanceAccountTemplateDataReportItemDTO.class,
                targetUnitAccount.id,
                targetUnitAccount.businessId,
                targetUnitAccount.name,
                performanceAccountTemplateDataEntity.submissionDate,
                Expressions.cases()
	                .when(performanceAccountTemplateDataEntity.isNull())
	                .then(PerformanceAccountTemplateDataStatus.OUTSTANDING.name())
	                .otherwise(PerformanceAccountTemplateDataStatus.SUBMITTED.name())
            ))
            .from(targetUnitAccount)
            .leftJoin(performanceAccountTemplateDataEntity).on(performanceAccountTemplateDataEntity.accountId.eq(targetUnitAccount.id)
						.and(performanceAccountTemplateDataEntity.targetPeriodYear.eq(criteria.getTargetPeriodYear())))
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
