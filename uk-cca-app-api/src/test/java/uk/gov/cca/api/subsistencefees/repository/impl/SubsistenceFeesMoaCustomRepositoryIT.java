package uk.gov.cca.api.subsistencefees.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaTargetUnit;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultsInfo;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SubsistenceFeesMoaCustomRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
    private SubsistenceFeesMoaCustomRepositoryImpl repository;

    @Autowired
    private EntityManager entityManager;

    private final LocalDateTime submissionDate = LocalDateTime.of(2025, 4, 1, 12, 22, 44);

    @BeforeEach
    void setUp() {
    	// Persist sector associations
    	SectorAssociation sector1 = createSectorAssociation("name", "acronym1");
    	SectorAssociation sector2 = createSectorAssociation("name", "acronym2");
        // Persist run
        SubsistenceFeesRun run = SubsistenceFeesRun.builder()
                .businessId("S2501")
                .chargingYear(Year.of(2025))
                .initialTotalAmount(BigDecimal.valueOf(1000L))
                .submissionDate(submissionDate)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        entityManager.persist(run);

        // Set up MoAs
        SubsistenceFeesMoa moa1 = SubsistenceFeesMoa.builder()
                .transactionId("CCACM1200")
                .subsistenceFeesRun(run)
                .moaType(MoaType.SECTOR_MOA)
                .resourceId(sector1.getId())
                .initialTotalAmount(BigDecimal.valueOf(400L))
                .regulatorReceivedAmount(BigDecimal.valueOf(2000L))
                .fileDocumentUuid("111-111-111")
                .submissionDate(submissionDate)
                .build();
        entityManager.persist(moa1);
        
        SubsistenceFeesMoa moa2 = SubsistenceFeesMoa.builder()
                .transactionId("CCACM1201")
                .subsistenceFeesRun(run)
                .moaType(MoaType.SECTOR_MOA)
                .resourceId(sector2.getId())
                .initialTotalAmount(BigDecimal.valueOf(400L))
                .regulatorReceivedAmount(BigDecimal.valueOf(200L))
                .fileDocumentUuid("111-111-111")
                .submissionDate(submissionDate)
                .build();
        entityManager.persist(moa2);
        
        SubsistenceFeesMoa moa3 = SubsistenceFeesMoa.builder()
                .transactionId("CCATM1200")
                .subsistenceFeesRun(run)
                .moaType(MoaType.TARGET_UNIT_MOA)
                .resourceId(40L)
                .initialTotalAmount(BigDecimal.valueOf(200L))
                .regulatorReceivedAmount(BigDecimal.valueOf(1L))
                .fileDocumentUuid("111-111-111")
                .submissionDate(submissionDate)
                .build();
        entityManager.persist(moa3);

        // Set up MoA target units
        SubsistenceFeesMoaTargetUnit tu1 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa1)
                .accountId(40L)
                .initialTotalAmount(BigDecimal.valueOf(200L))
                .build();
        entityManager.persist(tu1);
        SubsistenceFeesMoaTargetUnit tu2 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa1)
                .accountId(41L)
                .initialTotalAmount(BigDecimal.valueOf(200L))
                .build();
        entityManager.persist(tu2);
        SubsistenceFeesMoaTargetUnit tu3 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa2)
                .accountId(38L)
                .initialTotalAmount(BigDecimal.valueOf(200L))
                .build();
        entityManager.persist(tu3);
        SubsistenceFeesMoaTargetUnit tu4 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa2)
                .accountId(39L)
                .initialTotalAmount(BigDecimal.valueOf(200L))
                .build();
        entityManager.persist(tu4);
        SubsistenceFeesMoaTargetUnit tu5 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa3)
                .accountId(40L)
                .initialTotalAmount(BigDecimal.valueOf(100L))
                .build();
        entityManager.persist(tu5);

        // Set up MoA facilities
        SubsistenceFeesMoaFacility facility1 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu1)
                .facilityId(1L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .build();
        entityManager.persist(facility1);
        SubsistenceFeesMoaFacility facility2 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu1)
                .facilityId(2L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility2);
        SubsistenceFeesMoaFacility facility3 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu2)
                .facilityId(3L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.CANCELLED)
                .build();
        entityManager.persist(facility3);
        SubsistenceFeesMoaFacility facility4 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu2)
                .facilityId(4L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .build();
        entityManager.persist(facility4);
        SubsistenceFeesMoaFacility facility5 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu3)
                .facilityId(5L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .build();
        entityManager.persist(facility5);
        SubsistenceFeesMoaFacility facility6 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu3)
                .facilityId(7L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .build();
        entityManager.persist(facility6);
        SubsistenceFeesMoaFacility facility7 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu4)
                .facilityId(8L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .build();
        entityManager.persist(facility7);
        SubsistenceFeesMoaFacility facility8 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu4)
                .facilityId(9L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .build();
        entityManager.persist(facility8);
        SubsistenceFeesMoaFacility facility9 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu5)
                .facilityId(9L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility9);

        flushAndClear();
    }

    @Test
    @Order(1)
    void findBySearchCriteriaForRegulator_noCriteria() {
    	PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0).pageSize(50).build();
    	SubsistenceFeesMoaSearchCriteria criteria = SubsistenceFeesMoaSearchCriteria.builder()
    			.moaType(MoaType.SECTOR_MOA)
    			.paging(pagingRequest)
    			.build();
    	
    	SubsistenceFeesMoaSearchResultInfo resultInfo1 = new SubsistenceFeesMoaSearchResultInfo(1L, "CCACM1200", 
    			"acronym1", "name", new BigDecimal("300.00"),new BigDecimal("100.00"), new BigDecimal("2000.00"), submissionDate);
    	SubsistenceFeesMoaSearchResultInfo resultInfo2 = new SubsistenceFeesMoaSearchResultInfo(2L, "CCACM1201", 
    			"acronym2", "name", new BigDecimal("400.00"), BigDecimal.ZERO, new BigDecimal("200.00"), submissionDate);
    	SubsistenceFeesMoaSearchResultsInfo expectedResults = SubsistenceFeesMoaSearchResultsInfo.builder()
    			.subsistenceFeesMoaSearchResultInfo(List.of(resultInfo1, resultInfo2))
    			.total(2L)
    			.build();

    	SubsistenceFeesMoaSearchResultsInfo resultInfos = repository.findBySearchCriteriaForCAView(1L, criteria);

        assertThat(resultInfos).isEqualTo(expectedResults);
    }
    
    @Test
    @Order(2)
    void findBySearchCriteria_term_in_name_and_facilities_status() {
    	PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0).pageSize(50).build();
    	SubsistenceFeesMoaSearchCriteria criteria = SubsistenceFeesMoaSearchCriteria.builder()
    			.moaType(MoaType.SECTOR_MOA)
    			.term("nam")
    			.markFacilitiesStatus(FacilityPaymentStatus.COMPLETED)   			
    			.paging(pagingRequest)
    			.build();
    	
    	SubsistenceFeesMoaSearchResultInfo resultInfo = new SubsistenceFeesMoaSearchResultInfo(5L, "CCACM1201", 
    			"acronym2", "name", new BigDecimal("400.00"), BigDecimal.ZERO, new BigDecimal("200.00"), submissionDate);
    	SubsistenceFeesMoaSearchResultsInfo expectedResults = SubsistenceFeesMoaSearchResultsInfo.builder()
    			.subsistenceFeesMoaSearchResultInfo(List.of(resultInfo))
    			.total(1L)
    			.build();

        SubsistenceFeesMoaSearchResultsInfo resultInfos = repository.findBySearchCriteriaForCAView(2L, criteria);

        assertThat(resultInfos).isEqualTo(expectedResults);
    }
    
    @Test
    @Order(3)
    void findBySearchCriteria_paymentStatus() {
    	PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0).pageSize(50).build();
    	SubsistenceFeesMoaSearchCriteria criteria = SubsistenceFeesMoaSearchCriteria.builder()
    			.moaType(MoaType.SECTOR_MOA)
    			.paymentStatus(PaymentStatus.OVERPAID)
    			.paging(pagingRequest)
    			.build();
    	
    	SubsistenceFeesMoaSearchResultInfo resultInfo = new SubsistenceFeesMoaSearchResultInfo(7L, "CCACM1200", 
    			"acronym1", "name", new BigDecimal("300.00"), new BigDecimal("100.00"), new BigDecimal("2000.00"), submissionDate);
    	SubsistenceFeesMoaSearchResultsInfo expectedResults = SubsistenceFeesMoaSearchResultsInfo.builder()
    			.subsistenceFeesMoaSearchResultInfo(List.of(resultInfo))
    			.total(1L)
    			.build();

        SubsistenceFeesMoaSearchResultsInfo resultInfos = repository.findBySearchCriteriaForCAView(3L, criteria);

        assertThat(resultInfos).isEqualTo(expectedResults);
    }
    
    @Test
    @Order(4)
    void findBySearchCriteriaForSectorUser_noCriteria() {
    	PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0).pageSize(50).build();
    	SubsistenceFeesMoaSearchCriteria criteria = SubsistenceFeesMoaSearchCriteria.builder()
    			.paging(pagingRequest)
    			.build();
    	
    	SubsistenceFeesMoaSearchResultInfo resultInfo1 = new SubsistenceFeesMoaSearchResultInfo(10L, "CCACM1200", 
    			"acronym1", "name", new BigDecimal("300.00"),new BigDecimal("100.00"), new BigDecimal("2000.00"), submissionDate);
    	SubsistenceFeesMoaSearchResultsInfo expectedResults = SubsistenceFeesMoaSearchResultsInfo.builder()
    			.subsistenceFeesMoaSearchResultInfo(List.of(resultInfo1))
    			.total(1L)
    			.build();

    	SubsistenceFeesMoaSearchResultsInfo resultInfos = repository.findBySearchCriteriaForSectorAssociationView(7L, criteria);

        assertThat(resultInfos).isEqualTo(expectedResults);
    }
    
    @Test
    @Order(5)
    void findBySearchCriteriaForSectorUser_term_not_in_transaction_id_() {
    	PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0).pageSize(50).build();
    	SubsistenceFeesMoaSearchCriteria criteria = SubsistenceFeesMoaSearchCriteria.builder()
    			.moaType(MoaType.SECTOR_MOA)
    			.term("nam")
    			.markFacilitiesStatus(FacilityPaymentStatus.COMPLETED)   			
    			.paging(pagingRequest)
    			.build();

    	SubsistenceFeesMoaSearchResultsInfo expectedResults = SubsistenceFeesMoaSearchResultsInfo.builder()
    			.subsistenceFeesMoaSearchResultInfo(List.of())
    			.total(0L)
    			.build();

        SubsistenceFeesMoaSearchResultsInfo resultInfos = repository.findBySearchCriteriaForCAView(9L, criteria);

        assertThat(resultInfos).isEqualTo(expectedResults);
    }
    
    @AfterEach
    void flushAndClear() {
    	entityManager.flush();
        entityManager.clear();
    }
    
    private SectorAssociation createSectorAssociation(String name, String acronym) {
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

        SectorAssociation sectorAssociation = SectorAssociation.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .legalName("Some Association Legal")
                .name(name)
                .acronym(acronym)
                .facilitatorUserId("Facilitator User Id")
                .energyEprFactor("Energy Factor")
                .location(location)
                .sectorAssociationContact(contact)
                .build();
        entityManager.persist(sectorAssociation);

        return sectorAssociation;
    }
}
