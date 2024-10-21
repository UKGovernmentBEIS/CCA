package uk.gov.cca.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.account.service.CaExternalContactService;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaDecisionNotificationUsersServiceTest {

    @InjectMocks
    private CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private CaExternalContactService caExternalContactService;

    @Test
    void findCCUserEmails() {
        final Set<String> operators = Set.of("operator");
        final Set<String> sectors = Set.of("sector");
        final Set<Long> externals = Set.of(1L);
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .decisionNotification(DecisionNotification.builder()
                        .operators(operators)
                        .externalContacts(externals)
                        .build())
                .sectorUsers(sectors)
                .build();

        List<UserInfo> operatorUsers = List.of(UserInfo.builder().email("operator@example.com").build());
        List<UserInfo> sectorUsers = List.of(UserInfo.builder().email("sector@example.com").build());
        List<String> externalUsers = List.of("external@example.com");

        when(userAuthService.getUsers((new ArrayList<>(operators))))
                .thenReturn(operatorUsers);
        when(userAuthService.getUsers((new ArrayList<>(sectors))))
                .thenReturn(sectorUsers);
        when(caExternalContactService.getCaExternalContactEmailsByIds(externals))
                .thenReturn(externalUsers);

        // Invoke
        List<String> result = ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification);

        // Verify
        assertThat(result).containsExactlyInAnyOrder("operator@example.com", "sector@example.com",
                "external@example.com");
        verify(userAuthService, times(1))
                .getUsers((new ArrayList<>(operators)));
        verify(userAuthService, times(1))
                .getUsers((new ArrayList<>(sectors)));
        verify(caExternalContactService, times(1))
                .getCaExternalContactEmailsByIds(externals);
    }
}
