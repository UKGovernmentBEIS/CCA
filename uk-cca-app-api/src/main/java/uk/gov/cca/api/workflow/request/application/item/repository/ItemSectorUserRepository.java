package uk.gov.cca.api.workflow.request.application.item.repository;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.gov.netz.api.workflow.request.application.item.domain.Item;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.netz.api.workflow.request.application.item.domain.QRequestTaskVisit;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemSearchCriteriaDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.domain.QFacilityData;
import uk.gov.cca.api.sectorassociation.domain.QSectorAssociation;
import uk.gov.netz.api.account.domain.QAccount;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.workflow.request.core.domain.QRequest;
import uk.gov.netz.api.workflow.request.core.domain.QRequestResource;
import uk.gov.netz.api.workflow.request.core.domain.QRequestTask;

@Repository
public class ItemSectorUserRepository {

	@PersistenceContext
    private EntityManager entityManager;

    public ItemPage findItems(String userId, ItemAssignmentType assignmentType, Map<Long, Set<String>> scopedUserRequestTaskTypes, PagingRequest paging, ItemSearchCriteriaDTO searchCriteria) {
        QRequest request = QRequest.request;
        QRequestTask requestTask = QRequestTask.requestTask;
        QRequestTaskVisit requestTaskVisit = QRequestTaskVisit.requestTaskVisit;
        QRequestResource requestResource = QRequestResource.requestResource;

        JPAQuery<Item> query = new JPAQuery<>(entityManager);

        JPAQuery<Item> jpaQuery = query.select(
                        Projections.constructor(Item.class,
                                requestTask.startDate,
                                request.id, request.type,
                                requestTask.id, requestTask.type, requestTask.assignee,
                                requestTask.dueDate, requestTask.pauseDate, requestTaskVisit.isNull()))
                .from(request)
                .innerJoin(requestResource)
                .on(request.id.eq(requestResource.request.id))
                .innerJoin(requestTask)
                .on(request.id.eq(requestTask.request.id))
                .leftJoin(requestTaskVisit)
                .on(requestTask.id.eq(requestTaskVisit.taskId).and(requestTaskVisit.userId.eq(userId)));
        
        if (!StringUtils.isBlank(searchCriteria.getSearchTerm())) {
            buildSearchTermJoin(jpaQuery);
        }

        jpaQuery.where(constructWherePredicate(userId, assignmentType, request, requestTask, requestResource,
        		scopedUserRequestTaskTypes, searchCriteria.getRequestType(), searchCriteria.getSearchTerm()))
                .orderBy(searchCriteria.getOrderBy().getOrderSpecifier())
                .offset((long)paging.getPageNumber() * paging.getPageSize())
                .limit(paging.getPageSize());

        return ItemPage.builder()
                .items(jpaQuery.fetch())
                .totalItems(jpaQuery.fetchCount())
                .build();
    }

    private void buildSearchTermJoin(JPAQuery<Item> jpaQuery) {
    	QRequestResource accountResource = new QRequestResource("accountResource");
		QRequestResource facilityResource = new QRequestResource("facilityResource");
		QRequestResource sectorResource = new QRequestResource("sectorResource");
		
		QRequest request = QRequest.request;
		QAccount account = QAccount.account;
		QFacilityData facility = QFacilityData.facilityData;
		QSectorAssociation sector = QSectorAssociation.sectorAssociation;

		jpaQuery
		    .leftJoin(accountResource)
		    .on(
		        request.id.eq(accountResource.request.id)
		            .and(accountResource.resourceType.eq(ResourceType.ACCOUNT))
		    )
		    .leftJoin(account)
		    .on(account.id.stringValue().eq(accountResource.resourceId))

		    .leftJoin(facilityResource)
		    .on(
		        request.id.eq(facilityResource.request.id)
		            .and(facilityResource.resourceType.eq(CcaResourceType.FACILITY))
		    )
		    .leftJoin(facility)
		    .on(facility.id.stringValue().eq(facilityResource.resourceId))

		    .leftJoin(sectorResource)
		    .on(
		        request.id.eq(sectorResource.request.id)
		            .and(sectorResource.resourceType.eq(CcaResourceType.SECTOR_ASSOCIATION))
		    )
		    .leftJoin(sector)
		    .on(sector.id.stringValue().eq(sectorResource.resourceId));
	}

	private Predicate constructWherePredicate(String userId, ItemAssignmentType assignmentType,
											  QRequest request, QRequestTask requestTask, QRequestResource requestResource,
                                              Map<Long, Set<String>> scopedUserRequestTaskTypes,
                                              String requestType,
                                              String searchTerm) {
        BooleanExpression whereClause = CcaItemRepoUtils.constructSectorRequestTaskScopeWhereClause(
        		scopedUserRequestTaskTypes, requestTask, requestResource);

        whereClause = switch (assignmentType) {
            case ME -> requestTask.assignee.eq(userId).and(whereClause);
            case OTHERS -> requestTask.assignee.ne(userId).and(whereClause);
            case UNASSIGNED -> requestTask.assignee.isNull().and(whereClause);
        };
        
        if (!StringUtils.isBlank(requestType)) {
            whereClause = whereClause.and(request.type.code.eq(requestType));
        }

        if (!StringUtils.isBlank(searchTerm)) {
            whereClause = whereClause.and(buildSearchTermWhereClause(searchTerm));
        }
        
        return whereClause;
    }

	private BooleanExpression buildSearchTermWhereClause(String searchTerm) {
		QAccount account = QAccount.account;
        QFacilityData facility = QFacilityData.facilityData;
		QSectorAssociation sector = QSectorAssociation.sectorAssociation;
		
        return account.businessId.containsIgnoreCase(searchTerm).or(account.name.containsIgnoreCase(searchTerm))
        		.or(facility.facilityBusinessId.containsIgnoreCase(searchTerm)).or(facility.siteName.containsIgnoreCase(searchTerm))
        		.or(sector.acronym.containsIgnoreCase(searchTerm));
	}
}
