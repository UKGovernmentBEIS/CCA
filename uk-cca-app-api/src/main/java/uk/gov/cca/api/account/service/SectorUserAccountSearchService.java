package uk.gov.cca.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.core.service.AppUserService;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.account.service.UserRoleTypeAccountSearchService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.ArrayList;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class SectorUserAccountSearchService implements UserRoleTypeAccountSearchService {

    private final CcaAccountSearchService accountSearchService;
    private final AppUserService appUserService;

    @Override
    public AccountSearchResults getUserAccountsBySearchCriteria(AppUser appUser, AccountSearchCriteria searchCriteria) {
		return accountSearchService.searchAccounts(new ArrayList<>(appUserService.getUserSectorAssociations(appUser)),
				searchCriteria);
    }

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }

}
