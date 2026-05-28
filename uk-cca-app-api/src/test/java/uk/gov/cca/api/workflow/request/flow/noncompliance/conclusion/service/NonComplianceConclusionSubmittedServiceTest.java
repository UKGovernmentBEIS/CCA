package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.service;

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
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusion;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonCompliancePenaltyOutcomeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceWithdrawNotice;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.netz.api.workflow.request.flow.common.service.DecisionNotificationUsersService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceConclusionSubmittedServiceTest {

    @InjectMocks
    private NonComplianceConclusionSubmittedService service;

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
    void submitConclusion_sendWithdrawNotice() {
        final String requestId = "requestId";
        final String regulator = "regulator";
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "attachment");
        final UUID fileUuid = UUID.randomUUID();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .build();
        final NonComplianceConclusion nonComplianceConclusion = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.WITHDRAW)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(NonComplianceWithdrawNotice.builder()
                        .file(fileUuid)
                        .comments("bla bla")
                        .build()).build();
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .nonComplianceConclusion(nonComplianceConclusion)
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
        final NonComplianceConclusionSubmittedRequestActionPayload actionPayload =
                NonComplianceConclusionSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMITTED_PAYLOAD)
                        .nonComplianceConclusion(requestPayload.getNonComplianceConclusion())
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
        service.submitConclusion(requestId);

        // Verify
        verify(requestService, times(2))
                .findRequestById(requestId);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, CcaRequestActionType.NON_COMPLIANCE_CONCLUSION_SUBMITTED, regulator);
        verify(requestActionUserInfoResolver, times(1))
                .getUsersInfo(decisionNotification, request);
        verify(ccaOfficialNoticeSendService, times(1))
                .getOfficialNoticeToDefaultRecipients(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(nonComplianceOfficialNoticeSendService, times(1))
                .sendOfficialNotice(List.of(enforcementResponseNoticeFileInfoDTO), request, List.of("operator@test.com"), Collections.emptyList());

    }

    @Test
    void submitConclusion_completeConclusion() {
        final String requestId = "requestId";
        final String regulator = "regulator";
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "attachment");
        final UUID fileUuid = UUID.randomUUID();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator"))
                .build();
        final NonComplianceConclusion nonComplianceConclusion = NonComplianceConclusion.builder()
                .details(NonComplianceConclusionDetails.builder()
                        .complianceRestored(true)
                        .complianceRestoredDate(LocalDate.now().minusDays(1))
                        .penaltyOutcome(NonCompliancePenaltyOutcomeType.NONE)
                        .penaltyPaid(false)
                        .comment("bla bla bla")
                        .build())
                .withdrawNotice(null)
                .build();
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .nonComplianceConclusion(nonComplianceConclusion)
                .nonComplianceAttachments(attachments)
                .regulatorAssignee(regulator)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();
        final NonComplianceConclusionSubmittedRequestActionPayload actionPayload =
                NonComplianceConclusionSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMITTED_PAYLOAD)
                        .nonComplianceConclusion(requestPayload.getNonComplianceConclusion())
                        .build();
        final FileInfoDTO enforcementResponseNoticeFileInfoDTO = FileInfoDTO.builder()
                .name(requestPayload.getNonComplianceAttachments().get(fileUuid))
                .uuid(fileUuid.toString())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.submitConclusion(requestId);

        // Verify
        verify(requestService, times(2))
                .findRequestById(requestId);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, CcaRequestActionType.NON_COMPLIANCE_CONCLUSION_SUBMITTED, regulator);
        verify(requestActionUserInfoResolver, never())
                .getUsersInfo(decisionNotification, request);
        verify(ccaOfficialNoticeSendService, never())
                .getOfficialNoticeToDefaultRecipients(request);
        verify(decisionNotificationUsersService, never()).findUserEmails(decisionNotification);
        verify(nonComplianceOfficialNoticeSendService, never())
                .sendOfficialNotice(List.of(enforcementResponseNoticeFileInfoDTO), request, List.of("operator@test.com"), Collections.emptyList());

    }
}