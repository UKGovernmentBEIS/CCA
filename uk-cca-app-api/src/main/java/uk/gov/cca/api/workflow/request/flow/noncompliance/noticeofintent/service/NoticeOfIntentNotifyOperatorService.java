package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmittedRequestActionPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.netz.api.workflow.request.flow.common.service.DecisionNotificationUsersService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeOfIntentNotifyOperatorService {

    private final RequestService requestService;
    private final CcaRequestActionUserInfoResolver requestActionUserInfoResolver;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final NonComplianceOfficialNoticeSendService nonComplianceOfficialNoticeSendService;

    public void sendNoticeOfIntent(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();

        // Get users' information
        final DecisionNotification decisionNotification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
                .getUsersInfo(decisionNotification, request);

        // Get Default notice contacts
        final List<DefaultNoticeRecipient> defaultContacts =
                ccaOfficialNoticeSendService.getOfficialNoticeToDefaultRecipients(request);

        final UUID noticeOfIntentUUID = requestPayload.getNoticeOfIntent().getNoticeOfIntentFile();
        final FileInfoDTO noticeOfIntent = FileInfoDTO.builder()
                .name(requestPayload.getNonComplianceAttachments().get(noticeOfIntentUUID))
                .uuid(noticeOfIntentUUID.toString())
                .build();

        // Create request action
        final NonComplianceNoticeOfIntentSubmittedRequestActionPayload actionPayload =
                NonComplianceNoticeOfIntentSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMITTED_PAYLOAD)
                        .noticeOfIntent(requestPayload.getNoticeOfIntent())
                        .decisionNotification(decisionNotification)
                        .nonComplianceAttachments(requestPayload.getNonComplianceAttachments())
                        .usersInfo(usersInfo)
                        .defaultContacts(defaultContacts)
                        .build();

        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMITTED,
                request.getPayload().getRegulatorAssignee());

        // Send notice of intent
        nonComplianceOfficialNoticeSendService.sendOfficialNotice(List.of(noticeOfIntent), request,
                decisionNotificationUsersService.findUserEmails(decisionNotification), Collections.emptyList());
    }
}
