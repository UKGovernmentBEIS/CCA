package uk.gov.cca.api.workflow.request.application.item.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.domain.QFacilityData;
import uk.gov.cca.api.sectorassociation.domain.QSectorAssociation;
import uk.gov.netz.api.account.domain.QAccount;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.application.item.domain.Item;
import uk.gov.netz.api.workflow.request.application.item.repository.ItemOperatorAbstractRepository;
import uk.gov.netz.api.workflow.request.core.domain.QRequest;
import uk.gov.netz.api.workflow.request.core.domain.QRequestResource;

@Service
@Primary
public class CcaItemOperatorRepository extends ItemOperatorAbstractRepository {

	@Override
    protected void buildSearchTermJoin(JPAQuery<Item> jpaQuery) {
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

    @Override
    protected BooleanExpression buildSearchTermWhereClause(String searchTerm) {
    	QAccount account = QAccount.account;
        QFacilityData facility = QFacilityData.facilityData;
		QSectorAssociation sector = QSectorAssociation.sectorAssociation;
		
        return account.businessId.containsIgnoreCase(searchTerm).or(account.name.containsIgnoreCase(searchTerm))
        		.or(facility.facilityBusinessId.containsIgnoreCase(searchTerm)).or(facility.siteName.containsIgnoreCase(searchTerm))
        		.or(sector.acronym.containsIgnoreCase(searchTerm));
    }
}
