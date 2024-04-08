package uk.gov.cca.api.workflow.request.flow.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.cca.api.account.domain.dto.CaExternalContactDTO;
import uk.gov.cca.api.account.service.CaExternalContactService;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.AuthorityStatus;
import uk.gov.cca.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.cca.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentValidationService;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowUsersValidator {

    private final OperatorAuthorityQueryService operatorAuthorityQueryService;
    private final CaExternalContactService caExternalContactService;
    private final RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;


    public boolean areOperatorsValid(final Long accountId,
                                     final Set<String> operators,
                                     final AppUser appUser) {
        if (CollectionUtils.isEmpty(operators)) {
            return true;
        }

        final List<String> allOperators = operatorAuthorityQueryService.getAccountAuthorities(appUser, accountId)
            .getAuthorities()
            .stream()
            .filter(au -> au.getAuthorityStatus().equals(AuthorityStatus.ACTIVE))
            .map(UserAuthorityDTO::getUserId)
            .collect(Collectors.toList());
        return allOperators.containsAll(operators);
    }

    public boolean areExternalContactsValid(final Set<Long> externalContacts,
                                            final AppUser appUser) {
        if (CollectionUtils.isEmpty(externalContacts)) {
            return true;
        }

        final Set<Long> allExternalContacts = caExternalContactService.getCaExternalContacts(appUser)
            .getCaExternalContacts()
            .stream()
            .map(CaExternalContactDTO::getId).
            collect(Collectors.toSet());
        return allExternalContacts.containsAll(externalContacts);
    }

    public boolean isSignatoryValid(final RequestTask requestTask,
                                     final String signatory) {
        if (signatory == null) {
            return true;
        }

        return requestTaskAssignmentValidationService.hasUserPermissionsToBeAssignedToTask(requestTask, signatory);
    }
}
