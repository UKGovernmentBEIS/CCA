package uk.gov.cca.api.account.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaAccountContactType;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class TargetUnitAccountRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private TargetUnitAccountRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        AccountAddress address1 = AccountAddress.builder()
            .line1("123 Test Street")
            .city("Test City")
            .postcode("12345")
            .country("Test Country")
            .build();
        entityManager.persist(address1);

        AccountAddress address2 = AccountAddress.builder()
            .line1("456 Test Avenue")
            .city("Another City")
            .postcode("67890")
            .country("Another Country")
            .build();
        entityManager.persist(address2);

        AccountAddress address3 = AccountAddress.builder()
            .line1("789 Test Road")
            .city("Third City")
            .postcode("54321")
            .country("Third Country")
            .build();
        entityManager.persist(address3);

        TargetUnitAccount account1 = TargetUnitAccount.builder()
            .id(1L)
            .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(TargetUnitAccountStatus.LIVE)
            .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
            .sectorAssociationId(1L)
            .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
            .createdBy("user1")
            .creationDate(LocalDateTime.now())
            .address(address1)
            .businessId("businessId1")
            .name("Target Unit Account 1")
            .contacts(Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"))
            .build();
        entityManager.merge(account1);

        TargetUnitAccount account2 = TargetUnitAccount.builder()
            .id(2L)
            .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(TargetUnitAccountStatus.LIVE)
            .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
            .sectorAssociationId(1L)
            .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
            .createdBy("user2")
            .creationDate(LocalDateTime.now())
            .address(address2)
            .businessId("businessId2")
            .name("Target Unit Account 2")
            .contacts(Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId2"))
            .build();
        entityManager.merge(account2);

        TargetUnitAccount account3 = TargetUnitAccount.builder()
            .id(3L)
            .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(TargetUnitAccountStatus.NEW)
            .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
            .sectorAssociationId(2L)
            .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
            .createdBy("user3")
            .creationDate(LocalDateTime.now())
            .address(address3)
            .businessId("businessId3")
            .name("Target Unit Account 3")
            .build();
        entityManager.merge(account3);

        flushAndClear();
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
        flushAndClear();
    }

    @Test
    void testFindTargetUnitAccountContactsByCaAndContactTypeAndStatusNotIn() {
        Long sectorAssociationId = 1L;
        
        String contactType = CcaAccountContactType.TU_SITE_CONTACT;
        PageRequest pageable = PageRequest.of(0, 10);

        Page<TargetUnitAccountInfoDTO> result = repository.findTargetUnitAccountsWithSiteContact(pageable, sectorAssociationId, contactType);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    void testFindAllByIdIn() {
        List<Long> ids = List.of(1L, 2L, 3L);

        List<TargetUnitAccount> result = repository.findAllByIdIn(ids);

        assertThat(result).isNotNull().hasSize(ids.size());
    }

    @Test
    void testFindAllIdsBySectorAssociationId() {

    	Long sectorAssociationId = 1L;

        List<Long> result = repository.findAllIdsBySectorAssociationId(sectorAssociationId);

        assertThat(result).isNotNull().hasSize(2);
    }

    @Test
    void findTargetUnitAccountWithSiteContactAndAccountsIds() {
        Long sectorAssociationId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Set<Long> accountsIds = Set.of(1L, 2L, 3L);

        String contactType = CcaAccountContactType.TU_SITE_CONTACT;

        Page<TargetUnitAccountInfoDTO> result = repository
                .findTargetUnitAccountWithSiteContactAndAccountsIds(pageable, sectorAssociationId,
                        accountsIds, contactType);


        assertThat(result.getContent())
                .hasSize(2)
                .extracting(TargetUnitAccountInfoDTO::getAccountId)
                .containsExactlyInAnyOrder(1L, 2L);
    }
    
    @Test
    void findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedTerminatedBetween() {
    	//TP6 2016
    	LocalDateTime acceptedDate = LocalDate.of(2016, 12, 31).atTime(LocalTime.MAX);
    	LocalDateTime terminatedDateFrom = LocalDate.of(2016, 1, 1).atTime(LocalTime.MIN);
    	LocalDateTime terminatedDateTo = LocalDate.of(2017, 5, 1).atTime(LocalTime.MIN);
    	
    	Long sectorAssociationId = 1L;
        
        TargetUnitAccount account1 = createAccount(-1L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2015, 11, 3).atStartOfDay(), null);
        TargetUnitAccount account2 = createAccount(-2L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2016, 11, 3).atStartOfDay(), null);
        createAccount(-3L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2018, 11, 3).atStartOfDay(), null);
        createAccount(-4L, sectorAssociationId, TargetUnitAccountStatus.NEW, LocalDate.of(2016, 11, 3).atStartOfDay(), null);
        createAccount(-5L, -1l, TargetUnitAccountStatus.LIVE, LocalDate.of(2016, 11, 3).atStartOfDay(), null);
        TargetUnitAccount account6 = createAccount(-6L, sectorAssociationId, TargetUnitAccountStatus.TERMINATED, LocalDate.of(2016, 11, 3).atStartOfDay(), LocalDate.of(2016, 12, 3).atStartOfDay());
        createAccount(-7L, sectorAssociationId, TargetUnitAccountStatus.TERMINATED, LocalDate.of(2016, 11, 3).atStartOfDay(), LocalDate.of(2017, 5, 3).atStartOfDay());
    	
    	flushAndClear();
    	
    	List<TargetUnitAccountBusinessInfoDTO> result = repository.findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedBetween(sectorAssociationId,
				acceptedDate, terminatedDateFrom, terminatedDateTo);
    	
		assertThat(result).extracting(TargetUnitAccountBusinessInfoDTO::getAccountId).containsExactly(account1.getId(),
				account2.getId(), account6.getId());
    	
    }

    private TargetUnitAccount createAccount(Long id, Long sectorId, TargetUnitAccountStatus status, LocalDateTime acceptedDate, LocalDateTime terminationDate) {
    	TargetUnitAccount account = TargetUnitAccount.builder()
    		.id(id)
			.businessId("businessId" + id)
			.name("name" + id)
	        .sectorAssociationId(sectorId)
	        .status(status)
	        .acceptedDate(acceptedDate)
	        .terminatedDate(terminationDate)
	        .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
	        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
	        .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
	        .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
	        .createdBy("user1")
	        .address(createAddress())
	        .contacts(Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"))
	        .build();
    	entityManager.persist(account);
    	return account;
    }
    
	private AccountAddress createAddress() {
		AccountAddress address = AccountAddress.builder().line1("123 Test Street").city("Test City").postcode("12345")
				.country("Test Country").build();
		entityManager.persist(address);
		return address;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
