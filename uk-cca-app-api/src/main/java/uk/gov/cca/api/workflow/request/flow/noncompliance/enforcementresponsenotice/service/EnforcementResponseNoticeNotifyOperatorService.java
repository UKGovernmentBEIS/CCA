package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSubmittedRequestActionPayload;
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
public class EnforcementResponseNoticeNotifyOperatorService {

    private final RequestService requestService;
    private final CcaRequestActionUserInfoResolver requestActionUserInfoResolver;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final NonComplianceOfficialNoticeSendService nonComplianceOfficialNoticeSendService;

    public void sendEnforcementResponseNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();

        // Get users' information
        final DecisionNotification decisionNotification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
                .getUsersInfo(decisionNotification, request);

        // Get Default notice contacts
        final List<DefaultNoticeRecipient> defaultContacts =
                ccaOfficialNoticeSendService.getOfficialNoticeToDefaultRecipients(request);

        final UUID enforcementResponseNoticeFile = requestPayload.getEnforcementResponseNotice().getFile();
        final FileInfoDTO enforcementResponseNotice = FileInfoDTO.builder()
                .name(requestPayload.getNonComplianceAttachments().get(enforcementResponseNoticeFile))
                .uuid(enforcementResponseNoticeFile.toString())
                .build();

        // Create request action
        final NonComplianceEnforcementResponseNoticeSubmittedRequestActionPayload actionPayload =
                NonComplianceEnforcementResponseNoticeSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMITTED_PAYLOAD)
                        .enforcementResponseNotice(requestPayload.getEnforcementResponseNotice())
                        .decisionNotification(decisionNotification)
                        .nonComplianceAttachments(requestPayload.getNonComplianceAttachments())
                        .usersInfo(usersInfo)
                        .defaultContacts(defaultContacts)
                        .build();

        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMITTED,
                request.getPayload().getRegulatorAssignee());

        // Send notice of intent
        nonComplianceOfficialNoticeSendService.sendOfficialNotice(List.of(enforcementResponseNotice), request,
                decisionNotificationUsersService.findUserEmails(decisionNotification), Collections.emptyList());
    }
}
