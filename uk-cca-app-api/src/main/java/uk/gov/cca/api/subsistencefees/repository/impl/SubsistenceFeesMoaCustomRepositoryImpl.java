package uk.gov.cca.api.subsistencefees.repository.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.gov.cca.api.sectorassociation.domain.QSectorAssociation;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.QSubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.QSubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.QSubsistenceFeesMoaTargetUnit;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultsInfo;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaCustomRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesRepositoryUtils;
import uk.gov.netz.api.account.domain.QAccount;

@Repository
public class SubsistenceFeesMoaCustomRepositoryImpl implements SubsistenceFeesMoaCustomRepository {

	@PersistenceContext
    private EntityManager entityManager;
	
	@Override
	public SubsistenceFeesMoaSearchResultsInfo findBySearchCriteriaForCAView(Long runId, SubsistenceFeesMoaSearchCriteria criteria) {
		QSubsistenceFeesMoa moa = QSubsistenceFeesMoa.subsistenceFeesMoa;
		QSubsistenceFeesMoaTargetUnit moaTargetUnit = QSubsistenceFeesMoaTargetUnit.subsistenceFeesMoaTargetUnit;
		QSubsistenceFeesMoaFacility moaFacility = QSubsistenceFeesMoaFacility.subsistenceFeesMoaFacility;
		QSectorAssociation sector = QSectorAssociation.sectorAssociation;
		QAccount account = QAccount.account;
		
		JPAQuery<SubsistenceFeesMoaSearchResultInfo> query = new JPAQuery<>(entityManager);

        JPAQuery<SubsistenceFeesMoaSearchResultInfo> jpaQuery = query.select(
                Projections.constructor(SubsistenceFeesMoaSearchResultInfo.class,
                        moa.id, 
                        moa.transactionId, 
                        isSectorMoa(criteria) ? sector.acronym : account.businessId, 
                        isSectorMoa(criteria) ? sector.name : account.name,
                        SubsistenceFeesRepositoryUtils.currentTotalAmount(moaFacility),
                        SubsistenceFeesRepositoryUtils.facilityOutstandingAmount(moaFacility),
                        moa.regulatorReceivedAmount,
                        moa.submissionDate
                        ))
                .from(moa)
                .innerJoin(moaTargetUnit)
                .on(moa.id.eq(moaTargetUnit.subsistenceFeesMoa.id))
                .innerJoin(moaFacility)
                .on(moaTargetUnit.id.eq(moaFacility.subsistenceFeesMoaTargetUnit.id))
                .where(moa.subsistenceFeesRun.id.eq(runId).and(moa.moaType.eq(criteria.getMoaType())))
                .groupBy(moa.id, moa.transactionId)
                .orderBy(isSectorMoa(criteria) ? sector.acronym.asc() : account.businessId.asc())
                .offset(criteria.getPaging().getPageNumber() * criteria.getPaging().getPageSize())
                .limit(criteria.getPaging().getPageSize());

        constructSectorOrAccountJoinQuery(criteria, moa, sector, account, jpaQuery);
        // Apply filters
        constructWhereClause(criteria, moa, sector, account, jpaQuery);
        constructHavingClause(criteria, moaFacility, moa, jpaQuery);
        
        return SubsistenceFeesMoaSearchResultsInfo.builder()
        		.subsistenceFeesMoaSearchResultInfo(jpaQuery.fetch())
        		.total(jpaQuery.fetchCount())
        		.build();
	}
	
	@Override
	public SubsistenceFeesMoaSearchResultsInfo findBySearchCriteriaForSectorAssociationView(Long sectorAssociationId, SubsistenceFeesMoaSearchCriteria criteria) {
		QSubsistenceFeesMoa moa = QSubsistenceFeesMoa.subsistenceFeesMoa;
		QSubsistenceFeesMoaTargetUnit moaTargetUnit = QSubsistenceFeesMoaTargetUnit.subsistenceFeesMoaTargetUnit;
		QSubsistenceFeesMoaFacility moaFacility = QSubsistenceFeesMoaFacility.subsistenceFeesMoaFacility;
		QSectorAssociation sector = QSectorAssociation.sectorAssociation;
		
		JPAQuery<SubsistenceFeesMoaSearchResultInfo> query = new JPAQuery<>(entityManager);

        JPAQuery<SubsistenceFeesMoaSearchResultInfo> jpaQuery = query.select(
                Projections.constructor(SubsistenceFeesMoaSearchResultInfo.class,
                        moa.id, 
                        moa.transactionId, 
                        sector.acronym, 
                        sector.name,
                        SubsistenceFeesRepositoryUtils.currentTotalAmount(moaFacility),
                        SubsistenceFeesRepositoryUtils.facilityOutstandingAmount(moaFacility),
                        moa.regulatorReceivedAmount,
                        moa.submissionDate
                        ))
                .from(moa)
                .innerJoin(sector)
                .on(moa.resourceId.eq(sector.id))
                .innerJoin(moaTargetUnit)
                .on(moa.id.eq(moaTargetUnit.subsistenceFeesMoa.id))
                .innerJoin(moaFacility)
                .on(moaTargetUnit.id.eq(moaFacility.subsistenceFeesMoaTargetUnit.id))
                .where(moa.resourceId.eq(sectorAssociationId).and(moa.moaType.eq(MoaType.SECTOR_MOA)))
                .groupBy(moa.id, moa.transactionId, sector.acronym, sector.name)
                .orderBy(moa.submissionDate.desc())
                .offset(criteria.getPaging().getPageNumber() * criteria.getPaging().getPageSize())
                .limit(criteria.getPaging().getPageSize());

        // Apply filters
        constructWhereClause(criteria, moa, jpaQuery);
        constructHavingClause(criteria, moaFacility, moa, jpaQuery);
        
        return SubsistenceFeesMoaSearchResultsInfo.builder()
        		.subsistenceFeesMoaSearchResultInfo(jpaQuery.fetch())
        		.total(jpaQuery.fetchCount())
        		.build();
	}

	private JPAQuery<SubsistenceFeesMoaSearchResultInfo> constructSectorOrAccountJoinQuery(
			SubsistenceFeesMoaSearchCriteria criteria, QSubsistenceFeesMoa moa, QSectorAssociation sector,
			QAccount account, JPAQuery<SubsistenceFeesMoaSearchResultInfo> jpaQuery) {
		
		jpaQuery = isSectorMoa(criteria) ? jpaQuery.innerJoin(sector).on(moa.resourceId.eq(sector.id)).groupBy(sector.acronym, sector.name) 
        		: jpaQuery.innerJoin(account).on(moa.resourceId.eq(account.id)).groupBy(account.businessId, account.name);
		return jpaQuery;
	}

	private JPAQuery<SubsistenceFeesMoaSearchResultInfo> constructWhereClause(SubsistenceFeesMoaSearchCriteria criteria,
			QSubsistenceFeesMoa moa, QSectorAssociation sector, QAccount account,
			JPAQuery<SubsistenceFeesMoaSearchResultInfo> jpaQuery) {
		
		if (ObjectUtils.isNotEmpty(criteria.getTerm())) {
			String term = "%" + criteria.getTerm() + "%";
        	jpaQuery = jpaQuery.where(isSectorMoa(criteria) ? 
        			moa.transactionId.likeIgnoreCase(term).or(sector.acronym.likeIgnoreCase(term).or(sector.name.likeIgnoreCase(term))) 
        			: moa.transactionId.likeIgnoreCase(term).or(account.businessId.likeIgnoreCase(term).or(account.name.likeIgnoreCase(term))));
        }
		return jpaQuery;
	}
	
	private JPAQuery<SubsistenceFeesMoaSearchResultInfo> constructWhereClause(SubsistenceFeesMoaSearchCriteria criteria, 
			QSubsistenceFeesMoa moa, JPAQuery<SubsistenceFeesMoaSearchResultInfo> jpaQuery) {
		return ObjectUtils.isNotEmpty(criteria.getTerm()) 
				? jpaQuery.where(moa.transactionId.likeIgnoreCase("%" + criteria.getTerm() + "%")) 
						: jpaQuery;
	}

	private JPAQuery<SubsistenceFeesMoaSearchResultInfo> constructHavingClause(
			SubsistenceFeesMoaSearchCriteria criteria, QSubsistenceFeesMoaFacility facility,
			QSubsistenceFeesMoa moa, JPAQuery<SubsistenceFeesMoaSearchResultInfo> jpaQuery) {
		
		// Mark facilities status
		jpaQuery = SubsistenceFeesRepositoryUtils.constructMarkFacilitiesStatusQuery(criteria.getMarkFacilitiesStatus(), facility, jpaQuery);
		// Payment status
		jpaQuery = SubsistenceFeesRepositoryUtils.constructPaymentStatusQuery(criteria.getPaymentStatus(), facility, moa, jpaQuery);
		return jpaQuery;
	}
	
	private boolean isSectorMoa(SubsistenceFeesMoaSearchCriteria criteria) {
		return MoaType.SECTOR_MOA.equals(criteria.getMoaType());
	}

}
