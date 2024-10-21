package uk.gov.cca.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.account.service.CaExternalContactService;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.userinfoapi.UserInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CcaDecisionNotificationUsersService {

    private final UserAuthService userAuthService;
    private final CaExternalContactService caExternalContactService;

    public List<String> findCCUserEmails(final CcaDecisionNotification decisionNotification) {
        final List<String> operatorEmails = userAuthService
                .getUsers(new ArrayList<>(decisionNotification.getDecisionNotification().getOperators())).stream()
                .map(UserInfo::getEmail)
                .toList();
        final List<String> sectorUserEmails = userAuthService
                .getUsers(new ArrayList<>(decisionNotification.getSectorUsers())).stream()
                .map(UserInfo::getEmail)
                .toList();

        final List<String> externalContactEmails = caExternalContactService
                .getCaExternalContactEmailsByIds(decisionNotification.getDecisionNotification().getExternalContacts());

        return Stream.of(operatorEmails, sectorUserEmails, externalContactEmails)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
