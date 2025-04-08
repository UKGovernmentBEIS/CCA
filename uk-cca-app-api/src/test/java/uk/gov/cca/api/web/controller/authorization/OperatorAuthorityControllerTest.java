package uk.gov.cca.api.web.controller.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityService;
import uk.gov.cca.api.user.operator.domain.OperatorAuthoritiesInfoDTO;
import uk.gov.cca.api.user.operator.domain.OperatorAuthorityInfoDTO;
import uk.gov.cca.api.user.operator.service.OperatorUserAuthorityInfoService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.authorization.dto.AccountOperatorAuthorityUpdateWrapperDTO;
import uk.gov.cca.api.web.orchestrator.authorization.service.AccountOperatorUserAuthorityUpdateOrchestrator;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OperatorAuthorityControllerTest {

    private static final String BASE_PATH = "/v1.0/operator-authorities";
    public static final String ACCOUNT_OPERATOR_USERS_PATH = "/account";

    private MockMvc mockMvc;

    @InjectMocks
    private OperatorAuthorityController operatorAuthorityController;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private OperatorUserAuthorityInfoService operatorUserAuthorityInfoService;

    @Mock
    private AccountOperatorUserAuthorityUpdateOrchestrator accountOperatorUserAuthorityUpdateOrchestrator;

    @Mock
    private CcaOperatorAuthorityService ccaOperatorAuthorityService;

    @Mock
    private Validator validator;

    @Mock
    private AppSecurityComponent appSecurityComponent;
    
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(operatorAuthorityController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        operatorAuthorityController = (OperatorAuthorityController) aopProxy.getProxy();
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(operatorAuthorityController)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setValidator(validator)
            .build();
    }
    
    @Test
    void getAccountOperatorAuthorities() throws Exception {
    	AppUser user = AppUser.builder().userId("current_user").build();
        OperatorAuthorityInfoDTO operatorAuthorityInfoDTO = OperatorAuthorityInfoDTO.builder()
                .contactType(ContactType.OPERATOR.getName())
    			.userId("user")
    			.firstName("fn")
    			.lastName("ln")
    			.roleName("Operator")
    			.build();
    	
    	when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(operatorUserAuthorityInfoService.getOperatorAuthoritiesInfo(user, 1L))
        	.thenReturn(OperatorAuthoritiesInfoDTO.builder()
        			.authorities(List.of(operatorAuthorityInfoDTO))
        			.editable(true)
        			.build());
        
        //invoke
        mockMvc.perform(
        		MockMvcRequestBuilders.get(BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH + "/" + 1L)
        							.contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.editable").value(Boolean.TRUE))
            .andExpect(jsonPath("$.authorities[0].userId").value(operatorAuthorityInfoDTO.getUserId()));
    
	    verify(operatorUserAuthorityInfoService, times(1)).getOperatorAuthoritiesInfo(user, 1L);
    }
    
    @Test
    void getAccountOperatorAuthorities_forbidden() throws Exception {
    	AppUser user = AppUser.builder().userId("currentuser").build();
    	long accountId = 1L;

    	when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "getAccountOperatorAuthorities", String.valueOf(accountId), null, null);
        
        mockMvc.perform(
	        		MockMvcRequestBuilders.get(BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH + "/" + accountId)
	        							.contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isForbidden());
          
	    verify(appSecurityComponent, times(1)).getAuthenticatedUser();
    }

    @Test
    void updateAccountOperatorAuthorities() throws Exception {
        AppUser currentUser = AppUser.builder().userId("currentuser").build();

        List<AccountOperatorAuthorityUpdateDTO> accountUsers = List.of(
            AccountOperatorAuthorityUpdateDTO.builder().userId("1").roleCode("role1").authorityStatus(AuthorityStatus.ACTIVE).build(),
            AccountOperatorAuthorityUpdateDTO.builder().userId("2").roleCode("invalid_role").authorityStatus(AuthorityStatus.ACTIVE).build()
        );

        AccountOperatorAuthorityUpdateWrapperDTO wrapper =
                AccountOperatorAuthorityUpdateWrapperDTO.builder()
                    .accountOperatorAuthorityUpdateList(accountUsers)
                    .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.patch(BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH + "/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrapper))
        )
            .andExpect(status().isNoContent());

        verify(accountOperatorUserAuthorityUpdateOrchestrator, times(1))
                .updateAccountOperatorAuthorities(accountUsers, 1L);
    }

    @Test
    void updateAccountOperatorAuthorities_forbidden() throws Exception {
        AppUser currentUser = AppUser.builder().userId("currentuser").build();
        long accountId = 1L;

        List<AccountOperatorAuthorityUpdateDTO> accountUsers = List.of(
            AccountOperatorAuthorityUpdateDTO.builder().userId("1").roleCode("role1").authorityStatus(AuthorityStatus.ACTIVE).build(),
            AccountOperatorAuthorityUpdateDTO.builder().userId("2").roleCode("invalid_role").authorityStatus(AuthorityStatus.ACTIVE).build()
        );
        AccountOperatorAuthorityUpdateWrapperDTO wrapper =
                AccountOperatorAuthorityUpdateWrapperDTO.builder()
                    .accountOperatorAuthorityUpdateList(accountUsers)
                    .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(currentUser, "updateAccountOperatorAuthorities", String.valueOf(accountId), null, null);

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.patch(BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH + "/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrapper))
        )
            .andExpect(status().isForbidden());

        verify(accountOperatorUserAuthorityUpdateOrchestrator, never())
                .updateAccountOperatorAuthorities(Mockito.anyList(), anyLong());
    }

    @Test
    void deleteAccountOperatorAuthority() throws Exception {
        AppUser currentUser = AppUser.builder().userId("currentuser").build();
        long accountId = 1L;
        String userId = "userId";

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.delete(
                BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH  + "/" + accountId + "/" + userId))
            .andExpect(status().isNoContent());

        verify(ccaOperatorAuthorityService, times(1))
                .deleteAccountOperatorAuthority(userId, accountId);
    }

    @Test
    void deleteAccountOperatorAuthority_forbidden() throws Exception {
        AppUser currentUser = AppUser.builder().userId("currentuser").build();
        long accountId = 1L;
        String userId = "userId";

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(currentUser, "deleteAccountOperatorAuthority", String.valueOf(accountId), null, null);

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.delete(
                BASE_PATH + ACCOUNT_OPERATOR_USERS_PATH  + "/" + accountId + "/" + userId))
            .andExpect(status().isForbidden());

        verify(ccaOperatorAuthorityService, never())
                .deleteAccountOperatorAuthority(anyString(), anyLong());
    }
}
