package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.documenttemplate.service.DocumentFileGeneratorService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;

@Service
@RequiredArgsConstructor
public class CcaFileDocumentGeneratorService {

    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;

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

	private TemplateParams constructTemplateParams(final Request request,
			final CcaDecisionNotification decisionNotification, String type) {
		DocumentTemplateParamsSourceData params = DocumentTemplateParamsSourceData.builder()
			.contextActionType(type)
			.request(request)
			.signatory(decisionNotification.getDecisionNotification().getSignatory())
			.ccRecipientsEmails(this.ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification))
			.build();
		return documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(params);
	}
}
