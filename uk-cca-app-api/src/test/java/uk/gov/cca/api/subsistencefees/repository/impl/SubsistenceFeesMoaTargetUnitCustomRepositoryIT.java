package uk.gov.cca.api.subsistencefees.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaTargetUnit;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaTargetUnitSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaTargetUnitSearchResultsInfo;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class SubsistenceFeesMoaTargetUnitCustomRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
    private SubsistenceFeesMoaTargetUnitCustomRepositoryImpl repository;

    @Autowired
    private EntityManager entityManager;
    
    private final LocalDateTime submissionDate = LocalDateTime.now();

    @BeforeEach
    void setUp() {
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

        // set up accounts
        TargetUnitAccount account1 = TargetUnitAccount.builder()
                .id(1L)
                .businessId("businessId1")
                .name("name1")
                .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .status(TargetUnitAccountStatus.LIVE)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .sectorAssociationId(2L)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .createdBy("user3")
                .creationDate(LocalDateTime.now())
                .address(AccountAddress.builder()
                        .line1("123 Test Street")
                        .city("Test City")
                        .postcode("12345")
                        .country("Test Country")
                        .build())
                .build();
        entityManager.persist(account1);

        TargetUnitAccount account2 = TargetUnitAccount.builder()
                .id(2L)
                .businessId("businessId2")
                .name("name2")
                .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .status(TargetUnitAccountStatus.LIVE)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .sectorAssociationId(2L)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .createdBy("user3")
                .creationDate(LocalDateTime.now())
                .address(AccountAddress.builder()
                        .line1("123 Test Street")
                        .city("Test City")
                        .postcode("12345")
                        .country("Test Country")
                        .build())
                .build();
        entityManager.persist(account2);

        TargetUnitAccount account3 = TargetUnitAccount.builder()
                .id(3L)
                .businessId("businessId3")
                .name("name3")
                .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .status(TargetUnitAccountStatus.LIVE)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .sectorAssociationId(2L)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .createdBy("user3")
                .creationDate(LocalDateTime.now())
                .address(AccountAddress.builder()
                        .line1("123 Test Street")
                        .city("Test City")
                        .postcode("12345")
                        .country("Test Country")
                        .build())
                .build();
        entityManager.persist(account3);

        // set up facilities
        FacilityData facilityData1 = FacilityData.builder()
                .facilityBusinessId("term")
                .accountId(account1.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("site1")
                .schemeExitDate(null)
                .chargeStartDate(LocalDate.of(2025, 1, 1))
                .address(address1)
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(facilityData1);

        FacilityData facilityData2 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00015")
                .accountId(account1.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil2")
                .address(address2)
                .schemeExitDate(null)
                .chargeStartDate(LocalDate.of(2024, 12, 29))
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(facilityData2);

        FacilityData facilityData3 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00016")
                .accountId(account2.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("terminal3")
                .address(address3)
                .chargeStartDate(LocalDate.of(2024, 1, 1))
                .closedDate(LocalDateTime.of(2024, 8, 1, 12, 0))
                .schemeExitDate(LocalDate.of(2024, 8, 1))
                .createdDate(LocalDateTime.of(2024, 8, 5, 12, 0))
                .build();
        entityManager.persist(facilityData3);

        FacilityData facilityData4 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00017")
                .accountId(account2.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("terminal3")
                .address(address4)
                .chargeStartDate(LocalDate.of(2024, 1, 1))
                .schemeExitDate(LocalDate.of(2024, 8, 1))
                .closedDate(LocalDateTime.of(2024, 8, 1, 12, 0))
                .createdDate(LocalDateTime.of(2024, 8, 5, 12, 0))
                .build();
        entityManager.persist(facilityData4);

        FacilityData facilityData5 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00018")
                .accountId(account3.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil5")
                .address(address5)
                .schemeExitDate(null)
                .chargeStartDate(LocalDate.of(2024, 12, 29))
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(facilityData5);
        
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

        // Set up MoA target units
        SubsistenceFeesMoaTargetUnit tu1 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa1)
                .accountId(account1.getId())
                .initialTotalAmount(BigDecimal.valueOf(370L))
                .build();
        entityManager.persist(tu1);
        SubsistenceFeesMoaTargetUnit tu2 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa1)
                .accountId(account2.getId())
                .initialTotalAmount(BigDecimal.valueOf(370L))
                .build();
        entityManager.persist(tu2);
        SubsistenceFeesMoaTargetUnit tu3 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa1)
                .accountId(account3.getId())
                .initialTotalAmount(BigDecimal.valueOf(185L))
                .build();
        entityManager.persist(tu3);

        // Set up MoA facilities
        SubsistenceFeesMoaFacility facility1 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu1)
                .facilityId(facilityData1.getId())
                .initialAmount(BigDecimal.valueOf(185L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .build();
        entityManager.persist(facility1);
        SubsistenceFeesMoaFacility facility2 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu1)
                .facilityId(facilityData2.getId())
                .initialAmount(BigDecimal.valueOf(185L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility2);
        SubsistenceFeesMoaFacility facility3 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu2)
                .facilityId(facilityData3.getId())
                .initialAmount(BigDecimal.valueOf(185L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .build();
        entityManager.persist(facility3);
        SubsistenceFeesMoaFacility facility4 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu2)
                .facilityId(facilityData4.getId())
                .initialAmount(BigDecimal.valueOf(185L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .build();
        entityManager.persist(facility4);
        SubsistenceFeesMoaFacility facility5 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu3)
                .facilityId(facilityData5.getId())
                .initialAmount(BigDecimal.valueOf(185L))
                .paymentStatus(FacilityPaymentStatus.CANCELLED)
                .build();
        entityManager.persist(facility5);

        flushAndClear();
    }

    @Test
    void findBySearchCriteria_noCriteria() {
    	PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0).pageSize(50).build();
    	SubsistenceFeesSearchCriteria criteria = SubsistenceFeesSearchCriteria.builder()
    			.paging(pagingRequest)
    			.build();
    	
    	SubsistenceFeesMoaTargetUnitSearchResultInfo resultInfo1 = new SubsistenceFeesMoaTargetUnitSearchResultInfo(
    			1L, "businessId1", "name1", new BigDecimal("370.00"), new BigDecimal("185.00"));
    	SubsistenceFeesMoaTargetUnitSearchResultInfo resultInfo2 = new SubsistenceFeesMoaTargetUnitSearchResultInfo(
    			2L, "businessId2", "name2", new BigDecimal("370.00"), BigDecimal.ZERO);
    	SubsistenceFeesMoaTargetUnitSearchResultInfo resultInfo3 = new SubsistenceFeesMoaTargetUnitSearchResultInfo(
    			3L, "businessId3", "name3", BigDecimal.ZERO, BigDecimal.ZERO);
    	SubsistenceFeesMoaTargetUnitSearchResultsInfo expectedResults = SubsistenceFeesMoaTargetUnitSearchResultsInfo.builder()
    			.subsistenceFeesMoaTargetUnitSearchResultInfo(List.of(resultInfo1, resultInfo2, resultInfo3))
    			.total(3L)
    			.build();

    	SubsistenceFeesMoaTargetUnitSearchResultsInfo resultInfos = repository.findBySearchCriteria(1L, criteria);

        assertThat(resultInfos).isEqualTo(expectedResults);
    }
    
    @Test
    void findBySearchCriteria_term_in_facility_code() {
    	PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0).pageSize(50).build();
    	SubsistenceFeesSearchCriteria criteria = SubsistenceFeesSearchCriteria.builder()
    			.paging(pagingRequest)
    			.term("ter")
    			.build();
    	
    	SubsistenceFeesMoaTargetUnitSearchResultInfo resultInfo1 = new SubsistenceFeesMoaTargetUnitSearchResultInfo(
    			4L, "businessId1", "name1", new BigDecimal("370.00"), new BigDecimal("185.00"));
    	SubsistenceFeesMoaTargetUnitSearchResultsInfo expectedResults = SubsistenceFeesMoaTargetUnitSearchResultsInfo.builder()
    			.subsistenceFeesMoaTargetUnitSearchResultInfo(List.of(resultInfo1))
    			.total(1L)
    			.build();

    	SubsistenceFeesMoaTargetUnitSearchResultsInfo resultInfos = repository.findBySearchCriteria(2L, criteria);

        assertThat(resultInfos).isEqualTo(expectedResults);
    }
    
    @Test
    void findBySearchCriteria_facilityStatus() {
    	PagingRequest pagingRequest = PagingRequest.builder().pageNumber(0).pageSize(50).build();
    	// Find canceled only
    	SubsistenceFeesSearchCriteria criteria1 = SubsistenceFeesSearchCriteria.builder()
    			.paging(pagingRequest)
    			.markFacilitiesStatus(FacilityPaymentStatus.CANCELLED)
    			.build();
    	
    	SubsistenceFeesMoaTargetUnitSearchResultInfo resultInfo1 = new SubsistenceFeesMoaTargetUnitSearchResultInfo(
    			9L, "businessId3", "name3", BigDecimal.ZERO, BigDecimal.ZERO);
    	SubsistenceFeesMoaTargetUnitSearchResultsInfo expectedResults1 = SubsistenceFeesMoaTargetUnitSearchResultsInfo.builder()
    			.subsistenceFeesMoaTargetUnitSearchResultInfo(List.of(resultInfo1))
    			.total(1L)
    			.build();

    	SubsistenceFeesMoaTargetUnitSearchResultsInfo resultInfos1 = repository.findBySearchCriteria(3L, criteria1);

        assertThat(resultInfos1).isEqualTo(expectedResults1);
        
        // Find in progress only
    	SubsistenceFeesSearchCriteria criteria2 = SubsistenceFeesSearchCriteria.builder()
    			.paging(pagingRequest)
    			.markFacilitiesStatus(FacilityPaymentStatus.IN_PROGRESS)
    			.build();
    	
    	SubsistenceFeesMoaTargetUnitSearchResultInfo resultInfo2 = new SubsistenceFeesMoaTargetUnitSearchResultInfo(
    			7L, "businessId1", "name1", new BigDecimal("370.00"), new BigDecimal("185.00"));
    	SubsistenceFeesMoaTargetUnitSearchResultsInfo expectedResults2 = SubsistenceFeesMoaTargetUnitSearchResultsInfo.builder()
    			.subsistenceFeesMoaTargetUnitSearchResultInfo(List.of(resultInfo2))
    			.total(1L)
    			.build();

    	SubsistenceFeesMoaTargetUnitSearchResultsInfo resultInfos2 = repository.findBySearchCriteria(3L, criteria2);

        assertThat(resultInfos2).isEqualTo(expectedResults2);
        
        // Find completed only
    	SubsistenceFeesSearchCriteria criteria3= SubsistenceFeesSearchCriteria.builder()
    			.paging(pagingRequest)
    			.markFacilitiesStatus(FacilityPaymentStatus.COMPLETED)
    			.build();
    	
    	SubsistenceFeesMoaTargetUnitSearchResultInfo resultInfo3 = new SubsistenceFeesMoaTargetUnitSearchResultInfo(
    			8L, "businessId2", "name2", new BigDecimal("370.00"), BigDecimal.ZERO);
    	SubsistenceFeesMoaTargetUnitSearchResultsInfo expectedResults3 = SubsistenceFeesMoaTargetUnitSearchResultsInfo.builder()
    			.subsistenceFeesMoaTargetUnitSearchResultInfo(List.of(resultInfo3))
    			.total(1L)
    			.build();

    	SubsistenceFeesMoaTargetUnitSearchResultsInfo resultInfos3 = repository.findBySearchCriteria(3L, criteria3);

        assertThat(resultInfos3).isEqualTo(expectedResults3);
    }
    
    @AfterEach
    void flushAndClear() {
    	entityManager.flush();
        entityManager.clear();
    }
}
