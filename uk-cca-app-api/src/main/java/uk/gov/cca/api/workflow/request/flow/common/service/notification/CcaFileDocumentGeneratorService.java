package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.documenttemplate.service.DocumentFileGeneratorService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CcaFileDocumentGeneratorService {

    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;
    private final CcaDocumentTemplateCommonParamsProvider documentTemplateCommonParamsProvider;

    public FileInfoDTO generate(final Request request, final CcaDecisionNotification decisionNotification,
                                String type, String documentTemplateType, String fileNameToGenerate) {
        TemplateParams templateParams = constructTemplateParams(request, decisionNotification, type);

        return documentFileGeneratorService.generateAndSaveFileDocument(documentTemplateType, templateParams, fileNameToGenerate);
    }

    public CompletableFuture<FileInfoDTO> generateAsync(final Request request, final CcaDecisionNotification decisionNotification,
                                                        String type, String documentTemplateType, String fileNameToGenerate) {
        TemplateParams templateParams = constructTemplateParams(request, decisionNotification, type);

        return documentFileGeneratorService.generateAndSaveFileDocumentAsync(documentTemplateType, templateParams, fileNameToGenerate);
    }

    public CompletableFuture<FileInfoDTO> generateAsync(final String signatory, final Long sectorAssociationId, final String documentTemplateType, TemplateParams documentTemplateParams, String fileNameToGenerate) {
        final TemplateParams templateParams = documentTemplateCommonParamsProvider.getSectorTemplateParams(signatory, sectorAssociationId, documentTemplateParams);

        return documentFileGeneratorService.generateAndSaveFileDocumentAsync(documentTemplateType, templateParams, fileNameToGenerate);
    }

    public CompletableFuture<FileInfoDTO> generateAsync(final Request request, String signatory, final String documentTemplateType, TemplateParams documentTemplateParams, String fileNameToGenerate) {
        final TemplateParams commonTemplateParams = constructTemplateParams(request, signatory);

        commonTemplateParams.getParams().putAll(documentTemplateParams.getParams());

        final TemplateParams templateParams = TemplateParams.builder()
                .competentAuthorityParams(commonTemplateParams.getCompetentAuthorityParams())
                .signatoryParams(commonTemplateParams.getSignatoryParams())
                .accountParams(commonTemplateParams.getAccountParams())
                .params(commonTemplateParams.getParams())
                .build();
        return documentFileGeneratorService.generateAndSaveFileDocumentAsync(documentTemplateType, templateParams, fileNameToGenerate);
    }

    private TemplateParams constructTemplateParams(final Request request, final CcaDecisionNotification decisionNotification, String type) {
        DocumentTemplateParamsSourceData params = DocumentTemplateParamsSourceData.builder()
                .contextActionType(type)
                .request(request)
                .signatory(decisionNotification.getDecisionNotification().getSignatory())
                .ccRecipientsEmails(this.ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification))
                .build();
        return documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(params);
    }

    private TemplateParams constructTemplateParams(final Request request, final String signatory) {
        DocumentTemplateParamsSourceData params = DocumentTemplateParamsSourceData.builder()
                .request(request)
                .signatory(signatory)
                .build();
        return documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(params);
    }
}
