package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateCommonParamsProvider;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService {

    private final RequestService requestService;
    private final CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider;
    private final CcaDocumentTemplateCommonParamsProvider ccaDocumentTemplateCommonParamsProvider;
    private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;

    public CompletableFuture<FileInfoDTO> createUnderlyingAgreementDocument(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata requestMetadata =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata) request.getMetadata();
        final boolean isFinal = requestPayload.getActivationDetails() != null;
        final String signatory = isFinal
                ? requestPayload.getDecisionNotification().getDecisionNotification().getSignatory()
                : requestPayload.getDefaultSignatory();
        final TargetUnitAccountDetails accountDetails = requestPayload.getAccountReferenceData().getTargetUnitAccountDetails();
        final TemplateParams documentTemplateParams = constructDocumentTemplateParams(requestPayload, accountDetails, isFinal);

        return ccaFileDocumentGeneratorService.generateAsync(request, signatory,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA3, documentTemplateParams,
                SchemeVersion.CCA_3, constructFileName(requestMetadata.getAccountBusinessId(), isFinal));
    }

    private TemplateParams constructDocumentTemplateParams(final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload,
                                                           TargetUnitAccountDetails accountDetails, boolean isFinalDocument) {
        TemplateParams documentTemplateParams = new TemplateParams();
        documentTemplateParams.getParams().putAll(ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider
                .constructTemplateParams(
                        requestPayload.getUnderlyingAgreement(),
                        isFinalDocument? documentTemplateTransformationMapper.formatCurrentDate() : null,
                        SchemeVersion.CCA_3,
                        1));
        documentTemplateParams.getParams().putAll(ccaDocumentTemplateCommonParamsProvider
                .constructTargetUnitDetailsParams(accountDetails));

        return documentTemplateParams;
    }

    private String constructFileName(final String businessId, boolean isFinalDocument) {
        return String.format("%s CCA3 Underlying Agreement v1%s.pdf", businessId, isFinalDocument ? "" : " [proposed]");
    }
}
