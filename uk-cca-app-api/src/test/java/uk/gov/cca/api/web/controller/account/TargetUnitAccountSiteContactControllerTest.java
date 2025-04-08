package uk.gov.cca.api.web.controller.account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountSiteContactDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountSiteContactService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
public class TargetUnitAccountSiteContactControllerTest {

    private static final String BASE_PATH = "/v1.0/account-site-contacts";

    private MockMvc mockMvc;

    @InjectMocks
    private TargetUnitAccountSiteContactController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (TargetUnitAccountSiteContactController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setConversionService(conversionService)
            .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void updateTargetUnitAccountSiteContacts() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final Long sectorAssociationId = 1L;
        List<TargetUnitAccountSiteContactDTO> siteContacts = List.of(
            TargetUnitAccountSiteContactDTO.builder().accountId(1L).userId("userId1").build(),
            TargetUnitAccountSiteContactDTO.builder().accountId(2L).userId("userId2").build()
        );

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(post(BASE_PATH  + "/sector-association/" + sectorAssociationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(siteContacts)))
            .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(targetUnitAccountSiteContactService, times(1)).updateTargetUnitAccountSiteContacts(user, sectorAssociationId, siteContacts);
    }

    @Test
    void updateTargetUnitAccountSiteContacts_forbidden() throws Exception {
        final Long sectorAssociationId = 1L;
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();
        List<TargetUnitAccountSiteContactDTO> siteContacts = List.of(
            TargetUnitAccountSiteContactDTO.builder().accountId(1L).userId("userId1").build(),
            TargetUnitAccountSiteContactDTO.builder().accountId(2L).userId("userId2").build()
        );

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "updateTargetUnitAccountSiteContacts", Long.toString(sectorAssociationId), null, null);

        mockMvc.perform(post(BASE_PATH + "/sector-association/" + sectorAssociationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(siteContacts)))
            .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(targetUnitAccountSiteContactService, never()).updateTargetUnitAccountSiteContacts(any(), any(), anyList());
    }
}