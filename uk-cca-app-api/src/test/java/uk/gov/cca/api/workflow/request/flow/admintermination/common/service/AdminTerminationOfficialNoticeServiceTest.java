package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawReasonDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
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
    private AdminTerminationOfficialNoticeService adminTerminationOfficialNoticeService;

    @Mock
    private CcaFileDocumentGeneratorService ccaOfficialNoticeGeneratorService;

    @Mock
    private CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Test
    void generateOfficialNotice() {
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final Request request = Request.builder()
                .payload(AdminTerminationRequestPayload.builder()
                        .adminTerminationReasonDetails(AdminTerminationReasonDetails.builder()
                                .reason(AdminTerminationReason.DATA_NOT_PROVIDED)
                                .build())
                        .decisionNotification(decisionNotification)
                        .build())
                .build();

        when(ccaOfficialNoticeGeneratorService.generate(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINALISED,
                CcaDocumentTemplateType.ADMIN_TERMINATION_ADMINISTRATIVE_SUBMITTED,
                "Termination notice.pdf"))
                .thenReturn(FileInfoDTO.builder().build());

        // Invoke
        adminTerminationOfficialNoticeService.generateOfficialNotice(request);

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generate(request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINALISED,
                        CcaDocumentTemplateType.ADMIN_TERMINATION_ADMINISTRATIVE_SUBMITTED,
                        "Termination notice.pdf");
    }

    @Test
    void generateOfficialNotice_regulatory() {
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final Request request = Request.builder()
                .payload(AdminTerminationRequestPayload.builder()
                        .adminTerminationReasonDetails(AdminTerminationReasonDetails.builder()
                                .reason(AdminTerminationReason.FAILURE_TO_PAY)
                                .build())
                        .decisionNotification(decisionNotification)
                        .build())
                .build();

        when(ccaOfficialNoticeGeneratorService.generate(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINALISED,
                CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_SUBMITTED,
                "Notice of intent to terminate agreement.pdf"))
                .thenReturn(FileInfoDTO.builder().build());

        // Invoke
        adminTerminationOfficialNoticeService.generateOfficialNotice(request);

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generate(request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINALISED,
                        CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_SUBMITTED,
                        "Notice of intent to terminate agreement.pdf");
    }

    @Test
    void generateFinalDecisionOfficialNotice() {
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final Request request = Request.builder()
                .payload(AdminTerminationRequestPayload.builder()
                        .adminTerminationFinalDecisionReasonDetails(
                                AdminTerminationFinalDecisionReasonDetails.builder()
                                        .finalDecisionType(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                                        .build())
                        .decisionNotification(decisionNotification)
                        .build())
                .build();

        when(ccaOfficialNoticeGeneratorService.generate(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINAL_DECISION_FINALISED,
                CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_TERMINATED,
                "Admin Termination Regulatory reason notice.pdf"))
                .thenReturn(FileInfoDTO.builder().build());

        // Invoke
        adminTerminationOfficialNoticeService.generateFinalDecisionOfficialNotice(request);

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generate(request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINAL_DECISION_FINALISED,
                        CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_TERMINATED,
                        "Admin Termination Regulatory reason notice.pdf");
    }

    @Test
    void generateFinalDecisionOfficialNotice_withdraw() {
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final Request request = Request.builder()
                .payload(AdminTerminationRequestPayload.builder()
                        .adminTerminationFinalDecisionReasonDetails(
                                AdminTerminationFinalDecisionReasonDetails.builder()
                                        .finalDecisionType(AdminTerminationFinalDecisionType.WITHDRAW_TERMINATION)
                                        .build())
                        .decisionNotification(decisionNotification)
                        .build())
                .build();

        when(ccaOfficialNoticeGeneratorService.generate(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINAL_DECISION_WITHDRAWN,
                CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_WITHDRAWN,
                "Withdrawal of intent to terminate agreement.pdf"))
                .thenReturn(FileInfoDTO.builder().build());

        // Invoke
        adminTerminationOfficialNoticeService.generateFinalDecisionOfficialNotice(request);

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generate(request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINAL_DECISION_WITHDRAWN,
                        CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_WITHDRAWN,
                        "Withdrawal of intent to terminate agreement.pdf");
    }

    @Test
    void generateWithdrawOfficialNotice() {
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final Request request = Request.builder()
                .payload(AdminTerminationRequestPayload.builder()
                        .adminTerminationWithdrawReasonDetails(
                                AdminTerminationWithdrawReasonDetails.builder().build())
                        .decisionNotification(decisionNotification)
                        .build())
                .build();

        when(ccaOfficialNoticeGeneratorService.generate(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_WITHDRAWN,
                CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_WITHDRAWN,
                "Withdrawal of intent to terminate agreement.pdf"))
                .thenReturn(FileInfoDTO.builder().build());

        // Invoke
        adminTerminationOfficialNoticeService.generateWithdrawOfficialNotice(request);

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generate(request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_WITHDRAWN,
                        CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_WITHDRAWN,
                        "Withdrawal of intent to terminate agreement.pdf");
    }

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
