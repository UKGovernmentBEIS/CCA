package uk.gov.cca.api.subsistencefees.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaTargetUnit;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class SubsistenceFeesMoaFacilityRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
    private SubsistenceFeesMoaFacilityRepository repository;

    @Autowired
    private EntityManager entityManager;
    
    private final LocalDateTime submissionDate = LocalDateTime.now();
    private final LocalDate paymentDate = LocalDate.now();

    @BeforeEach
    public void setUp() {   	
    	// set up facility addresses
        FacilityAddress address1 = FacilityAddress.builder()
                .line1("123 Test Street")
                .city("Test City")
                .postcode("12345")
                .country("Test Country")
                .build();
        entityManager.persist(address1);

        FacilityAddress address2 = FacilityAddress.builder()
                .line1("456 Test Avenue")
                .city("Another City")
                .postcode("67890")
                .country("Another Country")
                .build();
        entityManager.persist(address2);

        FacilityAddress address3 = FacilityAddress.builder()
                .line1("789 Test Road")
                .city("Third City")
                .postcode("54321")
                .country("Third Country")
                .build();
        entityManager.persist(address3);

        FacilityAddress address4 = FacilityAddress.builder()
                .line1("789 Test Road")
                .city("Third City")
                .postcode("54321")
                .country("Third Country")
                .build();
        entityManager.persist(address4);

        FacilityAddress address5 = FacilityAddress.builder()
                .line1("789 Test Road")
                .city("Third City")
                .postcode("54321")
                .country("Third Country")
                .build();
        entityManager.persist(address5);

        // set up facilities
        FacilityData facilityData1 = FacilityData.builder()
                .facilityId("term")
                .accountId(1L)
                .siteName("site1")
                .schemeExitDate(null)
                .chargeStartDate(LocalDate.of(2025, 1, 1))
                .address(address1)
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(facilityData1);

        FacilityData facilityData2 = FacilityData.builder()
                .facilityId("ADS_1-F00015")
                .accountId(1L)
                .siteName("site2")
                .address(address2)
                .schemeExitDate(null)
                .chargeStartDate(LocalDate.of(2024, 12, 29))
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(facilityData2);

        FacilityData facilityData3 = FacilityData.builder()
                .facilityId("ADS_1-F00016")
                .accountId(1L)
                .siteName("site3")
                .address(address3)
                .chargeStartDate(LocalDate.of(2024, 1, 1))
                .closedDate(LocalDateTime.of(2024, 8, 1, 12, 0))
                .schemeExitDate(LocalDate.of(2024, 8, 1))
                .createdDate(LocalDateTime.of(2024, 8, 5, 12, 0))
                .build();
        entityManager.persist(facilityData3);

        FacilityData facilityData4 = FacilityData.builder()
                .facilityId("ADS_1-F00017")
                .accountId(1L)
                .siteName("site4")
                .address(address4)
                .chargeStartDate(LocalDate.of(2024, 1, 1))
                .schemeExitDate(LocalDate.of(2024, 8, 1))
                .closedDate(LocalDateTime.of(2024, 8, 1, 12, 0))
                .createdDate(LocalDateTime.of(2024, 8, 5, 12, 0))
                .build();
        entityManager.persist(facilityData4);
        
        // Persist run
        SubsistenceFeesRun run = SubsistenceFeesRun.builder()
                .businessId("S2501")
                .chargingYear(Year.of(2025))
                .initialTotalAmount(BigDecimal.valueOf(1000L))
                .submissionDate(submissionDate)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        entityManager.persist(run);

        // Set up MoA
        SubsistenceFeesMoa moa1 = SubsistenceFeesMoa.builder()
                .transactionId("CCACM1200")
                .subsistenceFeesRun(run)
                .moaType(MoaType.SECTOR_MOA)
                .resourceId(1L)
                .initialTotalAmount(BigDecimal.valueOf(1000L))
                .regulatorReceivedAmount(BigDecimal.ZERO)
                .fileDocumentUuid("111-111-111")
                .submissionDate(submissionDate)
                .build();
        entityManager.persist(moa1);

        // Set up MoA target unit
        SubsistenceFeesMoaTargetUnit tu1 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa1)
                .accountId(1L)
                .initialTotalAmount(BigDecimal.valueOf(370L))
                .build();
        entityManager.persist(tu1);

        // Set up MoA facilities
        SubsistenceFeesMoaFacility facility1 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu1)
                .facilityId(facilityData1.getId())
                .initialAmount(BigDecimal.valueOf(185L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .paymentDate(paymentDate)
                .build();
        entityManager.persist(facility1);
        SubsistenceFeesMoaFacility facility2 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu1)
                .facilityId(facilityData2.getId())
                .initialAmount(BigDecimal.valueOf(185L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .paymentDate(paymentDate)
                .build();
        entityManager.persist(facility2);
        SubsistenceFeesMoaFacility facility3 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu1)
                .facilityId(facilityData3.getId())
                .initialAmount(BigDecimal.valueOf(185L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .paymentDate(paymentDate)
                .build();
        entityManager.persist(facility3);
        SubsistenceFeesMoaFacility facility4 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu1)
                .facilityId(facilityData4.getId())
                .initialAmount(BigDecimal.valueOf(185L))
                .paymentStatus(FacilityPaymentStatus.CANCELLED)
                .paymentDate(paymentDate)
                .build();
        entityManager.persist(facility4);

        flushAndClear();
    }

    @Test
    void findBySearchCriteria_noCriteria() {
        LocalDate paymentDate = LocalDate.now();
    	PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0L).pageSize(50L).build();
    	SubsistenceFeesSearchCriteria criteria = SubsistenceFeesSearchCriteria.builder()
    			.paging(pagingRequest)
    			.build();
    	Pageable pageable = getPageable(criteria);
    	
    	SubsistenceFeesMoaFacilitySearchResultInfoDTO resultInfo1 = new SubsistenceFeesMoaFacilitySearchResultInfoDTO(
    			1L, "term", "site1", FacilityPaymentStatus.COMPLETED, paymentDate);
    	SubsistenceFeesMoaFacilitySearchResultInfoDTO resultInfo2 = new SubsistenceFeesMoaFacilitySearchResultInfoDTO(
    			2L, "ADS_1-F00015", "site2", FacilityPaymentStatus.IN_PROGRESS, paymentDate);
    	SubsistenceFeesMoaFacilitySearchResultInfoDTO resultInfo3 = new SubsistenceFeesMoaFacilitySearchResultInfoDTO(
    			3L, "ADS_1-F00016", "site3", FacilityPaymentStatus.COMPLETED, paymentDate);
    	SubsistenceFeesMoaFacilitySearchResultInfoDTO resultInfo4 = new SubsistenceFeesMoaFacilitySearchResultInfoDTO(
    			4L, "ADS_1-F00017", "site4", FacilityPaymentStatus.CANCELLED, paymentDate);
    	SubsistenceFeesMoaFacilitySearchResults expectedResults = SubsistenceFeesMoaFacilitySearchResults.builder()
    			.subsistenceFeesMoaFacilities(List.of(resultInfo2, resultInfo3, resultInfo4, resultInfo1))
    			.total(4L)
    			.build();

    	Page<SubsistenceFeesMoaFacilitySearchResultInfoDTO> resultInfos = repository.findBySearchCriteria(pageable, 1L, "", null);

        assertThat(resultInfos.getContent()).isEqualTo(expectedResults.getSubsistenceFeesMoaFacilities());
        assertThat(resultInfos.getTotalElements()).isEqualTo(expectedResults.getTotal());
    }
    
    @Test
    void findBySearchCriteria_term_in_facility_id() {
    	PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0L).pageSize(50L).build();
    	String term = "ter";
    	SubsistenceFeesSearchCriteria criteria = SubsistenceFeesSearchCriteria.builder()
    			.paging(pagingRequest)
    			.term(term)
    			.build();
    	Pageable pageable = getPageable(criteria);
    	
    	SubsistenceFeesMoaFacilitySearchResultInfoDTO resultInfo1 = new SubsistenceFeesMoaFacilitySearchResultInfoDTO(
    			5L, "term", "site1", FacilityPaymentStatus.COMPLETED, paymentDate);
    	SubsistenceFeesMoaFacilitySearchResults expectedResults = SubsistenceFeesMoaFacilitySearchResults.builder()
    			.subsistenceFeesMoaFacilities(List.of(resultInfo1))
    			.total(1L)
    			.build();

    	Page<SubsistenceFeesMoaFacilitySearchResultInfoDTO> resultInfos = repository.findBySearchCriteria(pageable, 2L, term, null);

    	assertThat(resultInfos.getContent()).isEqualTo(expectedResults.getSubsistenceFeesMoaFacilities());
        assertThat(resultInfos.getTotalElements()).isEqualTo(expectedResults.getTotal());
    }
    
    @Test
    void findBySearchCriteria_facilityStatus() {
    	PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0L).pageSize(50L).build();
    	SubsistenceFeesSearchCriteria criteria = SubsistenceFeesSearchCriteria.builder()
    			.paging(pagingRequest)
    			.build();
    	Pageable pageable = getPageable(criteria);
    	
    	SubsistenceFeesMoaFacilitySearchResultInfoDTO resultInfo1 = new SubsistenceFeesMoaFacilitySearchResultInfoDTO(
    			9L, "term", "site1", FacilityPaymentStatus.COMPLETED, paymentDate);
    	SubsistenceFeesMoaFacilitySearchResultInfoDTO resultInfo2 = new SubsistenceFeesMoaFacilitySearchResultInfoDTO(
    			11L, "ADS_1-F00016", "site3", FacilityPaymentStatus.COMPLETED, paymentDate);
    	SubsistenceFeesMoaFacilitySearchResults expectedResults = SubsistenceFeesMoaFacilitySearchResults.builder()
    			.subsistenceFeesMoaFacilities(List.of(resultInfo2, resultInfo1))
    			.total(2L)
    			.build();

    	Page<SubsistenceFeesMoaFacilitySearchResultInfoDTO> resultInfos = repository.findBySearchCriteria(pageable, 3L, "", FacilityPaymentStatus.COMPLETED);

    	assertThat(resultInfos.getContent()).isEqualTo(expectedResults.getSubsistenceFeesMoaFacilities());
        assertThat(resultInfos.getTotalElements()).isEqualTo(expectedResults.getTotal());
    }
    
    @AfterEach
    public void flushAndClear() {
    	entityManager.flush();
        entityManager.clear();
    }
    
    private Pageable getPageable(SubsistenceFeesSearchCriteria criteria) {
        return PageRequest.of(
                criteria.getPaging().getPageNumber().intValue(),
                criteria.getPaging().getPageSize().intValue());
    }
}
