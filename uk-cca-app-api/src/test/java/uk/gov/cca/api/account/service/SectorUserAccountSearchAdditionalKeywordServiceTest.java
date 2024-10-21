package uk.gov.cca.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.authorization.ccaauth.core.service.AppUserService;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchKeywordResultsDTO;
import uk.gov.netz.api.account.domain.dto.AccountSearchResultInfoDTO;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.account.transform.AccountSearchResultMapper;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SectorUserAccountSearchAdditionalKeywordServiceTest {

    @InjectMocks
    private SectorUserAccountSearchAdditionalKeywordService service;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Mock
    private AppUserService appUserService;

    @Mock
    private AccountSearchResultMapper accountSearchResultMapper;

    @Mock
    private CcaAccountSearchService accountSearchService;


    @Test
    void getUserAccountsBySearchCriteriaTest() {
        final Long sectorId_1 = 1L;
        final Long sectorId_2 = 2L;


        final AppUser appUser = AppUser.builder()
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .accountId(1L)
                        .build()))
                .build();

        final TargetUnitAccount account1 = buildAccount(1L, "Account_1", "business_id_1", TargetUnitAccountStatus.NEW, sectorId_1);
        final TargetUnitAccount account2 = buildAccount(2L, "Account_2", "business_id_2", TargetUnitAccountStatus.LIVE, sectorId_1);
        final TargetUnitAccount account3 = buildAccount(3L, "Account_3", "business_id_3", TargetUnitAccountStatus.LIVE, sectorId_2);

        final AccountSearchCriteria searchCriteria = getSearchCriteria();
        final List<Long> matchedAccountIds = Arrays.asList(1L, 2L, 3L);
        final Set<Long> sectorAssociationIds = Set.of(sectorId_1, sectorId_2);

        final AccountSearchResultInfoDTO accountSearchResultInfoDTO1 =
                new AccountSearchResultInfoDTO(account1.getId(), account1.getName(), account1.getBusinessId(), account1.getStatus());

        final AccountSearchResultInfoDTO accountSearchResultInfoDTO2 =
                new AccountSearchResultInfoDTO(account2.getId(), account2.getName(), account2.getBusinessId(), account2.getStatus());

        final AccountSearchResultInfoDTO accountSearchResultInfoDTO3 =
                new AccountSearchResultInfoDTO(account3.getId(), account3.getName(), account3.getBusinessId(), account3.getStatus());


        final AccountSearchKeywordResultsDTO accountSearchKeywordResultsDTO =
                AccountSearchKeywordResultsDTO.builder().accountIds(matchedAccountIds).total((long) matchedAccountIds.size()).build();

        when(appUserService.getUserSectorAssociations(appUser)).thenReturn(sectorAssociationIds);
        when(targetUnitAccountQueryService.getAccounts(List.of(account1.getId(), account2.getId(), account3.getId())))
                .thenReturn(List.of(account1, account2, account3));
        when(accountSearchResultMapper.toAccountInfoDTO(account1)).thenReturn(accountSearchResultInfoDTO1);
        when(accountSearchResultMapper.toAccountInfoDTO(account2)).thenReturn(accountSearchResultInfoDTO2);
        when(accountSearchResultMapper.toAccountInfoDTO(account3)).thenReturn(accountSearchResultInfoDTO3);
        when(accountSearchService.searchAccounts(sectorAssociationIds.stream().toList(), searchCriteria))
                .thenReturn(accountSearchKeywordResultsDTO);

        // invoke
        final AccountSearchResults results = service.getUserAccountsBySearchCriteria(appUser, getSearchCriteria());

        // verify
        verify(appUserService, times(1)).getUserSectorAssociations(appUser);
        verify(accountSearchService, times(1)).searchAccounts(sectorAssociationIds.stream().toList(), searchCriteria);
        verify(accountSearchResultMapper, times(3)).toAccountInfoDTO(any());
        assertThat(results.getTotal()).isEqualTo(3);
        assertThat(results.getAccounts().get(0).getName()).isEqualTo(account1.getName());
        assertThat(results.getAccounts().get(1).getName()).isEqualTo(account2.getName());
        assertThat(results.getAccounts().get(2).getName()).isEqualTo(account3.getName());
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

    private AccountSearchCriteria getSearchCriteria() {
        String term = "NEW";
        final PagingRequest pageRequest = PagingRequest.builder()
                .pageSize(5L)
                .pageNumber(0L)
                .build();
        return AccountSearchCriteria.builder()
                .term(term)
                .paging(pageRequest)
                .build();
    }
}
