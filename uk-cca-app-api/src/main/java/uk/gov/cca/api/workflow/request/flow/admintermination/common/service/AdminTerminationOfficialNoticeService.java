package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;

@Service
@RequiredArgsConstructor
public abstract class AdminTerminationOfficialNoticeService {

    private final CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    protected abstract FileInfoDTO generateOfficialNotice(final Request request);

    public void sendOfficialNotice(Request request, FileInfoDTO officialNotice,
                                   CcaDecisionNotification decisionNotification) {
        ccaOfficialNoticeSendService.sendOfficialNotice(List.of(officialNotice), request,
                ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification));
    }
}
