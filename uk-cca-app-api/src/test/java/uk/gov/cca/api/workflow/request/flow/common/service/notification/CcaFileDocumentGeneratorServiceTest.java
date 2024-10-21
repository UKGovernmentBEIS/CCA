package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.documenttemplate.service.DocumentFileGeneratorService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaFileDocumentGeneratorServiceTest {

    @InjectMocks
    private CcaFileDocumentGeneratorService ccaOfficialNoticeGeneratorService;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;

    @Test
    void generate() {
        final Request request = Request.builder().id("request-id").build();
        final String signatory = "signatory";
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .decisionNotification(DecisionNotification.builder().signatory(signatory).build())
                .build();
        final String type = CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINALISED;
        final String documentTemplateType = CcaDocumentTemplateType.ADMIN_TERMINATION_ADMINISTRATIVE_SUBMITTED;
        final String fileNameToGenerate = "Termination notice.pdf";

        final List<String> recipients = List.of("sector@example.com");
        final DocumentTemplateParamsSourceData params = DocumentTemplateParamsSourceData.builder()
                .contextActionType(type)
                .request(request)
                .signatory(signatory)
                .ccRecipientsEmails(recipients)
                .build();
        final TemplateParams templateParams = TemplateParams.builder()
                .params(Map.of("param", "param"))
                .build();

        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(params))
                .thenReturn(templateParams);
        when(ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification))
                .thenReturn(recipients);
        when(documentFileGeneratorService.generateAndSaveFileDocument(documentTemplateType, templateParams, fileNameToGenerate))
                .thenReturn(FileInfoDTO.builder().build());

        // Invoke
        ccaOfficialNoticeGeneratorService.generate(request, decisionNotification, type, documentTemplateType, fileNameToGenerate);

        // Verify
        verify(documentTemplateOfficialNoticeParamsProvider, times(1))
                .constructTemplateParams(params);
        verify(ccaDecisionNotificationUsersService, times(1))
                .findCCUserEmails(decisionNotification);
        verify(documentFileGeneratorService, times(1))
                .generateAndSaveFileDocument(documentTemplateType, templateParams, fileNameToGenerate);
    }
    
    @Test
    void generateAsync() {
        final Request request = Request.builder().id("request-id").build();
        final String signatory = "signatory";
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .decisionNotification(DecisionNotification.builder().signatory(signatory).build())
                .build();
        final String type = CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACCEPTED;
        final String documentTemplateType = CcaDocumentTemplateType.UNDERLYING_AGREEMENT_ACCEPTED;
        final String fileNameToGenerate = "una.pdf";

        final List<String> recipients = List.of("sector@example.com");
        final DocumentTemplateParamsSourceData params = DocumentTemplateParamsSourceData.builder()
                .contextActionType(type)
                .request(request)
                .signatory(signatory)
                .ccRecipientsEmails(recipients)
                .build();
        final TemplateParams templateParams = TemplateParams.builder()
                .params(Map.of("param", "param"))
                .build();

        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(params))
                .thenReturn(templateParams);
        when(ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification))
                .thenReturn(recipients);
        when(documentFileGeneratorService.generateAndSaveFileDocumentAsync(documentTemplateType, templateParams, fileNameToGenerate))
                .thenReturn(CompletableFuture.completedFuture(FileInfoDTO.builder().build()));

        // Invoke
        ccaOfficialNoticeGeneratorService.generateAsync(request, decisionNotification, type, documentTemplateType, fileNameToGenerate);

        // Verify
        verify(documentTemplateOfficialNoticeParamsProvider, times(1))
                .constructTemplateParams(params);
        verify(ccaDecisionNotificationUsersService, times(1))
                .findCCUserEmails(decisionNotification);
        verify(documentFileGeneratorService, times(1))
                .generateAndSaveFileDocumentAsync(documentTemplateType, templateParams, fileNameToGenerate);
    }
}
