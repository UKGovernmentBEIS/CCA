package uk.gov.cca.api.web.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.netz.api.account.domain.dto.AccountContactDTO;
import uk.gov.netz.api.account.domain.dto.AccountContactVbInfoDTO;
import uk.gov.netz.api.account.domain.dto.AccountContactVbInfoResponse;
import uk.gov.netz.api.account.service.AccountVbSiteContactService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.domain.TestEmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VbSiteContactControllerTest {

    private static final String VB_SITE_CONTACT_CONTROLLER_PATH = "/v1.0/vb-site-contacts";

    private MockMvc mockMvc;

    @InjectMocks
    private VbSiteContactController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AccountVbSiteContactService service;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (VbSiteContactController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getVbSiteContacts() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.VERIFIER).build();

        AccountContactVbInfoResponse accountVbSiteContactInfoResponse = AccountContactVbInfoResponse.builder()
                .contacts(List.of(
                        new AccountContactVbInfoDTO(1L, "accountName1", TestEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME, "userId1"),
                        new AccountContactVbInfoDTO(2L, "accountName2", TestEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME_2, "userId2")
                    ))
                .editable(false).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(service.getAccountsAndVbSiteContacts(user, 0, 2)).thenReturn(accountVbSiteContactInfoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(VB_SITE_CONTACT_CONTROLLER_PATH + "?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("contacts[0].accountId").value(1L))
                .andExpect(jsonPath("contacts[0].accountName").value("accountName1"))
                .andExpect(jsonPath("contacts[0].type").value("DUMMY"))
                .andExpect(jsonPath("contacts[0].userId").value("userId1"))
                .andExpect(jsonPath("contacts[1].accountId").value(2L))
                .andExpect(jsonPath("contacts[1].accountName").value("accountName2"))
                .andExpect(jsonPath("contacts[1].type").value("DUMMY2"))
                .andExpect(jsonPath("contacts[1].userId").value("userId2"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(service, times(1)).getAccountsAndVbSiteContacts(user,0, 2);
    }

    @Test
    void getVbSiteContacts_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.VERIFIER).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(user, new String[] {RoleTypeConstants.VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders.get(VB_SITE_CONTACT_CONTROLLER_PATH + "?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(service, never()).getAccountsAndVbSiteContacts(any(), anyInt(), anyInt());
    }

    @Test
    void updateVbSiteContacts() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.VERIFIER).build();
        List<AccountContactDTO> accountVbSiteContacts = List.of(
                AccountContactDTO.builder().accountId(1L).userId("userId1").build(),
                AccountContactDTO.builder().accountId(2L).userId("userId2").build()
        );

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post(VB_SITE_CONTACT_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountVbSiteContacts)))
                .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(service, times(1)).updateVbSiteContacts(user, accountVbSiteContacts);
    }

    @Test
    void updateVbSiteContacts_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.VERIFIER).build();
        List<AccountContactDTO> accountVbSiteContacts = List.of(
                AccountContactDTO.builder().accountId(1L).userId("userId1").build(),
                AccountContactDTO.builder().accountId(2L).userId("userId2").build()
        );

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "updateVbSiteContacts");

        mockMvc.perform(MockMvcRequestBuilders.post(VB_SITE_CONTACT_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountVbSiteContacts)))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(service, never()).updateVbSiteContacts(any(), anyList());
    }
}
