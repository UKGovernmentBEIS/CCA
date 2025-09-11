package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.service.AdminTerminationSubmitOfficialNoticeService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationOfficialNoticeServiceTest {

    @InjectMocks
    private AdminTerminationSubmitOfficialNoticeService adminTerminationOfficialNoticeService;

    @Mock
    private CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Test
    void sendOfficialNotice() {
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final Request request = Request.builder()
                .payload(AdminTerminationRequestPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build())
                .build();
        final FileInfoDTO file = FileInfoDTO.builder().name("name").build();

        final List<String> userEmails = List.of("emal1@example.com", "emal2@example.com");

        when(ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification))
                .thenReturn(userEmails);

        // Invoke
        adminTerminationOfficialNoticeService.sendOfficialNotice(request, file, decisionNotification);

        // Verify
        verify(ccaDecisionNotificationUsersService, times(1))
                .findCCUserEmails(decisionNotification);
        verify(ccaOfficialNoticeSendService, times(1))
                .sendOfficialNotice(List.of(file), request, userEmails);
    }
}
