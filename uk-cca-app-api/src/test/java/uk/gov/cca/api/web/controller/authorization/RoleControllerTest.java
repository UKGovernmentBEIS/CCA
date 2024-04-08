package uk.gov.cca.api.web.controller.authorization;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.cca.api.authorization.core.service.RoleService;
import uk.gov.cca.api.authorization.regulator.domain.RegulatorPermissionLevel;
import uk.gov.cca.api.authorization.regulator.domain.RegulatorRolePermissionsDTO;
import uk.gov.cca.api.authorization.regulator.service.RegulatorRoleService;
import uk.gov.cca.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.security.AppSecurityComponent;
import uk.gov.cca.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.cca.api.web.security.AuthorizedAspect;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.cca.api.authorization.regulator.domain.RegulatorPermissionLevel.NONE;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    private static final String BASE_PATH = "/v1.0/authorities";
    private static final String OPERATOR_ROLE_CODES_PATH = "operator-role-codes";
    private static final String REGULATOR_ROLES_PATH = "regulator-roles";
    private static final String VERIFIER_ROLE_CODES_PATH = "verifier-role-codes";

    private MockMvc mockMvc;

    @InjectMocks
    private RoleController roleController;

    @Mock
    private RoleService roleService;

    @Mock
    private Validator validator;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RegulatorRoleService regulatorRoleService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver =
            new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect authorizedAspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(roleController);
        aspectJProxyFactory.addAspect(authorizedAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        roleController = (RoleController) aopProxy.getProxy();
        mockMvc = MockMvcBuilders.standaloneSetup(roleController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setValidator(validator)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .build();
    }

    @Test
    void getOperatorRoleCodes() throws Exception {
        final long accountId = 1L;
        AppUser user = AppUser.builder().userId("authId").roleType(RoleType.OPERATOR).build();
        List<RoleDTO> roles = List.of(buildRole("code1"), buildRole("code2"));

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(roleService.getOperatorRoles()).thenReturn(roles);

        // Invoke
        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + "account" + "/" + accountId + "/" + OPERATOR_ROLE_CODES_PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value(roles.get(0).getCode()))
            .andExpect(jsonPath("$[1].code").value(roles.get(1).getCode()));
    }

    @Test
    void getOperatorRoleCodesForbidden() throws Exception {
        final long accountId = 1L;
        AppUser user = AppUser.builder().userId("authId").roleType(RoleType.OPERATOR).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "getOperatorRoleCodes", String.valueOf(accountId));

        // Invokeact_ru_event_subscr
        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + "account" + "/" + accountId + "/" + OPERATOR_ROLE_CODES_PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    void getRegulatorRolesForbidden() throws Exception {
        AppUser user = AppUser.builder().userId("authId").roleType(RoleType.OPERATOR).build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "getRegulatorRoles");

        // Invoke
        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + REGULATOR_ROLES_PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    void getRegulatorRoles() throws Exception {
        RegulatorRolePermissionsDTO regulatorRolePermissionsDTO =
            buildRolePermissionsDTO("code1", Map.of(MANAGE_USERS_AND_CONTACTS, NONE));

        when(regulatorRoleService.getRegulatorRoles()).thenReturn(List.of(regulatorRolePermissionsDTO));

        // Invoke
        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + REGULATOR_ROLES_PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$[0].code").value(regulatorRolePermissionsDTO.getCode()))
            .andExpect(jsonPath("$[0].rolePermissions", Matchers.hasKey(MANAGE_USERS_AND_CONTACTS)))
            .andExpect(jsonPath("$[0].rolePermissions", Matchers.hasValue(NONE.name())));
    }

    @Test
    void getVerifierRoleCodes() throws Exception {
        List<RoleDTO> verifierRoles = List.of(buildRole("verifier_code1"), buildRole("verifier_code2"));

        when(roleService.getVerifierRoleCodes()).thenReturn(verifierRoles);

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + VERIFIER_ROLE_CODES_PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value(verifierRoles.get(0).getCode()))
            .andExpect(jsonPath("$[1].code").value(verifierRoles.get(1).getCode()));
    }

    @Test
    void getVerifierRoleCodes_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("authId").roleType(RoleType.VERIFIER).build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "getVerifierRoleCodes");

        // Invoke
        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + VERIFIER_ROLE_CODES_PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    private RegulatorRolePermissionsDTO buildRolePermissionsDTO(String code,
                                                                Map<String, RegulatorPermissionLevel> rolePermissions) {

        return RegulatorRolePermissionsDTO.builder()
            .code(code)
            .rolePermissions(rolePermissions)
            .build();
    }

    private RoleDTO buildRole(String code) {
        return RoleDTO.builder()
            .code(code)
            .build();
    }

}
