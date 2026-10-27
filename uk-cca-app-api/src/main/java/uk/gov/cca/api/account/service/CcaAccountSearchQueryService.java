package uk.gov.cca.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.dto.CcaAccountSearchResultInfoDTO;
import uk.gov.cca.api.account.repository.CcaAccountSearchEntityPaths;
import uk.gov.cca.api.account.repository.CcaAccountSearchResultRowMapper;
import uk.gov.cca.api.authorization.ccaauth.core.service.AppUserService;
import uk.gov.cca.api.common.domain.CcaRoleTypeConstants;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.account.search.criteria.AccountSearchContactFilter;
import uk.gov.netz.api.account.search.criteria.AccountSearchFilterCriteria;
import uk.gov.netz.api.account.search.criteria.AccountSearchQueryContext;
import uk.gov.netz.api.account.search.criteria.AccountSearchScope;
import uk.gov.netz.api.account.search.query.AccountSearchQueryRepository;
import uk.gov.netz.api.account.search.service.AccountSearchQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CcaAccountSearchQueryService implements AccountSearchQueryService<TargetUnitAccount, CcaAccountSearchResultInfoDTO> {

    private final AccountSearchQueryRepository<TargetUnitAccount, CcaAccountSearchResultInfoDTO> ccaAccountSearchQueryRepository;
    private final CcaAccountSearchEntityPaths ccaAccountSearchEntityPaths;
    private final CcaAccountSearchResultRowMapper ccaAccountSearchResultRowMapper;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final AppUserService appUserService;

    @Override
    public AccountSearchResults<CcaAccountSearchResultInfoDTO> search(AppUser user, AccountSearchFilterCriteria criteria, AccountSearchContactFilter contactFilter) {
        AccountSearchQueryContext context = buildQueryContext(user, contactFilter);
        if (context.isEffectivelyEmpty()) {
            return ccaAccountSearchResultRowMapper.emptyResults();
        }
        return ccaAccountSearchQueryRepository.search(criteria, context, ccaAccountSearchEntityPaths);
    }

    private AccountSearchQueryContext buildQueryContext(AppUser user, AccountSearchContactFilter contactFilter) {
        Set<Long> contactAccountIds = contactFilter.getAccountIds();
        boolean contactFilterActive = contactFilter.isActive();

        return switch (user.getRoleType()) {
            case CcaRoleTypeConstants.SECTOR_USER -> forSectorAssociationIds(user, contactFilter);
            case RoleTypeConstants.OPERATOR -> AccountSearchQueryContext.forAccountIds(
                    user.getAccounts(), contactAccountIds, contactFilterActive);
            case RoleTypeConstants.REGULATOR -> AccountSearchQueryContext.forCompetentAuthority(
                    user.getCompetentAuthority(), contactAccountIds, contactFilterActive);
            default -> throw new UnsupportedOperationException(
                    String.format("Fetching accounts for role type %s is not supported", user.getRoleType()));
        };
    }

    private AccountSearchQueryContext forSectorAssociationIds(AppUser user, AccountSearchContactFilter contactFilter) {
        Set<Long> sectorAssociationIds = this.appUserService.getUserSectorAssociations(user);
        Set<Long> accountIds = targetUnitAccountQueryService.getAllTargetUnitAccountIdsBySectorAssociationIds(sectorAssociationIds);

        return AccountSearchQueryContext.builder()
                .scope(AccountSearchScope.ACCOUNT_IDS)
                .roleAccountIds(accountIds)
                .contactAccountIds(contactFilter.getAccountIds())
                .contactFilterActive(contactFilter.isActive())
                .build();
    }
}
