package uk.gov.cca.api.web.controller.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FileEvidenceUploadControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/file-evidence/upload";

    private MockMvc mockMvc;

    @InjectMocks
    private FileEvidenceUploadController controller;

    @Mock
    private FileEvidenceService fileEvidenceService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    void setUp() {

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (FileEvidenceUploadController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }


    @Test
    void uploadEvidenceFile() throws Exception {

        final AppUser authUser = AppUser.builder().userId("id").build();
        final String evidenceName = "file";
        final String evidenceOriginalFileName = "filename.txt";
        final String evidenceContentType = "text/plain";
        final byte[] evidenceContent = "content".getBytes();

        final MockMultipartFile
                evidenceFile = new MockMultipartFile(evidenceName, evidenceOriginalFileName, evidenceContentType, evidenceContent);
        final FileDTO fileDTO = FileDTO.builder()
                .fileName(evidenceOriginalFileName)
                .fileType(evidenceContentType)
                .fileContent(evidenceContent)
                .fileSize(evidenceFile.getSize())
                .build();
        final UUID fileEvidenceUuid = UUID.randomUUID();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        when(fileEvidenceService.createFileEvidence(fileDTO, authUser))
                .thenReturn(fileEvidenceUuid.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart(CONTROLLER_PATH)
                                .file(evidenceFile)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(fileEvidenceUuid.toString()));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(fileEvidenceService, times(1)).createFileEvidence(fileDTO, authUser);
    }
}
