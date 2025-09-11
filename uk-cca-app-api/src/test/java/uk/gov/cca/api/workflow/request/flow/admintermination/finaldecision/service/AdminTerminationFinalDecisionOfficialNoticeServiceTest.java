package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionReasonDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationFinalDecisionOfficialNoticeServiceTest {

    @InjectMocks
    private AdminTerminationFinalDecisionOfficialNoticeService adminTerminationFinalDecisionOfficialNoticeService;

    @Mock
    private CcaFileDocumentGeneratorService ccaOfficialNoticeGeneratorService;

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
        adminTerminationFinalDecisionOfficialNoticeService.generateOfficialNotice(request);

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
        adminTerminationFinalDecisionOfficialNoticeService.generateOfficialNotice(request);

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generate(request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINAL_DECISION_WITHDRAWN,
                        CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_WITHDRAWN,
                        "Withdrawal of intent to terminate agreement.pdf");
    }
}
