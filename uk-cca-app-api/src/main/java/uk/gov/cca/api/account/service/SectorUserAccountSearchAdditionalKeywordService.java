package uk.gov.cca.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.core.service.AppUserService;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchKeywordResultsDTO;
import uk.gov.netz.api.account.domain.dto.AccountSearchResultInfoDTO;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.account.service.UserRoleTypeAccountSearchService;
import uk.gov.netz.api.account.transform.AccountSearchResultMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class SectorUserAccountSearchAdditionalKeywordService implements UserRoleTypeAccountSearchService {

    private final CcaAccountSearchService accountSearchService;
    private final AppUserService appUserService;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final AccountSearchResultMapper accountSearchResultMapper;

    @Override
    public AccountSearchResults getUserAccountsBySearchCriteria(AppUser appUser, AccountSearchCriteria searchCriteria) {
        final Set<Long> userSectorAssociationIds = appUserService.getUserSectorAssociations(appUser);
        final AccountSearchKeywordResultsDTO accountSearchKeywordResultsDTO =
                accountSearchService.searchAccounts(userSectorAssociationIds.stream().toList(), searchCriteria);
        return getAccountSearchResults(accountSearchKeywordResultsDTO);
    }

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }

    private AccountSearchResults getAccountSearchResults(AccountSearchKeywordResultsDTO accountSearchKeywordResults) {
        final List<AccountSearchResultInfoDTO> accounts = targetUnitAccountQueryService
                .getAccounts(accountSearchKeywordResults.getAccountIds())
                .stream()
                .map(accountSearchResultMapper::toAccountInfoDTO)
                .collect(Collectors.toList());

        return AccountSearchResults.builder()
                .accounts(accounts)
                .total(accountSearchKeywordResults.getTotal())
                .build();
    }
}
