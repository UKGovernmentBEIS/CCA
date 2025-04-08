package uk.gov.cca.api.web.controller.underlyingagreement;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementDocumentService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.token.FileToken;


@ExtendWith(MockitoExtension.class)
public class UnderlyingAgreementControllerTest {
    private static final String CONTROLLER_PATH = "/v1.0/underlying-agreements";
    
    private MockMvc mockMvc;

    @InjectMocks
    private UnderlyingAgreementController controller;
    
    @Mock
    private UnderlyingAgreementDocumentService underlyingAgreementDocumentService;
    
    @Mock
    private AppSecurityComponent appSecurityComponent;
    
    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;
    
    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (UnderlyingAgreementController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
            .setConversionService(conversionService)
                .build();
    }

    @Test
    void generateGetUnderlyingAgreementDocumentToken() throws Exception {
        Long underlyingAgreementId = 1L;
        UUID fileDocumentUuid = UUID.randomUUID();
        FileToken expectedToken = FileToken.builder().token("token").build();

        when(underlyingAgreementDocumentService.generateGetFileDocumentToken(underlyingAgreementId, fileDocumentUuid))
            .thenReturn(expectedToken);

        mockMvc.perform(MockMvcRequestBuilders
                .get(CONTROLLER_PATH + "/" + String.valueOf(underlyingAgreementId) + "/document")
                .param("fileDocumentUuid", fileDocumentUuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(expectedToken.getToken()));

        verify(underlyingAgreementDocumentService, times(1)).generateGetFileDocumentToken(underlyingAgreementId, fileDocumentUuid);
    }

    @Test
    void generateGetUnderlyingAgreementDocumentToken_forbidden() throws Exception {
        Long underlyingAgreementId = 1L;
        UUID documentUuid = UUID.randomUUID();
        final AppUser user = AppUser.builder().userId("userId").build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "generateGetUnderlyingAgreementDocumentToken", String.valueOf(underlyingAgreementId), null, null);

        mockMvc.perform(MockMvcRequestBuilders
                .get(CONTROLLER_PATH + "/" + String.valueOf(underlyingAgreementId) + "/document")
                .param("fileDocumentUuid", documentUuid.toString()))
            .andExpect(status().isForbidden());

        verifyNoInteractions(underlyingAgreementDocumentService);
    }
    
}
