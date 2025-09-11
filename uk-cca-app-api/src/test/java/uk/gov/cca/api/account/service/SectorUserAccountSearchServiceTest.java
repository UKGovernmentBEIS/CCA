package uk.gov.cca.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.authorization.ccaauth.core.service.AppUserService;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchResultInfoDTO;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectorUserAccountSearchServiceTest {

    @InjectMocks
    private SectorUserAccountSearchService service;

    @Mock
    private CcaAccountSearchService accountSearchService;

    @Mock
    private AppUserService appUserService;

    @Test
    void getUserAccountsBySearchCriteria() {
        final AppUser appUser = AppUser.builder()
                .authorities(List.of(AppAuthority.builder()
                        .verificationBodyId(1L)
                        .build()))
                .build();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("NEW")
                .paging(PagingRequest.builder()
                        .pageSize(5)
                        .pageNumber(0)
                        .build())
                .build();

        final AccountSearchResultInfoDTO accountSearchResultInfoDTO1 =
                new AccountSearchResultInfoDTO(1L, "acc1", "bus1", TargetUnitAccountStatus.NEW);
        
        final AccountSearchResultInfoDTO accountSearchResultInfoDTO2 =
                new AccountSearchResultInfoDTO(2L, "acc2", "bus2", TargetUnitAccountStatus.NEW);
        
        Set<Long> sectorAssociationIds = Set.of(1L);
        
        AccountSearchResults expected = AccountSearchResults.builder()
		.accounts(List.of(accountSearchResultInfoDTO1, accountSearchResultInfoDTO2))
		.build();

        when(appUserService.getUserSectorAssociations(appUser)).thenReturn(sectorAssociationIds);
        when(accountSearchService.searchAccounts(new ArrayList<>(sectorAssociationIds), searchCriteria))
                .thenReturn(expected);

        // invoke
        final AccountSearchResults results = service.getUserAccountsBySearchCriteria(appUser, searchCriteria);

        // verify
        verify(appUserService, times(1)).getUserSectorAssociations(appUser);
        verify(accountSearchService, times(1)).searchAccounts(sectorAssociationIds.stream().toList(), searchCriteria);
        assertThat(results).isEqualTo(expected);
    }

}
