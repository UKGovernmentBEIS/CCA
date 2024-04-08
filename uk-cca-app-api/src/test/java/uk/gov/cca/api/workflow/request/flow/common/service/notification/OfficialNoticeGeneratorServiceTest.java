package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.OfficialNoticeGeneratorService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.cca.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.cca.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.cca.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfficialNoticeGeneratorServiceTest {

    @InjectMocks
    private OfficialNoticeGeneratorService officialNoticeGeneratorService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void generate() {
        Request request = Request.builder().build();
        DocumentTemplateGenerationContextActionType documentTemplateContextActionType =
            DocumentTemplateGenerationContextActionType.RDE_SUBMIT;
        DocumentTemplateType documentTemplateType = DocumentTemplateType.IN_RDE;
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        List<String> decisionNotificationUserEmails = List.of("operator@netz.uk");
        String filename = "rde.pdf";
        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@netz.uk").build();
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(documentTemplateContextActionType)
                .request(request)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContactInfo)
                .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                .ccRecipientsEmails(decisionNotificationUserEmails)
                .build();
        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
            .name("offDoc.pdf")
            .uuid(UUID.randomUUID().toString())
            .build();

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(decisionNotificationUserEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(documentTemplateType, templateParams, filename))
            .thenReturn(officialNotice);

        FileInfoDTO generatedOfficialNotice =
            officialNoticeGeneratorService.generate(request, documentTemplateContextActionType, documentTemplateType,
                decisionNotification, filename);

        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1))
            .generateFileDocument(documentTemplateType, templateParams, filename);

        assertEquals(officialNotice, generatedOfficialNotice);
    }
}