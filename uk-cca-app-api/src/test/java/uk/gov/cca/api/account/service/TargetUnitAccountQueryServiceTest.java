package uk.gov.cca.api.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountHeaderInfoDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.account.transform.AccountAddressMapper;
import uk.gov.cca.api.account.transform.TargetUnitAccountMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountQueryServiceTest {

	@InjectMocks
    private TargetUnitAccountQueryService service;

    @Mock
    private TargetUnitAccountRepository repository;
    
	private static final TargetUnitAccountMapper targetUnitAccountMapper = Mappers.getMapper(TargetUnitAccountMapper.class);
	private static final AccountAddressMapper accountAddressMapper = Mappers.getMapper(AccountAddressMapper.class);
    
	@BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "targetUnitAccountMapper", targetUnitAccountMapper);
        ReflectionTestUtils.setField(targetUnitAccountMapper, "accountAddressMapper", accountAddressMapper);
    }
	
	@Test
    void getAccountsByIds() {
        Long accountId = 1L;
        List<Long> accountIds = List.of(accountId);
        LocalDateTime now = LocalDateTime.now();
        TargetUnitAccount account = TargetUnitAccount.builder()
                .name("name")
                .subsectorAssociationId(1L)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .creationDate(now)
                .sectorAssociationId(123L)
                .status(TargetUnitAccountStatus.NEW)
                .build();

        when(repository.findAllByIdIn(accountIds)).thenReturn(List.of(account));

        TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
                .name("name")
                .subsectorAssociationId(1L)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .creationDate(now)
                .sectorAssociationId(123L)
                .status(TargetUnitAccountStatus.NEW)
                .build();

        //invoke
        List<TargetUnitAccountDTO> actualList = service.getAccountsByIds(accountIds);

        //verify
        assertThat(actualList).containsExactly(accountDTO);
        verify(repository, times(1)).findAllByIdIn(accountIds);
    }

    @Test
    void getSectorAssociationIdsByAccountIds() {
        Long accountId = 1L;
        List<Long> accountIds = List.of(accountId);
        LocalDateTime now = LocalDateTime.now();
        TargetUnitAccount account = TargetUnitAccount.builder()
                .name("name")
                .subsectorAssociationId(1L)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .creationDate(now)
                .sectorAssociationId(123L)
                .status(TargetUnitAccountStatus.NEW)
                .build();

        when(repository.findAllByIdIn(accountIds)).thenReturn(List.of(account));

        Set<Long> results = service.getSectorAssociationIdsByAccountIds(accountIds);

        verify(repository, times(1)).findAllByIdIn(accountIds);

        assertEquals(Set.of(123L), results);
    }

    @Test
    void getAccountsTest() {
        final TargetUnitAccount account1 = buildAccount(1L, "Account_1", "business_id_1", TargetUnitAccountStatus.NEW, 1L);
        final TargetUnitAccount account2 = buildAccount(2L, "Account_2", "business_id_2", TargetUnitAccountStatus.LIVE, 2L);

        final List<Long> accountIds = List.of(account1.getId(), account2.getId());
        when(repository.findAllByIdIn(accountIds)).thenReturn(List.of(account1, account2));

        // invoke
        final List<TargetUnitAccount> results = service.getAccounts(accountIds);

        // verify
        assertThat(results).hasSize(2);
        assertThat(results.getFirst().getName()).isEqualTo(account1.getName());

        verify(repository, times(1)).findAllByIdIn(accountIds);
    }

    @Test
    void getAllTargetUnitAccountIdsBySectorAssociationId() {
        Long sectorAssociationId = 1L;
        List<Long> expectedIds = List.of(1L, 2L, 3L);

        when(repository.findAllIdsBySectorAssociationId(sectorAssociationId))
                .thenReturn(expectedIds);

        List<Long> result = service.getAllTargetUnitAccountIdsBySectorAssociationId(sectorAssociationId);

        assertEquals(expectedIds, result);

        verify(repository).findAllIdsBySectorAssociationId(sectorAssociationId);
    }
    
    @Test
    void getAccountName() {
        final Long accountId = 1L;
        final String name = "Test Account";
        final TargetUnitAccount targetUnitAccount =
                buildAccount(accountId, name, "dfdf", TargetUnitAccountStatus.LIVE, 2L);

        when(repository.findById(accountId)).thenReturn(Optional.of(targetUnitAccount));

        final String accountName = service.getAccountName(accountId);

        assertEquals("Test Account", accountName);
        verify(repository).findById(accountId);
    }
    
    @Test
    void getAccountName_NotFound() {
        final Long accountId = 1L;
        
        when(repository.findById(accountId)).thenReturn(Optional.empty());

        BusinessException thrown = assertThrows(
                BusinessException.class,
                () -> service.getAccountName(accountId),
                "Expected getAccountName to throw, but it didn't"
        );

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, thrown.getErrorCode());
        verify(repository).findById(accountId);
    }

    @Test
    void getAccountBusinessIdAndName() {
        final Long accountId = 1L;
        final String businessId = "AIC/800544";
        final String name = "Test Account";
        final TargetUnitAccount targetUnitAccount =
                buildAccount(accountId, name, businessId, TargetUnitAccountStatus.LIVE, 1L);

        when(repository.findById(accountId)).thenReturn(Optional.of(targetUnitAccount));

        final String accountName = service.getAccountBusinessIdAndName(accountId);

        assertEquals("AIC/800544 - Test Account", accountName);
        verify(repository).findById(accountId);
    }

    @Test
    void getAccountBusinessIdAndName_NotFound() {
        final Long accountId = 1L;

        BusinessException thrown = assertThrows(
                BusinessException.class,
                () -> service.getAccountBusinessIdAndName(accountId),
                "Expected getAccountName to throw, but it didn't"
        );

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, thrown.getErrorCode());
        verify(repository).findById(accountId);
    }

    @Test
    void getTargetUnitHeaderInfoDTO() {
        final Long accountId = 1L;
        final String businessId = "AIC/800544";
        final String name = "Test Account";
        final TargetUnitAccountStatus status = TargetUnitAccountStatus.LIVE;
        final TargetUnitAccount targetUnitAccount =
                buildAccount(accountId, name, businessId, status, 1L);

        when(repository.findById(accountId)).thenReturn(Optional.of(targetUnitAccount));

        final TargetUnitAccountHeaderInfoDTO targetUnitAccountHeaderInfoDTO = service.getTargetUnitAccountHeaderInfo(accountId);

        assertEquals(name, targetUnitAccountHeaderInfoDTO.getName());
        assertEquals(businessId, targetUnitAccountHeaderInfoDTO.getBusinessId());
        assertEquals(status, targetUnitAccountHeaderInfoDTO.getStatus());
        verify(repository).findById(accountId);

    }

    @Test
    void findTargetUnitAccountContactsWithAccountId() {
        Long accountId = 1L;
        final List<NoticeRecipientDTO> list = new ArrayList<>();

        final NoticeRecipientDTO responsiblePerson = NoticeRecipientDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.RESPONSIBLE_PERSON)
                .build();

        final NoticeRecipientDTO administrativeContact = NoticeRecipientDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                .build();

        list.add(responsiblePerson);
        list.add(administrativeContact);

        when(repository.findTargetUnitAccountNoticeRecipientsByAccountId(accountId))
                .thenReturn(list);

        List<NoticeRecipientDTO> result = service.getTargetUnitAccountNoticeRecipientsByAccountId(accountId);

        assertEquals(list, result);

        verify(repository).findTargetUnitAccountNoticeRecipientsByAccountId(accountId);
    }
    
    @Test
    void findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedDuringActivatedYearOrTerminatedBetween() {
    	Long sectorAssociationId = 1L;
    	LocalDateTime acceptedDate = LocalDateTime.now();
    	LocalDateTime terminatedDateFrom = LocalDateTime.now();
		LocalDateTime terminatedDateTo = LocalDateTime.now();
		
		List<TargetUnitAccountBusinessInfoDTO> accounts = List.of(
				TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("1").build()
				);
		
		when(repository.findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedBetween(
				sectorAssociationId, acceptedDate, terminatedDateFrom, terminatedDateTo)).thenReturn(accounts);
		
		var result = service
				.findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedDuringActivatedYearOrTerminatedBetween(
						sectorAssociationId, acceptedDate, terminatedDateFrom, terminatedDateTo);
		
		assertThat(result).containsExactlyElementsOf(accounts);
		
		verify(repository, times(1)).findAllTargetUnitAccountsActivatedBeforeWithStatusActiveOrTerminatedBetween(
				sectorAssociationId, acceptedDate, terminatedDateFrom, terminatedDateTo);
    }

    private TargetUnitAccount buildAccount(Long id, String accountName, String businessId, TargetUnitAccountStatus status, Long sectorAssociationId) {
        return TargetUnitAccount.builder()
                .id(id)
                .status(status)
                .sectorAssociationId(sectorAssociationId)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .name(accountName)
                .businessId(businessId)
                .build();
    }
}
