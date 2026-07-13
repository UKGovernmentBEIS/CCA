package uk.gov.cca.api.workflow.request.application.item.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;
import uk.gov.cca.api.workflow.request.common.repository.CcaRequestAbstractTest;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.application.item.domain.Item;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemAssignmentType;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemOrderBy;
import uk.gov.netz.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemSearchCriteriaDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, CcaItemRegulatorRepository.class})
class CcaItemRegulatorRepositoryIT extends CcaRequestAbstractTest {

	@Autowired
    private CcaItemRegulatorRepository cut;

    @Test
    void findItems_filterByFacilitySiteNameOrBusinessId() {
    	String searchTerm = "site";
    	Long accountId = 1L;
    	TargetUnitAccount account = createAccount(accountId);
        FacilityData facility1 = createFacility(account.getId(), "siteName", "businessId1");
        FacilityData facility2 = createFacility(account.getId(), "otherName", "siteId");
        FacilityData facility3 = createFacility(account.getId(), "otherName2", "other");
        String user = "user";

        LocalDateTime t1 = LocalDateTime.now();

        LocalDate d1 = LocalDate.now();

        String requestTypeCode1 = "DUMMY_REQUEST_TYPE_1";

        String requestTaskTypeCode1 = "DUMMY_REQUEST_TASK_TYPE_APPLICATION_REVIEW";

        String statusInProgress = "inprogress";

        Map<CompetentAuthorityEnum, Set<String>> scopedRequestTaskTypes = Map.of(
            CompetentAuthorityEnum.ENGLAND, Set.of(requestTaskTypeCode1)
        );
        
        Map<String, String> resources1 = Map.of(
				CcaResourceType.FACILITY, facility1.getId().toString(),
				ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()
		);
        Map<String, String> resources2 = Map.of(
				CcaResourceType.FACILITY, facility2.getId().toString(),
				ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()
		);
        Map<String, String> resources3 = Map.of(
				CcaResourceType.FACILITY, facility3.getId().toString(),
				ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()
		);

        RequestType requestType1 = createRequestType(requestTypeCode1, "descr", "processdef", "histCat", false, false, false, false, CcaResourceType.FACILITY);
        
        Request request1 = createRequest(resources1, requestType1, "procInstId1", statusInProgress, LocalDateTime.now());
        Request request2 = createRequest(resources2, requestType1, "procInstId2", statusInProgress, LocalDateTime.now());
        Request request3 = createRequest(resources3, requestType1, "procInstId3", statusInProgress, LocalDateTime.now());

        RequestTaskType requestTaskType1 = createRequestTaskType(requestTaskTypeCode1, requestType1, false, "key2");
        RequestTask requestTask1 = createRequestTask(user, request1, requestTaskType1, "t1", t1, d1);
        RequestTask requestTask2 = createRequestTask(user, request2, requestTaskType1, "t2", t1, d1);

        createRequestTask(user, request3, requestTaskType1, "t3", t1, d1);

        ItemPage itemPage = cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes,
            PagingRequest.builder().pageNumber(0).pageSize(10).build(), getItemSearchCriteria(ItemOrderBy.NEWEST_FIRST, null, searchTerm));

        assertEquals(2L, itemPage.getTotalItems());
        assertEquals(2, itemPage.getItems().size());
        assertThat(itemPage.getItems())
        .extracting(Item::getTaskId)
        .containsExactlyInAnyOrder(
            requestTask1.getId(),
            requestTask2.getId()
        );
    }
    
    @Test
    void findItems_filterBySectorAcronymAndRequestType() {
    	String searchTerm = "ADS";
    	SectorAssociation sector1 = createSector("ADS1");
    	SectorAssociation sector2 = createSector("ADS2");
        String user = "user";

        LocalDateTime t1 = LocalDateTime.now();

        LocalDate d1 = LocalDate.now();

        String requestTypeCode1 = "DUMMY_REQUEST_TYPE_1";
        String requestTypeCode2 = "DUMMY_REQUEST_TYPE_2";

        String requestTaskTypeCode1 = "DUMMY_REQUEST_TASK_TYPE_APPLICATION_REVIEW";
        String requestTaskTypeCode2 = "DUMMY_REQUEST_TASK_TYPE_APPLICATION_REVIEW2";

        String statusInProgress = "inprogress";

        Map<CompetentAuthorityEnum, Set<String>> scopedRequestTaskTypes = Map.of(
            CompetentAuthorityEnum.ENGLAND, Set.of(requestTaskTypeCode1, requestTaskTypeCode2)
        );
        
        Map<String, String> resources1 = Map.of(
				CcaResourceType.SECTOR_ASSOCIATION, sector1.getId().toString(),
				ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()
		);
        Map<String, String> resources2 = Map.of(
				CcaResourceType.SECTOR_ASSOCIATION, sector2.getId().toString(),
				ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()
		);

        RequestType requestType1 = createRequestType(requestTypeCode1, "descr", "processdef", "histCat", false, false, false, false, CcaResourceType.SECTOR_ASSOCIATION);
        RequestType requestType2 = createRequestType(requestTypeCode2, "descr", "processdef", "histCat", false, false, false, false, CcaResourceType.SECTOR_ASSOCIATION);
        
        Request request1 = createRequest(resources1, requestType1, "procInstId1", statusInProgress, LocalDateTime.now());
        Request request2 = createRequest(resources2, requestType2, "procInstId2", statusInProgress, LocalDateTime.now());

        RequestTaskType requestTaskType1 = createRequestTaskType(requestTaskTypeCode1, requestType1, false, "key2");
        RequestTaskType requestTaskType2 = createRequestTaskType(requestTaskTypeCode2, requestType2, false, "key2");        
        RequestTask requestTask1 = createRequestTask(user, request1, requestTaskType1, "t1", t1, d1);
        createRequestTask(user, request2, requestTaskType2, "t2", t1, d1);

        ItemPage itemPage = cut.findItems(user, ItemAssignmentType.ME, scopedRequestTaskTypes,
            PagingRequest.builder().pageNumber(0).pageSize(10).build(), getItemSearchCriteria(ItemOrderBy.NEWEST_FIRST, requestTypeCode1, searchTerm));

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());
        assertThat(itemPage.getItems())
        .extracting(Item::getTaskId)
        .containsExactly(requestTask1.getId());
    }
    
    private SectorAssociation createSector(String acronym) {
    	Location location = Location.builder()
                .postcode("12345")
                .line1("123 Main St")
                .city("Springfield")
                .county("CountyName")
                .build();

            SectorAssociationContact contact = SectorAssociationContact.builder()
                .title("Mr.")
                .firstName("John")
                .lastName("Doe")
                .jobTitle("Director")
                .organisationName("Acme Corp")
                .phoneNumber("123456789")
                .email("john.doe@example.com")
                .location(location)
                .build();
            
    	SectorAssociation sector = SectorAssociation.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .legalName("Some Association Legal")
                .name("Some Association")
                .acronym(acronym)
                .facilitatorUserId("Facilitator User Id")
                .energyEprFactor("Energy Factor")
                .location(location)
                .sectorAssociationContact(contact)
                .build();

    	entityManager.persist(sector);
    	
    	return sector;
	}

	private TargetUnitAccount createAccount(Long accountId) {  	
    	AccountAddress addr1 = AccountAddress.builder()
                .line1("123 Test Street")
                .city("Test City")
                .postcode("12345")
                .country("Test Country")
                .build();
            entityManager.persist(addr1);
            
    	TargetUnitAccount account = TargetUnitAccount.builder()
    			.id(accountId)
    			.businessId("businessId")
    			.name("name")
    			.status(TargetUnitAccountStatus.LIVE)
    			.emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
    			.competentAuthority(CompetentAuthorityEnum.ENGLAND)
    			.operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .sectorAssociationId(1L)
                .address(addr1)
                .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
    			.build();

        entityManager.persist(account);

        return account;
    }
    
    private FacilityData createFacility(Long accountId, String siteName, String businessId) {
    	
    	FacilityAddress address = FacilityAddress.builder()
                .line1("123 Test Street")
                .city("Test City")
                .postcode("12345")
                .country("Test Country")
                .build();
        entityManager.persist(address);
        
    	FacilityData facility = FacilityData.builder()
    			.facilityBusinessId(businessId)
                .accountId(accountId)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                .siteName(siteName)
                .address(address)
                .createdDate(LocalDateTime.now())
                .build();

        entityManager.persist(facility);

        return facility;
    }

    private ItemSearchCriteriaDTO getItemSearchCriteria(ItemOrderBy orderBy,
                                                        String requestType,
                                                        String searchTerm) {
        return ItemSearchCriteriaDTO.builder()
        		.orderBy(orderBy)
        		.requestType(requestType)
        		.searchTerm(searchTerm)
        		.build();
    }
}
