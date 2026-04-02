package uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorAuthorityQueryService;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CcaDecisionNotificationUsersValidator {

    private final SectorAuthorityQueryService sectorAuthorityQueryService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public List<BusinessViolation> validate(final RequestTask requestTask, final CcaDecisionNotification decisionNotification,
                                            final AppUser appUser) {
        List<BusinessViolation> violations = new ArrayList<>();

        // Validate Sector Users
        final Set<String> sectorUsers = decisionNotification.getSectorUsers();
        if (!areSectorUsersValid(sectorUsers, requestTask.getRequest().getAccountId(), appUser)) {
            violations.add(new BusinessViolation("CcaDecisionNotifications.sectorUsers"));
        }

        // Validate Operators, External Contacts and Signatory
        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification.getDecisionNotification(), appUser)) {
            violations.add(new BusinessViolation("DecisionNotifications"));
        }

        return violations;
    }

    private boolean areSectorUsersValid(Set<String> sectorUsers, Long accountId, AppUser appUser) {
        if (CollectionUtils.isEmpty(sectorUsers)) {
            return true;
        }

        // Find sector from account
        Long sectorId = accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)
                .getSectorAssociationId();

        final Set<String> allSectorUsers = sectorAuthorityQueryService.getSectorUserAuthorities(appUser, sectorId)
                .getAuthorities()
                .stream()
                .filter(au -> au.getAuthorityStatus().equals(AuthorityStatus.ACTIVE))
                .map(SectorUserAuthorityDTO::getUserId)
                .collect(Collectors.toSet());

        return allSectorUsers.containsAll(sectorUsers);
    }
}
