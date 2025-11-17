package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeAccountProcessingOfficialNoticeService {

    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    public CompletableFuture<FileInfoDTO> generateExtensionNotice(final Request request, final int version) {
        final Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload =
                (Cca2ExtensionNoticeAccountProcessingRequestPayload) request.getPayload();
        final String signatory = requestPayload.getDefaultSignatory();

        TemplateParams documentTemplateParams = new TemplateParams();
        documentTemplateParams.getParams().put("version", "v" + version);

        return ccaFileDocumentGeneratorService.generateAsync(request, signatory,
                CcaDocumentTemplateType.EXTENSION_NOTICE_CCA2,
                documentTemplateParams,
                "CCA2 Extension Activated underlying agreement cover letter.pdf");
    }

    public void sendOfficialNotice(final Request request) {
        final Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload =
                (Cca2ExtensionNoticeAccountProcessingRequestPayload) request.getPayload();

        List<FileInfoDTO> attachments = List.of(
                requestPayload.getUnderlyingAgreementDocument(),
                requestPayload.getOfficialNotice()
        );

        ccaOfficialNoticeSendService.sendOfficialNotice(attachments, request, new ArrayList<>());
    }
}
