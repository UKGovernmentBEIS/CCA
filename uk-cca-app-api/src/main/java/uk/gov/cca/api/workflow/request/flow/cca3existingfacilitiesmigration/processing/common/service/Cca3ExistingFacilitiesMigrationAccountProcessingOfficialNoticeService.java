package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService {

    private final RequestService requestService;
    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    public CompletableFuture<FileInfoDTO> generateAcceptedOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();
        final String signatory = requestPayload.getDefaultSignatory();
        final int version = 1;

        TemplateParams documentTemplateParams = new TemplateParams();
        documentTemplateParams.getParams().put("version", "v" + version);

        return ccaFileDocumentGeneratorService.generateAsync(request, signatory,
                CcaDocumentTemplateType.MIGRATION_UNDERLYING_AGREEMENT_ACCEPTED_CCA3,
                documentTemplateParams,
                "CCA3 Migration Proposed underlying agreement cover letter.pdf");
    }

    public CompletableFuture<FileInfoDTO> generateActivatedOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        return ccaFileDocumentGeneratorService.generateAsync(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.EXISTING_FACILITIES_MIGRATION_UNDERLYING_AGREEMENT_ACTIVATED_CCA3,
                CcaDocumentTemplateType.MIGRATION_UNDERLYING_AGREEMENT_ACTIVATED_CCA3,
                "CCA3 Migration Activated underlying agreement cover letter.pdf");
    }

    public void sendOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();

        List<FileInfoDTO> attachments = List.of(
                requestPayload.getUnderlyingAgreementDocument(),
                requestPayload.getOfficialNotice()
        );

        ccaOfficialNoticeSendService.sendOfficialNotice(attachments, request, new ArrayList<>());
    }
}
