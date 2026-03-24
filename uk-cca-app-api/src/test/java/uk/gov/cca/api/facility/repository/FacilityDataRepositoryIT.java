package uk.gov.cca.api.facility.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class FacilityDataRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private FacilityDataRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
    	AccountAddress addr1 = AccountAddress.builder()
                .line1("123 Test Street")
                .city("Test City")
                .postcode("12345")
                .country("Test Country")
                .build();
            entityManager.persist(addr1);

        AccountAddress addr2 = AccountAddress.builder()
                .line1("456 Test Avenue")
                .city("Another City")
                .postcode("67890")
                .country("Another Country")
                .build();
            entityManager.persist(addr2);
            
        AccountAddress addr3 = AccountAddress.builder()
                .line1("456 Test Avenue")
                .city("Another City")
                .postcode("67890")
                .country("Another Country")
                .build();
            entityManager.persist(addr3);
            
    	TargetUnitAccount account1 = TargetUnitAccount.builder()
    			.id(1L)
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
    	entityManager.persist(account1);
    	
    	TargetUnitAccount account2 = TargetUnitAccount.builder()
    			.id(100L)
    			.businessId("businessId2")
    			.name("name2")
    			.status(TargetUnitAccountStatus.LIVE)
    			.emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
    			.competentAuthority(CompetentAuthorityEnum.ENGLAND)
    			.operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
    			.financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .sectorAssociationId(1L)
                .address(addr2)
    			.build();
    	entityManager.persist(account2);
    	
    	TargetUnitAccount account3 = TargetUnitAccount.builder()
    			.id(200L)
    			.businessId("businessId3")
    			.name("name3")
    			.status(TargetUnitAccountStatus.LIVE)
    			.emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
    			.competentAuthority(CompetentAuthorityEnum.ENGLAND)
    			.operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
    			.financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .sectorAssociationId(1L)
                .address(addr3)
    			.build();
    	entityManager.persist(account3);
    	
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

        final FacilityData facility1 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00014")
                .accountId(1L)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("site1")
                .address(address1)
                .createdDate(LocalDateTime.now())
                .build();
        entityManager.persist(facility1);

        final FacilityData facility2 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00015")
                .accountId(2L)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil2")
                .address(address2)
                .createdDate(LocalDateTime.now())
                .build();
        entityManager.persist(facility2);

        final FacilityData facility3 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00016")
                .accountId(1L)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("terminal3")
                .address(address3)
                .closedDate(LocalDateTime.of(2024, 8, 1, 12, 0))
                .createdDate(LocalDateTime.of(2023, 8, 1, 12, 0))
                .build();
        entityManager.persist(facility3);
        
        final FacilityData facility4 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00017")
                .accountId(100L)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3))
                .siteName("terminal3")
                .address(address4)
                .createdDate(LocalDateTime.of(2023, 8, 1, 12, 0))
                .build();
        entityManager.persist(facility4);
        
        final FacilityData facility5 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00017")
                .accountId(100L)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                .siteName("terminal3")
                .address(address5)
                .createdDate(LocalDateTime.of(2023, 8, 1, 12, 0))
                .build();
        entityManager.persist(facility5);

        flushAndClear();
    }

    @Test
    void existsByFacilityBusinessId() {
        final String facilityBusinessId = "ADS_1-F00016";

        boolean exists = repository.existsByFacilityBusinessId(facilityBusinessId);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByFacilityBusinessIdAndClosedDateIsNull() {
        final String facilityBusinessId = "ADS_1-F00016";

        boolean exists = repository.existsByFacilityBusinessIdAndClosedDateIsNull(facilityBusinessId);

        assertThat(exists).isFalse();
    }

    @Test
    void findAllByFacilityBusinessIdIn() {
        final String facilityBusinessId1 = "ADS_1-F00015";
        final String facilityBusinessId2 = "ADS_1-F00016";

        List<FacilityData> facilities = repository.findAllByFacilityBusinessIdIn(Set.of(facilityBusinessId1, facilityBusinessId2));

        assertThat(facilities).hasSize(2);
        assertThat(facilities.stream().map(FacilityData::getFacilityBusinessId).collect(Collectors.toSet()))
                .isEqualTo(Set.of(facilityBusinessId1, facilityBusinessId2));
    }

    @Test
    void findFacilityDataByAccountIdAndClosedDateIsNull() {
        List<FacilityData> facilities = repository.findFacilityDataByAccountIdAndClosedDateIsNull(1L);

        assertThat(facilities).hasSize(1);
        assertThat(facilities.getFirst().getFacilityBusinessId()).isEqualTo("ADS_1-F00014");
    }

    @Test
    void searchFacilityDataByAccountIdAndTerm() {
        final int pageSize = 30;
        final Pageable pageable = PageRequest.of(0, pageSize, Sort.by("facilityBusinessId"));

        Page<FacilityData> results = repository.searchFacilityDataByAccountIdAndTerm(pageable, 1L, "term");

        List<String> facilities = results.stream().map(FacilityData::getFacilityBusinessId).toList();
        assertThat(facilities).hasSize(1);
        assertThat(facilities.getFirst()).isEqualTo("ADS_1-F00016");
    }

    @Test
    void findByFacilityBusinessIdAndClosedDateIsNull() {

        Optional<FacilityData> facilityDataOptional = repository.findByFacilityBusinessIdAndClosedDateIsNull("ADS_1-F00014");

        assertThat(facilityDataOptional).isPresent();
        assertThat(facilityDataOptional.get().getFacilityBusinessId()).isEqualTo("ADS_1-F00014");
        assertThat(facilityDataOptional.get().getClosedDate()).isNull();
        assertThat(facilityDataOptional.get().getParticipatingSchemeVersions())
                .isEqualTo(Set.of(SchemeVersion.CCA_2));
    }
    
    @Test
    void findLiveAccountsWithAtLeastOneFacilityForSchemeVersionOnly() {

    	TargetUnitAccountBusinessInfoDTO result1 = TargetUnitAccountBusinessInfoDTO.builder()
    			.accountId(1L)
    			.businessId("businessId")
    			.name("name")
    			.build();
    	TargetUnitAccountBusinessInfoDTO result2 = TargetUnitAccountBusinessInfoDTO.builder()
    			.accountId(100L)
    			.businessId("businessId2")
    			.name("name2")
    			.build();
    	List<TargetUnitAccountBusinessInfoDTO> accounts = repository.findLiveAccountsWithActiveFacilityForSchemeVersion(SchemeVersion.CCA_2.name());

        assertThat(accounts).hasSize(2).containsExactlyInAnyOrder(result1, result2);
    }

    @Test
    void findAllByAccountId() {

        List<FacilityData> facilityDataList = repository.findAllByAccountId(1L);

        assertThat(facilityDataList).hasSize(2);
        assertThat(facilityDataList.getFirst().getAccountId()).isEqualTo(1L);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
        flushAndClear();
    }


    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
