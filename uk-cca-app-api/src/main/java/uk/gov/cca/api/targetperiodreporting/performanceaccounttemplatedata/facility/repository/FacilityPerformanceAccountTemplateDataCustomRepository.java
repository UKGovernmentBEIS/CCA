package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;

import uk.gov.cca.api.account.domain.QTargetUnitAccount;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.QFacilityData;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.PerformanceAccountTemplateDataStatus;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.QFacilityPerformanceAccountTemplateDataEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public class FacilityPerformanceAccountTemplateDataCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public SectorPerformanceAccountTemplateDataReportListDTO getSectorFacilityPerformanceDataReportListBySearchCriteria(
            Long sectorAssociationId, SectorPerformanceAccountTemplateDataReportSearchCriteria criteria) {
        QTargetUnitAccount targetUnitAccount = QTargetUnitAccount.targetUnitAccount;
        QFacilityData facilityData = QFacilityData.facilityData;
        QFacilityPerformanceAccountTemplateDataEntity facilityPerformanceAccountTemplateData = QFacilityPerformanceAccountTemplateDataEntity
                .facilityPerformanceAccountTemplateDataEntity;
        final LocalDateTime reportingPeriodStartDate = LocalDate.of(criteria.getTargetPeriodYear().getValue() + 1, 1, 1)
                .atStartOfDay();

        BooleanBuilder whereClause = new BooleanBuilder();

        whereClause.and(targetUnitAccount.sectorAssociationId.eq(sectorAssociationId))
                .and(Expressions.booleanTemplate("function('jsonb_exists', {0}, {1}) = true",
                        facilityData.participatingSchemeVersions, SchemeVersion.CCA_3.name()));

        if(!ObjectUtils.isEmpty(criteria.getTerm())) {
            String termLike = "%" + criteria.getTerm() + "%";
            whereClause.and((targetUnitAccount.businessId.likeIgnoreCase(termLike))
                    .or(facilityData.facilityBusinessId.likeIgnoreCase(termLike)));
        }

        BooleanExpression submitted = criteria.getStatus() != PerformanceAccountTemplateDataStatus.OUTSTANDING
                ? facilityPerformanceAccountTemplateData.id.isNotNull()
                : Expressions.FALSE;

        BooleanExpression outstanding = criteria.getStatus() != PerformanceAccountTemplateDataStatus.SUBMITTED
                ? facilityPerformanceAccountTemplateData.id.isNull()
                    .and(facilityData.createdDate.lt(reportingPeriodStartDate))
                    .and(facilityData.closedDate.isNull().or(facilityData.closedDate.gt(reportingPeriodStartDate)))
                : Expressions.FALSE;

        whereClause.and(submitted.or(outstanding));

        // Query
        JPAQuery<SectorPerformanceAccountTemplateDataReportItemDTO> query = new JPAQuery<>(entityManager);

        JPAQuery<SectorPerformanceAccountTemplateDataReportItemDTO> jpaQuery = query.select(Projections.constructor(SectorPerformanceAccountTemplateDataReportItemDTO.class,
                        facilityData.id,
                        targetUnitAccount.id,
                        facilityData.facilityBusinessId,
                        facilityData.siteName,
                        facilityPerformanceAccountTemplateData.submissionDate,
                        Expressions.cases().when(facilityPerformanceAccountTemplateData.isNull())
                                .then(PerformanceAccountTemplateDataStatus.OUTSTANDING.name())
                                .otherwise(PerformanceAccountTemplateDataStatus.SUBMITTED.name())
                ))
                .from(facilityData)
                .innerJoin(targetUnitAccount).on(targetUnitAccount.id.eq(facilityData.accountId))
                .leftJoin(facilityPerformanceAccountTemplateData).on(facilityPerformanceAccountTemplateData.facilityId.eq(facilityData.id)
                        .and(facilityPerformanceAccountTemplateData.targetPeriodYear.eq(criteria.getTargetPeriodYear())))
                .where(whereClause)
                .orderBy(facilityData.facilityBusinessId.asc())
                .offset((long)criteria.getPaging().getPageNumber() * criteria.getPaging().getPageSize())
                .limit(criteria.getPaging().getPageSize());

        return SectorPerformanceAccountTemplateDataReportListDTO.builder()
                .items(jpaQuery.fetch())
                .total(jpaQuery.fetchCount())
                .build();
    }
}
