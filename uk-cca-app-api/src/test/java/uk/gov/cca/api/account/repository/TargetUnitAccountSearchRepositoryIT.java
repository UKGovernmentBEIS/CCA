package uk.gov.cca.api.account.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;
import uk.gov.netz.api.account.domain.AccountSearchAdditionalKeyword;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class TargetUnitAccountSearchRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private TargetUnitAccountSearchRepository cut;

    @Autowired
    private EntityManager entityManager;

    @Test
    void searchAccounts() {
    	TargetUnitAccount account1 = createAccount(101L, 1L, TargetUnitAccountStatus.LIVE, LocalDate.of(2015, 11, 3).atStartOfDay(), null, "crn", Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"));
    	createAccountSearchAdditionalKeyword(account1.getId(), "key1", "Operator_1705160674924_NEW");
    	
    	TargetUnitAccount account2 = createAccount(102L, 2L, TargetUnitAccountStatus.LIVE, LocalDate.of(2015, 11, 3).atStartOfDay(), null, "crn", Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"));
    	createAccountSearchAdditionalKeyword(account2.getId(), "key1", "Operator_23232342_NEW");
    	
    	TargetUnitAccount account3 = createAccount(103L, 2L, TargetUnitAccountStatus.LIVE, LocalDate.of(2015, 11, 3).atStartOfDay(), null, "crn", Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"));
    	createAccountSearchAdditionalKeyword(account3.getId(), "key1", "Operator_2323234234");
    	
    	TargetUnitAccount account4 = createAccount(104L, 3L, TargetUnitAccountStatus.LIVE, LocalDate.of(2015, 11, 3).atStartOfDay(), null, "crn", Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"));
    	createAccountSearchAdditionalKeyword(account4.getId(), "key1", "Operator_2323434_NEW");
    	
    	flushAndClear();
    	
    	Page<TargetUnitAccount> result = cut.searchAccounts(PageRequest.of(0, 10, Sort.by("id").descending()), List.of(1L, 2L), "new");
    	
    	assertThat(result.toList()).containsExactly(account2, account1);
    }

    @Test
    void searchAccountsWithSiteContact() {
        Long sectorAssociationId = 1L;
        
		createAccount(101L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2015, 11, 3).atStartOfDay(),
				null, "crn1", Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"));
    	
		createAccount(102L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2016, 11, 3).atStartOfDay(),
				null, "crn2", Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"));

		createAccount(103L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2016, 11, 3).atStartOfDay(),
				null, "crn3", Map.of("cont", "userId1"));

		createAccount(104L, 2L, TargetUnitAccountStatus.LIVE, LocalDate.of(2017, 11, 3).atStartOfDay(), null, "crn4",
				Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"));
        
        String contactType = CcaAccountContactType.TU_SITE_CONTACT;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("tu.id").descending());

        Page<TargetUnitAccountInfoDTO> result = cut.searchAccountsWithSiteContact(pageable, sectorAssociationId, contactType);

		assertThat(result.getContent()).containsExactly(
				TargetUnitAccountInfoDTO.builder().accountId(103L).businessId("businessId" + 103L).accountName("name" + 103L).status(TargetUnitAccountStatus.LIVE).siteContactUserId(null).build(),
				TargetUnitAccountInfoDTO.builder().accountId(102L).businessId("businessId" + 102L).accountName("name" + 102L).status(TargetUnitAccountStatus.LIVE).siteContactUserId("userId1").build(),
				TargetUnitAccountInfoDTO.builder().accountId(101L).businessId("businessId" + 101L).accountName("name" + 101L).status(TargetUnitAccountStatus.LIVE).siteContactUserId("userId1").build()
				);
    }

    @Test
    void searchAccountsWithSiteContactAndAccountsIds() {
    	Long sectorAssociationId = 1L;
        
		createAccount(101L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2015, 11, 3).atStartOfDay(),
				null, "crn1", Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"));
    	
		createAccount(102L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2016, 11, 3).atStartOfDay(),
				null, "crn2", Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"));

		createAccount(103L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2016, 11, 3).atStartOfDay(),
				null, "crn3", Map.of("cont", "userId1"));

		createAccount(104L, 2L, TargetUnitAccountStatus.LIVE, LocalDate.of(2017, 11, 3).atStartOfDay(), null, "crn4",
				Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"));
        
        String contactType = CcaAccountContactType.TU_SITE_CONTACT;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("tu.id").descending());

		Page<TargetUnitAccountInfoDTO> result = cut.searchAccountsWithSiteContactAndAccountsIds(pageable,
				sectorAssociationId, Set.of(101L, 103L), contactType);

		assertThat(result.getContent()).containsExactly(
				TargetUnitAccountInfoDTO.builder().accountId(103L).businessId("businessId" + 103L).accountName("name" + 103L).status(TargetUnitAccountStatus.LIVE).siteContactUserId(null).build(),
				TargetUnitAccountInfoDTO.builder().accountId(101L).businessId("businessId" + 101L).accountName("name" + 101L).status(TargetUnitAccountStatus.LIVE).siteContactUserId("userId1").build()
				);
    }
    
    private TargetUnitAccount createAccount(Long id, Long sectorId, TargetUnitAccountStatus status, 
    		LocalDateTime acceptedDate, LocalDateTime terminationDate, String crn, Map<String, String> contactsMap) {
    	TargetUnitAccount account = TargetUnitAccount.builder()
    		.id(id)
			.businessId("businessId" + id)
			.name("name" + id)
			.companyRegistrationNumber(crn)
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
	        .contacts(contactsMap)
	        .build();
    	entityManager.persist(account);
    	return account;
    }
    
    private AccountSearchAdditionalKeyword createAccountSearchAdditionalKeyword(Long accountId, String key, String val) {
        AccountSearchAdditionalKeyword accountSearchAdditionalKeyword = AccountSearchAdditionalKeyword.builder()
                .accountId(accountId)
                .key(key)
                .value(val)
                .build();

        entityManager.persist(accountSearchAdditionalKeyword);
        return accountSearchAdditionalKeyword;
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
