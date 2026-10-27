package uk.gov.cca.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.dto.CcaAccountSearchResultInfoDTO;
import uk.gov.cca.api.account.repository.CcaAccountSearchEntityPaths;
import uk.gov.cca.api.account.repository.CcaAccountSearchResultRowMapper;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.service.AppUserService;
import uk.gov.cca.api.common.domain.CcaRoleTypeConstants;
import uk.gov.netz.api.account.search.criteria.AccountSearchContactFilter;
import uk.gov.netz.api.account.search.criteria.AccountSearchFilterCriteria;
import uk.gov.netz.api.account.search.query.AccountSearchQueryRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaAccountSearchQueryServiceTest {

    @InjectMocks
    private CcaAccountSearchQueryService ccaAccountSearchQueryService;

    @Mock
    private AccountSearchQueryRepository<TargetUnitAccount, CcaAccountSearchResultInfoDTO> ccaAccountSearchQueryRepository;

    @Mock
    private CcaAccountSearchEntityPaths ccaAccountSearchEntityPaths;

    @Mock
    private CcaAccountSearchResultRowMapper ccaAccountSearchResultRowMapper;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Mock
    private AppUserService appUserService;

    @Test
    void search_SECTOR_USER() {
        final long sectorAssociationId = 1L;
        final AppUser user = AppUser.builder()
                .roleType(CcaRoleTypeConstants.SECTOR_USER)
                .authorities(List.of(AppCcaAuthority.builder().sectorAssociationId(sectorAssociationId).build()))
                .build();
        final AccountSearchFilterCriteria criteria = AccountSearchFilterCriteria.builder().build();
        final AccountSearchContactFilter contactFilter = AccountSearchContactFilter.none();

        final Set<Long> accountIds = Set.of(11L, 22L);

        when(appUserService.getUserSectorAssociations(user)).thenReturn(Set.of(sectorAssociationId));
        when(targetUnitAccountQueryService.getAllTargetUnitAccountIdsBySectorAssociationIds(Set.of(sectorAssociationId)))
                .thenReturn(accountIds);

        // Invoke
        ccaAccountSearchQueryService.search(user, criteria, contactFilter);

        // Verify
        verify(appUserService, times(1)).getUserSectorAssociations(user);
        verify(targetUnitAccountQueryService, times(1))
                .getAllTargetUnitAccountIdsBySectorAssociationIds(Set.of(sectorAssociationId));
        verify(ccaAccountSearchQueryRepository, times(1)).search(eq(criteria), any(), eq(ccaAccountSearchEntityPaths));
        verifyNoInteractions(ccaAccountSearchResultRowMapper);
    }

    @Test
    void search_SECTOR_OPERATOR() {
        final AppUser user = AppUser.builder()
                .roleType(RoleTypeConstants.OPERATOR)
                .authorities(List.of(AppCcaAuthority.builder().accountId(11L).build()))
                .build();
        final AccountSearchFilterCriteria criteria = AccountSearchFilterCriteria.builder().build();
        final AccountSearchContactFilter contactFilter = AccountSearchContactFilter.none();

        // Invoke
        ccaAccountSearchQueryService.search(user, criteria, contactFilter);

        // Verify
        verify(ccaAccountSearchQueryRepository, times(1)).search(eq(criteria), any(), eq(ccaAccountSearchEntityPaths));
        verifyNoInteractions(appUserService, targetUnitAccountQueryService, ccaAccountSearchResultRowMapper);
    }

    @Test
    void search_REGULATOR() {
        final AppUser user = AppUser.builder()
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(AppCcaAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();
        final AccountSearchFilterCriteria criteria = AccountSearchFilterCriteria.builder().build();
        final AccountSearchContactFilter contactFilter = AccountSearchContactFilter.none();

        // Invoke
        ccaAccountSearchQueryService.search(user, criteria, contactFilter);

        // Verify
        verify(ccaAccountSearchQueryRepository, times(1)).search(eq(criteria), any(), eq(ccaAccountSearchEntityPaths));
        verifyNoInteractions(appUserService, targetUnitAccountQueryService, ccaAccountSearchResultRowMapper);
    }

    @Test
    void search_empty_results() {
        final AppUser user = AppUser.builder()
                .roleType(RoleTypeConstants.OPERATOR)
                .authorities(List.of())
                .build();
        final AccountSearchFilterCriteria criteria = AccountSearchFilterCriteria.builder().build();
        final AccountSearchContactFilter contactFilter = AccountSearchContactFilter.none();

        // Invoke
        ccaAccountSearchQueryService.search(user, criteria, contactFilter);

        // Verify
        verify(ccaAccountSearchResultRowMapper, times(1)).emptyResults();
        verifyNoInteractions(appUserService, targetUnitAccountQueryService, ccaAccountSearchQueryRepository);
    }

    @Test
    void search_unsupported_Role_type() {
        final AppUser user = AppUser.builder()
                .roleType(RoleTypeConstants.VERIFIER)
                .authorities(List.of(AppCcaAuthority.builder().accountId(11L).build()))
                .build();
        final AccountSearchFilterCriteria criteria = AccountSearchFilterCriteria.builder().build();
        final AccountSearchContactFilter contactFilter = AccountSearchContactFilter.none();

        // Invoke
        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> ccaAccountSearchQueryService.search(user, criteria, contactFilter));

        // Verify
        assertThat(ex.getMessage()).isEqualTo("Fetching accounts for role type VERIFIER is not supported");
        verifyNoInteractions(appUserService, targetUnitAccountQueryService, ccaAccountSearchResultRowMapper, ccaAccountSearchQueryRepository);
    }
}
