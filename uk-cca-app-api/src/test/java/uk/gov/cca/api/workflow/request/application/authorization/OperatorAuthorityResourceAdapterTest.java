package uk.gov.cca.api.workflow.request.application.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.AccountQueryService;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.services.resource.OperatorAuthorityResourceService;
import uk.gov.cca.api.workflow.request.application.authorization.OperatorAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorAuthorityResourceAdapterTest {

    @InjectMocks
    private OperatorAuthorityResourceAdapter operatorAuthorityResourceAdapter;

    @Mock
    private OperatorAuthorityResourceService operatorAuthorityResourceService;

    @Mock
    private AccountQueryService accountQueryService;

    @Test
    void getUserScopedRequestTaskTypesByAccountId() {
        final String userId = "userId";
        final Long accountId = 1L;

        when(operatorAuthorityResourceService.findUserScopedRequestTaskTypesByAccounts(userId, Set.of(accountId)))
            .thenReturn(Map.of(
                accountId, Set.of(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW.toString(), RequestTaskType.DUMMY_REQUEST_TASK_TYPE2.toString()))
            );

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypesByAccounts =
            operatorAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountId(userId, accountId);

        assertThat(userScopedRequestTaskTypesByAccounts).containsExactlyEntriesOf(Map.of(
            accountId,
            Set.of(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, RequestTaskType.DUMMY_REQUEST_TASK_TYPE2))
        );

        verify(operatorAuthorityResourceService, times(1))
            .findUserScopedRequestTaskTypesByAccounts(userId, Set.of(accountId));
        verifyNoInteractions(accountQueryService);
    }

    @Test
    void getUserScopedRequestTaskTypes() {
        final Long accountId1 = 1L;
        final Long accountId2 = 2L;
        final List<Long> accounts = List.of(accountId1, accountId2);
        final String userId = "userId";
        final AppUser appUser = AppUser.builder()
            .userId(userId)
            .authorities(List.of(
                AppAuthority.builder().accountId(accountId1).build(),
                AppAuthority.builder().accountId(accountId2).build()
                )
            )
            .build();
        final Set<Long> installationAccounts = Set.of(accountId1, accountId2);

        when(accountQueryService.getAccountIds(accounts)).thenReturn(installationAccounts);
        when(operatorAuthorityResourceService.findUserScopedRequestTaskTypesByAccounts(userId, installationAccounts))
            .thenReturn(Map.of(
                accountId1, Set.of(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW.toString())
                )
            );

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes =
            operatorAuthorityResourceAdapter.getUserScopedRequestTaskTypes(appUser);

        assertThat(userScopedRequestTaskTypes).containsExactlyEntriesOf(
            Map.of(accountId1, Set.of(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW))
        );

        verify(accountQueryService, times(1)).getAccountIds(accounts);
        verify(operatorAuthorityResourceService, times(1))
            .findUserScopedRequestTaskTypesByAccounts(userId, installationAccounts);
    }
}