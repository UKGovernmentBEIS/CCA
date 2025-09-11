package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.documenttemplate.service.FileDocumentGenerateServiceDelegator;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CcaFileDocumentGeneratorService {

    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final FileDocumentGenerateServiceDelegator fileDocumentGenerateServiceDelegator;
    private final CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;
    private final CcaDocumentTemplateCommonParamsProvider documentTemplateCommonParamsProvider;

    public FileInfoDTO generate(final Request request, final String signatory,
                                String type, String documentTemplateType, String fileNameToGenerate) {
        TemplateParams templateParams = constructTemplateParams(request, signatory, type, null);

        return fileDocumentGenerateServiceDelegator.generateAndSaveFileDocument(documentTemplateType, templateParams, fileNameToGenerate);
    }

    public FileInfoDTO generate(final Request request, final CcaDecisionNotification decisionNotification,
                                String type, String documentTemplateType, String fileNameToGenerate) {
        TemplateParams templateParams = constructTemplateParams(request, decisionNotification, type, null);

        return fileDocumentGenerateServiceDelegator.generateAndSaveFileDocument(documentTemplateType, templateParams, fileNameToGenerate);
    }
    
    public CompletableFuture<FileInfoDTO> generateAsync(final Request request, final CcaDecisionNotification decisionNotification,
            String type, String documentTemplateType, String fileNameToGenerate) {
		
		return generateAsync(request, decisionNotification, type, documentTemplateType, fileNameToGenerate, null);
	}

    public CompletableFuture<FileInfoDTO> generateAsync(final Request request, final CcaDecisionNotification decisionNotification,
                                                        String type, String documentTemplateType, String fileNameToGenerate, SchemeVersion version) {
    	TemplateParams templateParams = constructTemplateParams(request, decisionNotification, type, version);
        
        return fileDocumentGenerateServiceDelegator.generateAndSaveFileDocumentAsync(documentTemplateType, templateParams, fileNameToGenerate);
    }

    public CompletableFuture<FileInfoDTO> generateAsync(final String signatory, final Long sectorAssociationId, final String documentTemplateType, TemplateParams documentTemplateParams, String fileNameToGenerate) {
        final TemplateParams templateParams = documentTemplateCommonParamsProvider.getSectorAndCaAndSignatoryTemplateParams(signatory, sectorAssociationId, documentTemplateParams);

        return fileDocumentGenerateServiceDelegator.generateAndSaveFileDocumentAsync(documentTemplateType, templateParams, fileNameToGenerate);
    }

    public CompletableFuture<FileInfoDTO> generateAsync(final Request request, String signatory, final String documentTemplateType, TemplateParams documentTemplateParams, String fileNameToGenerate) {
        final TemplateParams commonTemplateParams = constructTemplateParams(request, signatory, null, null);

        commonTemplateParams.getParams().putAll(documentTemplateParams.getParams());

        final TemplateParams templateParams = TemplateParams.builder()
                .competentAuthorityParams(commonTemplateParams.getCompetentAuthorityParams())
                .signatoryParams(commonTemplateParams.getSignatoryParams())
                .accountParams(commonTemplateParams.getAccountParams())
                .params(commonTemplateParams.getParams())
                .build();
        return fileDocumentGenerateServiceDelegator.generateAndSaveFileDocumentAsync(documentTemplateType, templateParams, fileNameToGenerate);
    }

    private TemplateParams constructTemplateParams(final Request request, final CcaDecisionNotification decisionNotification, String type, SchemeVersion version) {
        DocumentTemplateParamsSourceData params = DocumentTemplateParamsSourceData.builder()
                .contextActionType(type)
                .request(request)
                .signatory(decisionNotification.getDecisionNotification().getSignatory())
                .ccRecipientsEmails(this.ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification))
                .build();
        TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(params);
        templateParams.getParams().putAll(documentTemplateCommonParamsProvider.getSectorTemplateParams(request, version));
        return templateParams;
    }

    private TemplateParams constructTemplateParams(final Request request, final String signatory, String type, SchemeVersion version) {
        DocumentTemplateParamsSourceData params = DocumentTemplateParamsSourceData.builder()
                .contextActionType(type)
                .request(request)
                .signatory(signatory)
                .build();
        TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(params);
        templateParams.getParams().putAll(documentTemplateCommonParamsProvider.getSectorTemplateParams(request, version));
        return templateParams;
    }
}
