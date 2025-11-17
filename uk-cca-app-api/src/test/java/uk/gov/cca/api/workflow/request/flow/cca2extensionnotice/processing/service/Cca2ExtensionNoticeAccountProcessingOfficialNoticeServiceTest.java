package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeAccountProcessingOfficialNoticeServiceTest {

    @InjectMocks
    private Cca2ExtensionNoticeAccountProcessingOfficialNoticeService service;

    @Mock
    private CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Test
    void generateExtensionNotice() {
        final int version = 1;
        final String signatory = "signatory";
        final String filename = "CCA2 Extension Activated underlying agreement cover letter.pdf";
        final Request request = Request.builder()
                .payload(Cca2ExtensionNoticeAccountProcessingRequestPayload.builder()
                        .defaultSignatory(signatory)
                        .build())
                .build();

        final TemplateParams params = TemplateParams.builder()
                .params(Map.of("version", "v" + version))
                .build();

        // Invoke
        service.generateExtensionNotice(request, version);

        // Verify
        verify(ccaFileDocumentGeneratorService, times(1))
                .generateAsync(request, signatory, CcaDocumentTemplateType.EXTENSION_NOTICE_CCA2, params, filename);
    }

    @Test
    void sendOfficialNotice() {
        final FileInfoDTO document = FileInfoDTO.builder().name("document").build();
        final FileInfoDTO notice = FileInfoDTO.builder().name("notice").build();
        final Request request = Request.builder()
                .payload(Cca2ExtensionNoticeAccountProcessingRequestPayload.builder()
                        .underlyingAgreementDocument(document)
                        .officialNotice(notice)
                        .build())
                .build();
        final List<FileInfoDTO> attachments = List.of(document, notice);

        // Invoke
        service.sendOfficialNotice(request);

        // Verify
        verify(ccaOfficialNoticeSendService, times(1))
                .sendOfficialNotice(attachments, request, new ArrayList<>());
    }
}
