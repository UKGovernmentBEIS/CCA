package uk.gov.cca.api.notification.template.service;

import fr.opensagres.xdocreport.template.freemarker.FreemarkerTemplateEngine;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.notification.template.service.DocumentGeneratorRemoteClientService;
import uk.gov.cca.api.notification.template.service.DocumentTemplateProcessService;
import uk.gov.netz.api.common.utils.MimeTypeUtils;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.competentauthority.CompetentAuthorityService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.cca.api.notification.template.TemplatesConfiguration;
import uk.gov.cca.api.notification.template.domain.dto.templateparams.AccountTemplateParams;
import uk.gov.cca.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.cca.api.notification.template.domain.dto.templateparams.SignatoryTemplateParams;
import uk.gov.cca.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.cca.api.notification.template.domain.dto.templateparams.WorkflowTemplateParams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateProcessServiceTest {
    private static FreemarkerTemplateEngine freemarkerTemplateEngine;

    @Mock
    private DocumentGeneratorRemoteClientService documentGeneratorClientService;

    @BeforeAll
    public static void init() {
        TemplatesConfiguration templatesConfiguration = new TemplatesConfiguration();
        freemarker.template.Configuration freemarkerConfig = templatesConfiguration.freemarkerConfig();
        freemarkerTemplateEngine = templatesConfiguration.freemarkerTemplateEngine(freemarkerConfig);
    }

    @Test
    void generateFileDocumentFromTemplate_rfi_template() throws Exception {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        String fileNameToGenerate = "fileNameToGenerate";
        String signatoryUser = "Signatory user full name";
        Path templateFilePath = Paths.get("src", "test", "resources", "templates", "L025_P3_Request_for_further_information_notice_20130402.docx");
        FileDTO templateFile = createFile(templateFilePath);

        Path signatureFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        FileDTO signatureFile = createFile(signatureFilePath);

        Map<String, Object> params = new HashMap<>();
        Date deadlineDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        params.put("deadline", deadlineDate);
        params.put("questions", List.of("question1", "question2"));

        TemplateParams templateParams = buildTemplateParams(ca, signatoryUser, signatureFile, params);

        byte[] resultExpected = "some bytes".getBytes();
        when(documentGeneratorClientService.generateDocument(Mockito.any(byte[].class), Mockito.eq(fileNameToGenerate))).thenReturn(resultExpected);

        byte[] resultActual = new DocumentTemplateProcessService(documentGeneratorClientService, freemarkerTemplateEngine)
                .generateFileDocumentFromTemplate(templateFile, templateParams, fileNameToGenerate);

        assertThat(resultActual).isEqualTo(resultExpected);

        ArgumentCaptor<byte[]> postProcessedDocumentCaptor = ArgumentCaptor.forClass(byte[].class);

        verify(documentGeneratorClientService, times(1)).generateDocument(postProcessedDocumentCaptor.capture(), eq(fileNameToGenerate));

        byte[] postProcessedDocument = postProcessedDocumentCaptor.getValue();

        try (InputStream bais = new ByteArrayInputStream(postProcessedDocument);
             XWPFDocument document = new XWPFDocument(bais);
             XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(document)) {
            final String docText = xwpfWordExtractor.getText();
            assertThat(docText).contains(templateParams.getPermitId());
            assertThat(docText).contains(templateParams.getCompetentAuthorityParams().getName());
            assertThat(docText).contains(templateParams.getSignatoryParams().getFullName());
            assertThat(docText).contains("question1");
            assertThat(docText).contains("question2");
            assertThat(docText).contains(new SimpleDateFormat("dd MMMM yyyy").format(deadlineDate));
        }
    }

    private TemplateParams buildTemplateParams(CompetentAuthorityEnum ca, String signatoryUser, FileDTO signatureFile,
                                               Map<String, Object> params) {
        CompetentAuthorityDTO caDto = CompetentAuthorityDTO.builder().id(ca).email("email").name("name").build();
        AccountTemplateParams accountParams = Mockito.mock(AccountTemplateParams.class);
        return TemplateParams.builder()
                .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                        .competentAuthority(caDto)
                        .logo(CompetentAuthorityService.getCompetentAuthorityLogo(ca))
                        .build())
                .competentAuthorityCentralInfo("ca central info")
                .signatoryParams(SignatoryTemplateParams.builder()
                        .fullName(signatoryUser)
                        .signature(signatureFile.getFileContent())
                        .jobTitle("Project Manager")
                        .build())
                .accountParams(accountParams)
                .permitId("UK-E-IN-12345")
                .workflowParams(WorkflowTemplateParams.builder()
                        .requestId("123")
                        .requestType("PERMIT_VARIATION") //("PERMIT_ISSUANCE")
                        .requestTypeInfo("your permit variation")
                        .requestSubmissionDate(new Date())
                        .requestEndDate(LocalDateTime.of(1998, 1, 1, 1, 1))
                        .build())
                .params(params)
                .build();
    }

    private FileDTO createFile(Path sampleFilePath) throws IOException {
        byte[] bytes = Files.readAllBytes(sampleFilePath);
        return FileDTO.builder()
                .fileContent(bytes)
                .fileName(sampleFilePath.getFileName().toString())
                .fileSize(sampleFilePath.toFile().length())
                .fileType(MimeTypeUtils.detect(bytes, sampleFilePath.getFileName().toString()))
                .build();
    }
}
