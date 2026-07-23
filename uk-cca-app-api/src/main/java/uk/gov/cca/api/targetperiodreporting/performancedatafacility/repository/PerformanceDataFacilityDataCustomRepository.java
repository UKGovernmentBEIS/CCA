package uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository;

import java.time.LocalTime;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.gov.cca.api.account.domain.QTargetUnitAccount;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.QFacilityData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.QPerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;

@Repository
public class PerformanceDataFacilityDataCustomRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public SectorFacilityPerformanceDataReportListDTO getSectorFacilityPerformanceDataReportListSubmittedBySearchCriteria(
    		Long sectorAssociationId, SectorFacilityPerformanceDataReportSearchCriteria criteria, TargetPeriodYear targetPeriodYear) {
        QTargetUnitAccount targetUnitAccount = QTargetUnitAccount.targetUnitAccount;
        QPerformanceDataFacilityStatus facilityPerformanceDataStatus = QPerformanceDataFacilityStatus.performanceDataFacilityStatus;
        QFacilityData facilityData = QFacilityData.facilityData;
        
        BooleanBuilder whereClause = new BooleanBuilder();
        
		whereClause.and(targetUnitAccount.sectorAssociationId.eq(sectorAssociationId))
				.and(facilityPerformanceDataStatus.targetPeriodYear.eq(targetPeriodYear.getTargetYear()));

		// Optional filters
		if (!ObjectUtils.isEmpty(criteria.getFacilityOrTargetUnitAccountBusinessId())) {
			whereClause.and(targetUnitAccount.businessId
					.likeIgnoreCase('%' + criteria.getFacilityOrTargetUnitAccountBusinessId() + '%')
					.or(facilityData.facilityBusinessId
							.likeIgnoreCase('%' + criteria.getFacilityOrTargetUnitAccountBusinessId() + '%')));
		}

		if (criteria.getReportStatus() != null && !criteria.getReportStatus().equals(PerformanceDataFacilityTargetPeriodResultType.SUBMITTED)) {
			whereClause.and(
					facilityPerformanceDataStatus.lastPerformanceData.performanceOutcome.eq(criteria.getReportStatus()));
		}

		if (criteria.getSubType() != null) {
			whereClause.and(
					facilityPerformanceDataStatus.lastPerformanceData.submissionType.eq(criteria.getSubType()));
		}
        
        // Query
        JPAQuery<SectorFacilityPerformanceDataReportItemDTO> query = new JPAQuery<>(entityManager);

        JPAQuery<SectorFacilityPerformanceDataReportItemDTO> jpaQuery = query.select(Projections.constructor(SectorFacilityPerformanceDataReportItemDTO.class,
        		facilityData.id,
        		facilityData.facilityBusinessId,
        		facilityData.siteName,
        		targetUnitAccount.id,
        		facilityPerformanceDataStatus.lastPerformanceData.submissionDate,
        		facilityPerformanceDataStatus.lastPerformanceData.reportVersion,
        		new CaseBuilder()
                .when(facilityPerformanceDataStatus.lastPerformanceData.performanceOutcome.isNull())
                .then(Expressions.constant(PerformanceDataFacilityTargetPeriodResultType.SUBMITTED))
                .otherwise(facilityPerformanceDataStatus.lastPerformanceData.performanceOutcome),
        		facilityPerformanceDataStatus.lastPerformanceData.submissionType,
        		facilityPerformanceDataStatus.locked,
        		facilityPerformanceDataStatus.variationIndicator,
        		Expressions.booleanTemplate(
        				"function('jsonb_extract_path_text', {0}, 'energyFuelDetails', 'atLeastSeventyPercentEnergyUsed') = 'true'",
        		        facilityPerformanceDataStatus.lastPerformanceData.data
        		    ),
        		Expressions.stringTemplate(
        				"function('jsonb_extract_path_text', {0}, 'calculatedResults', 'actualImprovement')",
        		        facilityPerformanceDataStatus.lastPerformanceData.data
        		    ),
        		Expressions.stringTemplate(
        				"function('jsonb_extract_path_text', {0}, 'calculatedResults', 'actualEnergyCarbon')",
        		        facilityPerformanceDataStatus.lastPerformanceData.data
        		    ),
        		Expressions.stringTemplate(
        				"function('jsonb_extract_path_text', {0}, 'calculatedResults', 'targetEnergyCarbon')",
        		        facilityPerformanceDataStatus.lastPerformanceData.data
        		    ),
        		Expressions.stringTemplate(
        				"function('jsonb_extract_path_text', {0}, 'calculatedResults', 'energyCarbonDifference')",
        		        facilityPerformanceDataStatus.lastPerformanceData.data
        		    ),
        		Expressions.stringTemplate(
        				"function('jsonb_extract_path_text', {0}, 'calculatedResults', 'actualCo2Emissions')",
        		        facilityPerformanceDataStatus.lastPerformanceData.data
        		    ),
        		Expressions.stringTemplate(
        				"function('jsonb_extract_path_text', {0}, 'calculatedResults', 'targetCo2Emissions')",
        		        facilityPerformanceDataStatus.lastPerformanceData.data
        		    ),
        		Expressions.stringTemplate(
        				"function('jsonb_extract_path_text', {0}, 'calculatedResults', 'co2EmissionsDifference')",
        		        facilityPerformanceDataStatus.lastPerformanceData.data
        		    ),
        		Expressions.stringTemplate(
        				"function('jsonb_extract_path_text', {0}, 'calculatedResults', 'buyOutRequired')",
        		        facilityPerformanceDataStatus.lastPerformanceData.data
        		    ),
        		Expressions.stringTemplate(
        				"function('jsonb_extract_path_text', {0}, 'calculatedResults', 'surplusGained')",
        		        facilityPerformanceDataStatus.lastPerformanceData.data
        		    )
            ))
            .from(facilityPerformanceDataStatus)
            .innerJoin(facilityData).on(facilityPerformanceDataStatus.facilityId.eq(facilityData.id))
            .innerJoin(targetUnitAccount).on(facilityData.accountId.eq(targetUnitAccount.id))
            .where(whereClause);
        
        final java.util.List<SectorFacilityPerformanceDataReportItemDTO> items = jpaQuery.fetch();
                
        return SectorFacilityPerformanceDataReportListDTO.builder()
                .performanceDataReportItems(items)
                .total((long) items.size())
                .build();
    }
    
    public SectorFacilityPerformanceDataReportListDTO getSectorFacilityPerformanceDataReportListOutstandingBySearchCriteria(
    		Long sectorAssociationId, SectorFacilityPerformanceDataReportSearchCriteria criteria, TargetPeriodYear targetPeriodYear) {
        QTargetUnitAccount targetUnitAccount = QTargetUnitAccount.targetUnitAccount;
        QPerformanceDataFacilityStatus facilityPerformanceDataStatus = QPerformanceDataFacilityStatus.performanceDataFacilityStatus;
        QFacilityData facilityData = QFacilityData.facilityData;
        
        BooleanBuilder whereClause = new BooleanBuilder();
        
		whereClause.and(targetUnitAccount.sectorAssociationId.eq(sectorAssociationId))
				.and(Expressions.booleanTemplate("function('jsonb_exists', {0}, {1}) = true",
						facilityData.participatingSchemeVersions, SchemeVersion.CCA_3.name()));
		
		whereClause.and(facilityData.createdDate.loe(targetPeriodYear.getEndDate().atTime(LocalTime.MAX)))
				.and(facilityData.closedDate.isNull()
						.or(facilityData.closedDate.isNotNull().and(facilityData.closedDate.gt(targetPeriodYear.getEndDate().atTime(LocalTime.MAX)))));
		
		whereClause.and(facilityPerformanceDataStatus.isNull());
		
		// Optional filters
		if (!ObjectUtils.isEmpty(criteria.getFacilityOrTargetUnitAccountBusinessId())) {
			whereClause.and(targetUnitAccount.businessId
					.likeIgnoreCase('%' + criteria.getFacilityOrTargetUnitAccountBusinessId() + '%')
					.or(facilityData.facilityBusinessId
							.likeIgnoreCase('%' + criteria.getFacilityOrTargetUnitAccountBusinessId() + '%')));
		}
        
        // Query
        JPAQuery<SectorFacilityPerformanceDataReportItemDTO> query = new JPAQuery<>(entityManager);

        JPAQuery<SectorFacilityPerformanceDataReportItemDTO> jpaQuery = query.select(Projections.constructor(SectorFacilityPerformanceDataReportItemDTO.class,
        		facilityData.id,
        		facilityData.facilityBusinessId,
        		facilityData.siteName,
        		targetUnitAccount.id
            ))
            .from(facilityData)
            .innerJoin(targetUnitAccount).on(facilityData.accountId.eq(targetUnitAccount.id))
            .leftJoin(facilityPerformanceDataStatus).on(facilityPerformanceDataStatus.facilityId.eq(facilityData.id)
            		.and(facilityPerformanceDataStatus.targetPeriodYear.eq(targetPeriodYear.getTargetYear())))
            .where(whereClause);
        
        final java.util.List<SectorFacilityPerformanceDataReportItemDTO> items = jpaQuery.fetch();
        
        return SectorFacilityPerformanceDataReportListDTO.builder()
                .performanceDataReportItems(items)
                .total((long) items.size())
                .build();
    }

}
