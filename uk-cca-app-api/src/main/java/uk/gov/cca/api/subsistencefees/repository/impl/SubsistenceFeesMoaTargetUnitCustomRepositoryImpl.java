package uk.gov.cca.api.subsistencefees.repository.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.gov.cca.api.facility.domain.QFacilityData;
import uk.gov.cca.api.subsistencefees.domain.QSubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.QSubsistenceFeesMoaTargetUnit;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaTargetUnitSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaTargetUnitSearchResultsInfo;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaTargetUnitCustomRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesRepositoryUtils;
import uk.gov.netz.api.account.domain.QAccount;

@Repository
public class SubsistenceFeesMoaTargetUnitCustomRepositoryImpl implements SubsistenceFeesMoaTargetUnitCustomRepository {

	@PersistenceContext
    private EntityManager entityManager;
	
	@Override
	public SubsistenceFeesMoaTargetUnitSearchResultsInfo findBySearchCriteria(Long moaId,
			SubsistenceFeesSearchCriteria criteria) {
		QSubsistenceFeesMoaTargetUnit moaTargetUnit = QSubsistenceFeesMoaTargetUnit.subsistenceFeesMoaTargetUnit;
		QSubsistenceFeesMoaFacility moaFacility = QSubsistenceFeesMoaFacility.subsistenceFeesMoaFacility;
		QAccount account = QAccount.account;
		QFacilityData facilityData = QFacilityData.facilityData;
		
		JPAQuery<SubsistenceFeesMoaTargetUnitSearchResultInfo> query = new JPAQuery<>(entityManager);

        JPAQuery<SubsistenceFeesMoaTargetUnitSearchResultInfo> jpaQuery = query.select(
                Projections.constructor(SubsistenceFeesMoaTargetUnitSearchResultInfo.class,
                		moaTargetUnit.id, 
                        account.businessId, 
                        account.name,
                        SubsistenceFeesRepositoryUtils.currentTotalAmount(moaFacility),
                        SubsistenceFeesRepositoryUtils.facilityOutstandingAmount(moaFacility)
                        ))
                .from(moaTargetUnit)
                .innerJoin(account)
                .on(account.id.eq(moaTargetUnit.accountId))
                .innerJoin(moaFacility)
                .on(moaTargetUnit.id.eq(moaFacility.subsistenceFeesMoaTargetUnit.id))
                .innerJoin(facilityData)
                .on(facilityData.id.eq(moaFacility.facilityId))
                .where(moaTargetUnit.subsistenceFeesMoa.id.eq(moaId))
                .groupBy(moaTargetUnit.id, account.businessId, account.name)
                .orderBy(account.businessId.asc())
                .offset((long)criteria.getPaging().getPageNumber() * criteria.getPaging().getPageSize())
                .limit(criteria.getPaging().getPageSize());

        // Apply filters
        constructWhereClause(criteria, moaTargetUnit, moaFacility, facilityData, account, jpaQuery);
        constructHavingClause(criteria, moaFacility, jpaQuery);
        
        return SubsistenceFeesMoaTargetUnitSearchResultsInfo.builder()
        		.subsistenceFeesMoaTargetUnitSearchResultInfo(jpaQuery.fetch())
        		.total(jpaQuery.fetchCount())
        		.build();
	}

	private JPAQuery<SubsistenceFeesMoaTargetUnitSearchResultInfo> constructWhereClause(
			SubsistenceFeesSearchCriteria criteria,
			QSubsistenceFeesMoaTargetUnit moaTargetUnit, QSubsistenceFeesMoaFacility moaFacility, QFacilityData facilityData, QAccount account,
			JPAQuery<SubsistenceFeesMoaTargetUnitSearchResultInfo> jpaQuery) {
		
		if (ObjectUtils.isNotEmpty(criteria.getTerm())) {
			String term = "%" + criteria.getTerm() + "%";
        	jpaQuery = jpaQuery.where(account.businessId.likeIgnoreCase(term)
        			.or(account.name.likeIgnoreCase(term)
        					.or(moaTargetUnit.id.in(constructFacilitySubquery(moaFacility, facilityData, term)))));
        }
		return jpaQuery;
		
	}

	private JPQLQuery<Long> constructFacilitySubquery(QSubsistenceFeesMoaFacility moaFacility,
			QFacilityData facilityData, String term) {
		return JPAExpressions.select(moaFacility.subsistenceFeesMoaTargetUnit.id).distinct()
				.from(moaFacility)
				.innerJoin(facilityData)
				.on(facilityData.id.eq(moaFacility.facilityId))
				.where(facilityData.facilityBusinessId.likeIgnoreCase(term));
	}

	private JPAQuery<SubsistenceFeesMoaTargetUnitSearchResultInfo> constructHavingClause(
			SubsistenceFeesSearchCriteria criteria,
			QSubsistenceFeesMoaFacility moaFacility,
			JPAQuery<SubsistenceFeesMoaTargetUnitSearchResultInfo> jpaQuery) {
		
		return SubsistenceFeesRepositoryUtils.constructMarkFacilitiesStatusQuery(
				criteria.getMarkFacilitiesStatus(), moaFacility, jpaQuery);
		
	}

}
