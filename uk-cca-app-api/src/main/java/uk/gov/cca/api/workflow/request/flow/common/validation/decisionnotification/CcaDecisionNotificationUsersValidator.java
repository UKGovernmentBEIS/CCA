package uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorAuthorityQueryService;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.account.domain.dto.CaExternalContactDTO;
import uk.gov.netz.api.account.service.CaExternalContactService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentValidationService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CcaDecisionNotificationUsersValidator {

    private final SectorAuthorityQueryService sectorAuthorityQueryService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final OperatorAuthorityQueryService operatorAuthorityQueryService;
    private final CaExternalContactService caExternalContactService;
    private final RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;

    public List<BusinessViolation> validate(final RequestTask requestTask, final CcaDecisionNotification decisionNotification,
                                            final AppUser appUser) {
        List<BusinessViolation> violations = new ArrayList<>();

        // Validate Sector Users
        final Set<String> sectorUsers = decisionNotification.getSectorUsers();
        Collection<String> sectorUsersDiff = validateSectorUsers(sectorUsers, requestTask.getRequest().getAccountId(), appUser);
        if(!sectorUsersDiff.isEmpty()) {
            violations.add(new BusinessViolation("", sectorUsersDiff.toArray()));
        }

        // Validate Operators
        final Set<String> operators = decisionNotification.getDecisionNotification().getOperators();
        Collection<String> operatorsDiff = validateOperators(operators, requestTask.getRequest().getAccountId(), appUser);
        if(!operatorsDiff.isEmpty()) {
            violations.add(new BusinessViolation("", operatorsDiff.toArray()));
        }

        // Validate external contacts
        final Set<Long> externalContacts = decisionNotification.getDecisionNotification().getExternalContacts();
        Collection<Long> externalContactsDiff = validateExternalContacts(externalContacts, appUser);
        if(!externalContactsDiff.isEmpty()) {
            violations.add(new BusinessViolation("", externalContactsDiff.toArray()));
        }

        // Validate signatory
        final String signatory = decisionNotification.getDecisionNotification().getSignatory();
        if(!validSignatory(requestTask, signatory)) {
            violations.add(new BusinessViolation("", signatory));
        }

        return violations;
    }

    private Collection<String> validateSectorUsers(Set<String> sectorUsers, Long accountId, AppUser appUser) {
        if(!sectorUsers.isEmpty()) {
            // Find sector from account
            Long sectorId = accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)
                    .getSectorAssociationId();

            final Set<String> allSectorUsers = sectorAuthorityQueryService.getSectorUserAuthorities(appUser, sectorId)
                    .getAuthorities()
                    .stream()
                    .filter(au -> au.getAuthorityStatus().equals(AuthorityStatus.ACTIVE))
                    .map(SectorUserAuthorityDTO::getUserId)
                    .collect(Collectors.toSet());

            return CollectionUtils.subtract(sectorUsers, allSectorUsers);
        }
        return Collections.emptySet();
    }

    private Collection<String> validateOperators(Set<String> operators, Long accountId, AppUser appUser) {
        if(!operators.isEmpty()) {
            final Set<String> allOperators = operatorAuthorityQueryService.getAccountAuthorities(appUser, accountId)
                    .getAuthorities()
                    .stream()
                    .filter(au -> au.getAuthorityStatus().equals(AuthorityStatus.ACTIVE))
                    .map(UserAuthorityDTO::getUserId)
                    .collect(Collectors.toSet());

            return CollectionUtils.subtract(operators, allOperators);
        }
        return Collections.emptySet();
    }

    private Collection<Long> validateExternalContacts(Set<Long> externalContacts, AppUser appUser) {
        if(!externalContacts.isEmpty()) {
            Set<Long> allExternalContacts = caExternalContactService.getCaExternalContacts(appUser)
                    .getCaExternalContacts()
                    .stream()
                    .map(CaExternalContactDTO::getId)
                    .collect(Collectors.toSet());

            return CollectionUtils.subtract(externalContacts, allExternalContacts);
        }
        return Collections.emptySet();
    }

    private boolean validSignatory(RequestTask requestTask, String signatory) {
        return ObjectUtils.isEmpty(signatory) || requestTaskAssignmentValidationService
                .hasUserPermissionsToBeAssignedToTask(requestTask, signatory);
    }
}
