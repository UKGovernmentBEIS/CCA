package uk.gov.cca.api.web.controller.sectorassociation;

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
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeDocumentService;
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

@ExtendWith({MockitoExtension.class})
class SectorAssociationSchemeUploadControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/sector-scheme-document/upload";

    private MockMvc mockMvc;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private SectorAssociationSchemeDocumentService sectorAssociationSchemeDocumentService;

    @InjectMocks
    private SectorAssociationSchemeUploadController controller;

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
        controller = (SectorAssociationSchemeUploadController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void uploadSectorSchemeDocumentFile() throws Exception {
        final AppUser authUser = AppUser.builder().userId("id").build();
        final String documentName = "file";
        final String documentOriginalFileName = "filename.txt";
        final String documentContentType = "text/plain";
        final byte[] documentContent = "content".getBytes();

        final MockMultipartFile
                documentFile = new MockMultipartFile(documentName, documentOriginalFileName, documentContentType, documentContent);
        final FileDTO fileDTO = FileDTO.builder()
                .fileName(documentOriginalFileName)
                .fileType(documentContentType)
                .fileContent(documentContent)
                .fileSize(documentFile.getSize())
                .build();
        final UUID documentUuid = UUID.randomUUID();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        when(sectorAssociationSchemeDocumentService.createSectorAssociationSchemeDocument(fileDTO, authUser))
                .thenReturn(documentUuid.toString());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart(CONTROLLER_PATH)
                                .file(documentFile)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(documentUuid.toString()));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(sectorAssociationSchemeDocumentService, times(1)).createSectorAssociationSchemeDocument(fileDTO, authUser);
    }
}
