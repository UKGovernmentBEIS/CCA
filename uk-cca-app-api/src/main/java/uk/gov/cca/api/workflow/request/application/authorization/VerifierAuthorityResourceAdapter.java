package uk.gov.cca.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.repository.AccountRepository;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.services.authorization.VerifierAccountAccessService;
import uk.gov.cca.api.authorization.rules.services.resource.VerifierAuthorityResourceService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.cca.api.authorization.core.domain.Permission.PERM_VB_ACCESS_ALL_ACCOUNTS;

@Service
@RequiredArgsConstructor
public class VerifierAuthorityResourceAdapter implements VerifierAccountAccessService {

    private final VerifierAuthorityResourceService verifierAuthorityResourceService;
    private final AccountRepository accountRepository;
    private final RequestTaskRepository taskRepository;

    @Override
    public Set<Long> findAuthorizedAccountIds(final AppUser user) {
        return this.getUserScopedRequestTaskTypes(user).keySet();
    }

    public Map<Long, Set<RequestTaskType>> getUserScopedRequestTaskTypes(final AppUser user) {

        final Map<Long, Set<String>> requestTaskTypesPerVbId =
                verifierAuthorityResourceService.findUserScopedRequestTaskTypes(user.getUserId());

        final boolean hasAccessToAllAccounts = this.hasUserPermissionToAccessAllAccounts(user);
        final Map<Long, Set<RequestTaskType>> requestTaskTypesPerAccount = new HashMap<>();

        for (final Map.Entry<Long, Set<String>> entry : requestTaskTypesPerVbId.entrySet()) {
            final Long vbId = entry.getKey();
            final Set<RequestTaskType> taskTypes = entry.getValue().stream().map(RequestTaskType::valueOf).collect(Collectors.toSet());
            final List<Long> accountIds = hasAccessToAllAccounts ?
                    accountRepository.findAllIdsByVerificationBody(vbId) :
                    taskRepository.findAccountIdsByTaskAssigneeAndTaskTypeInAndVerificationBody(user.getUserId(), taskTypes, vbId);
            accountIds.forEach(accId -> requestTaskTypesPerAccount.put(accId, taskTypes));
        }
        return requestTaskTypesPerAccount;
    }

    private boolean hasUserPermissionToAccessAllAccounts(final AppUser user) {

        return user.getAuthorities().stream()
                .filter(Objects::nonNull)
                .flatMap(authority -> authority.getPermissions().stream())
                .toList()
                .contains(PERM_VB_ACCESS_ALL_ACCOUNTS);
    }
}