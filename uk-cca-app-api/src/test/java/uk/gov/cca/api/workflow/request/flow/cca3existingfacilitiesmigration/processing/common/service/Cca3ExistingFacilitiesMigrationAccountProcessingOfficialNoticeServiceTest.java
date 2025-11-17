package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeServiceTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService service;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Test
    void generateAcceptedOfficialNotice() {
        final String requestId = "requestId";
        final String signatory = "signatory";
        final String filename = "CCA3 Migration Proposed underlying agreement cover letter.pdf";
        final Request request = Request.builder()
                .payload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .defaultSignatory(signatory)
                        .build())
                .build();

        final TemplateParams params = TemplateParams.builder()
                .params(Map.of("version", "v1"))
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.generateAcceptedOfficialNotice(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaFileDocumentGeneratorService, times(1))
                .generateAsync(request, signatory, CcaDocumentTemplateType.MIGRATION_UNDERLYING_AGREEMENT_ACCEPTED_CCA3, params, filename);
    }

    @Test
    void generateActivatedOfficialNotice() {
        final String requestId = "requestId";
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final Request request = Request.builder()
                .payload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.generateActivatedOfficialNotice(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaFileDocumentGeneratorService, times(1)).generateAsync(
                request, decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.EXISTING_FACILITIES_MIGRATION_UNDERLYING_AGREEMENT_ACTIVATED_CCA3,
                CcaDocumentTemplateType.MIGRATION_UNDERLYING_AGREEMENT_ACTIVATED_CCA3,
                "CCA3 Migration Activated underlying agreement cover letter.pdf");
    }

    @Test
    void sendOfficialNotice() {
        final String requestId = "requestId";
        final FileInfoDTO document = FileInfoDTO.builder().name("document").build();
        final FileInfoDTO notice = FileInfoDTO.builder().name("notice").build();
        final Request request = Request.builder()
                .payload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .underlyingAgreementDocument(document)
                        .officialNotice(notice)
                        .build())
                .build();
        final List<FileInfoDTO> attachments = List.of(document, notice);

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.sendOfficialNotice(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaOfficialNoticeSendService, times(1))
                .sendOfficialNotice(attachments, request, new ArrayList<>());
    }
}
