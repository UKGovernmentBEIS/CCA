package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNotice;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeType;
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
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnforcementResponseNoticeNotifyOperatorServiceTest {

    @InjectMocks
    private EnforcementResponseNoticeNotifyOperatorService enforcementResponseNoticeNotifyOperatorService;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaRequestActionUserInfoResolver requestActionUserInfoResolver;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private NonComplianceOfficialNoticeSendService nonComplianceOfficialNoticeSendService;

    @Test
    void sendEnforcementResponseNotice() {
        final String requestId = "requestId";
        final String regulator = "regulator";
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "attachment");
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .build();
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceEnforcementResponseNotice enforcementResponseNotice = NonComplianceEnforcementResponseNotice.builder()
                .type(NonComplianceEnforcementResponseNoticeType.PENALTY)
                .file(fileUuid)
                .comments("bla bla bla")
                .build();
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .enforcementResponseNotice(enforcementResponseNotice)
                .nonComplianceAttachments(attachments)
                .regulatorAssignee(regulator)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();
        final Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "operator", RequestActionUserInfo.builder().name("Operator").roleCode("operator_basic_user").build()
        );
        final List<DefaultNoticeRecipient> defaultContacts = List.of(
                DefaultNoticeRecipient.builder().
                        name("Responsible")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );
        final NonComplianceEnforcementResponseNoticeSubmittedRequestActionPayload actionPayload =
                NonComplianceEnforcementResponseNoticeSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMITTED_PAYLOAD)
                        .enforcementResponseNotice(requestPayload.getEnforcementResponseNotice())
                        .decisionNotification(decisionNotification)
                        .nonComplianceAttachments(requestPayload.getNonComplianceAttachments())
                        .usersInfo(usersInfo)
                        .defaultContacts(defaultContacts)
                        .build();
        final FileInfoDTO enforcementResponseNoticeFileInfoDTO = FileInfoDTO.builder()
                .name(requestPayload.getNonComplianceAttachments().get(fileUuid))
                .uuid(fileUuid.toString())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(decisionNotification, request)).thenReturn(usersInfo);
        when(ccaOfficialNoticeSendService.getOfficialNoticeToDefaultRecipients(request)).thenReturn(defaultContacts);
        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(List.of("operator@test.com"));

        // Invoke
        enforcementResponseNoticeNotifyOperatorService.sendEnforcementResponseNotice(requestId);

        // Verify
        verify(requestService, times(1))
                .findRequestById(requestId);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, CcaRequestActionType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMITTED, regulator);
        verify(requestActionUserInfoResolver, times(1))
                .getUsersInfo(decisionNotification, request);
        verify(ccaOfficialNoticeSendService, times(1))
                .getOfficialNoticeToDefaultRecipients(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(nonComplianceOfficialNoticeSendService, times(1))
                .sendOfficialNotice(List.of(enforcementResponseNoticeFileInfoDTO), request, List.of("operator@test.com"), Collections.emptyList());

    }
}
