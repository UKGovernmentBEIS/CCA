package uk.gov.cca.api.workflow.request.flow.admintermination.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
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
class AdminTerminationSubmitOfficialNoticeServiceTest {

    @InjectMocks
    private AdminTerminationSubmitOfficialNoticeService adminTerminationSubmitOfficialNoticeService;

    @Mock
    private CcaFileDocumentGeneratorService ccaOfficialNoticeGeneratorService;

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
        adminTerminationSubmitOfficialNoticeService.generateOfficialNotice(request);

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
        adminTerminationSubmitOfficialNoticeService.generateOfficialNotice(request);

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generate(request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINALISED,
                        CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_SUBMITTED,
                        "Notice of intent to terminate agreement.pdf");
    }
}
