package uk.gov.cca.api.workflow.request.application.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.repository.AccountRepository;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.services.resource.VerifierAuthorityResourceService;
import uk.gov.cca.api.workflow.request.application.authorization.VerifierAuthorityResourceAdapter;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifierAuthorityResourceAdapterTest {

    @InjectMocks
    private VerifierAuthorityResourceAdapter verifierAuthorityResourceAdapter;

    @Mock
    private VerifierAuthorityResourceService verifierAuthorityResourceService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RequestTaskRepository taskRepository;


    @Test
    void getUserScopedRequestTaskTypesVerifierAdmin() {
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId)
                .authorities(List.of(AppAuthority.builder()
                        .permissions(List.of(Permission.PERM_VB_ACCESS_ALL_ACCOUNTS)).build()))
                .build();
        final Long vbId = 1L;

        RequestTaskType requestTaskType1 = RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW;
        RequestTaskType requestTaskType2 = RequestTaskType.DUMMY_REQUEST_TASK_TYPE2;

        when(accountRepository.findAllIdsByVerificationBody(vbId)).thenReturn(List.of(3L, 4L));
        when(verifierAuthorityResourceService.findUserScopedRequestTaskTypes(userId))
                .thenReturn(Map.of(1L, Set.of(requestTaskType1.toString(), requestTaskType2.toString())));

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes =
                verifierAuthorityResourceAdapter.getUserScopedRequestTaskTypes(user);

        assertThat(userScopedRequestTaskTypes).containsExactlyInAnyOrderEntriesOf(
                Map.of(3L, Set.of(requestTaskType1, requestTaskType2),
                        4L, Set.of(requestTaskType1, requestTaskType2))
        );
    }

    @Test
    void getUserScopedRequestTaskTypesVerifier() {
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId)
                .authorities(List.of(AppAuthority.builder()
                        .permissions(List.of()).build()))
                .build();

        final Long vbId = 1L;
        final List<Long> accountIds = List.of(2L, 3L);

        RequestTaskType requestTaskType1 = RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW;
        RequestTaskType requestTaskType2 = RequestTaskType.DUMMY_REQUEST_TASK_TYPE2;

        final Set<String> taskTypesString = Set.of(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW.toString(), RequestTaskType.DUMMY_REQUEST_TASK_TYPE2.toString());
        final Set<RequestTaskType> taskTypes = Set.of(requestTaskType1, requestTaskType2);

        when(verifierAuthorityResourceService.findUserScopedRequestTaskTypes(userId))
                .thenReturn(Map.of(
                        vbId,
                        taskTypesString
                ));
        when(taskRepository.findAccountIdsByTaskAssigneeAndTaskTypeInAndVerificationBody( "userId", taskTypes, vbId))
                .thenReturn(accountIds);

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes =
                verifierAuthorityResourceAdapter.getUserScopedRequestTaskTypes(user);

        assertThat(userScopedRequestTaskTypes).containsExactlyInAnyOrderEntriesOf(
                Map.of(2L, Set.of(requestTaskType1, requestTaskType2),
                        3L, Set.of(requestTaskType1, requestTaskType2))
        );
    }
}