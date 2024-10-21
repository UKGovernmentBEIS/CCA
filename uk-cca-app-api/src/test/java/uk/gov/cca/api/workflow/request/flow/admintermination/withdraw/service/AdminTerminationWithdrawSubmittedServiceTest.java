package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.service.AdminTerminationOfficialNoticeService;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminTerminationWithdrawSubmittedServiceTest {

    @InjectMocks
    private AdminTerminationWithdrawSubmittedService adminTerminationWithdrawSubmittedService;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Mock
    private AdminTerminationOfficialNoticeService adminTerminationOfficialNoticeService;

    @Test
    void submit() {
        final String requestId = "requestId";
        final String regulator = "regulator";

        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "attachment");
        final AdminTerminationWithdrawReasonDetails reasonDetails = AdminTerminationWithdrawReasonDetails.builder()
                .explanation("explanation")
                .build();
        final CcaDecisionNotification ccaDecisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(AdminTerminationRequestPayload.builder()
                        .regulatorAssignee(regulator)
                        .adminTerminationWithdrawReasonDetails(reasonDetails)
                        .adminTerminationWithdrawAttachments(attachments)
                        .decisionNotification(ccaDecisionNotification)
                        .build())
                .build();
        final Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "sector", RequestActionUserInfo.builder().name("Sector").roleCode("sector_user_administrator").build()
        );
        final List<DefaultNoticeRecipient> defaultContacts = List.of(
                DefaultNoticeRecipient.builder().
                        name("Responsible")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );
        final FileInfoDTO file = FileInfoDTO.builder().name("name").build();

        final AdminTerminationWithdrawSubmittedRequestActionPayload actionPayload =
                AdminTerminationWithdrawSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.ADMIN_TERMINATION_WITHDRAW_SUBMITTED_PAYLOAD)
                        .adminTerminationWithdrawReasonDetails(reasonDetails)
                        .decisionNotification(ccaDecisionNotification)
                        .adminTerminationWithdrawAttachments(attachments)
                        .usersInfo(usersInfo)
                        .defaultContacts(defaultContacts)
                        .officialNotice(file)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaRequestActionUserInfoResolver.getUsersInfo(ccaDecisionNotification, request)).thenReturn(usersInfo);
        when(ccaOfficialNoticeSendService.getOfficialNoticeToDefaultRecipients(request)).thenReturn(defaultContacts);
        when(adminTerminationOfficialNoticeService.generateWithdrawOfficialNotice(request)).thenReturn(file);

        // Invoke
        adminTerminationWithdrawSubmittedService.submit(requestId);

        // Verify
        verify(requestService, times(1))
                .findRequestById(requestId);
        verify(ccaRequestActionUserInfoResolver, times(1))
                .getUsersInfo(ccaDecisionNotification, request);
        verify(ccaOfficialNoticeSendService, times(1))
                .getOfficialNoticeToDefaultRecipients(request);
        verify(adminTerminationOfficialNoticeService, times(1))
                .generateWithdrawOfficialNotice(request);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, CcaRequestActionType.ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED, regulator);
        verify(adminTerminationOfficialNoticeService, times(1))
                .sendOfficialNotice(request, file, ccaDecisionNotification);
    }
}
