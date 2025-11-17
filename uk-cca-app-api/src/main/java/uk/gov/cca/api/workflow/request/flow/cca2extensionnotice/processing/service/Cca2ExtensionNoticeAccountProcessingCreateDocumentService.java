package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateCommonParamsProvider;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeAccountProcessingCreateDocumentService {

    private final CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider;
    private final CcaDocumentTemplateCommonParamsProvider ccaDocumentTemplateCommonParamsProvider;
    private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;

    public CompletableFuture<FileInfoDTO> createUnderlyingAgreementDocument(final Request request,
                                                                            final Cca2ExtensionNoticeAccountState accountState,
                                                                            final int version) {
        final Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload =
                (Cca2ExtensionNoticeAccountProcessingRequestPayload) request.getPayload();
        final String signatory = requestPayload.getDefaultSignatory();
        final TemplateParams documentTemplateParams = constructDocumentTemplateParams(requestPayload, version);

        return ccaFileDocumentGeneratorService.generateAsync(request, signatory,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA2,
                documentTemplateParams, constructFileName(accountState.getAccountBusinessId(), version));
    }

    private TemplateParams constructDocumentTemplateParams(final Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload, final int version) {
        TemplateParams documentTemplateParams = new TemplateParams();
        documentTemplateParams.getParams().putAll(ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider
                .constructTemplateParams(
                        requestPayload.getUnderlyingAgreement(),
                        documentTemplateTransformationMapper.formatCurrentDate(),
                        SchemeVersion.CCA_2,
                        version));
        documentTemplateParams.getParams().putAll(ccaDocumentTemplateCommonParamsProvider
                .constructTargetUnitDetailsParams(requestPayload.getAccountReferenceData().getTargetUnitAccountDetails()));

        return documentTemplateParams;
    }

    private String constructFileName(final String businessId, int version) {
        return String.format("%s CCA2 Underlying Agreement v%d.pdf", businessId, version);
    }
}
