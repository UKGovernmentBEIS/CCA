package uk.gov.cca.api.web.controller.authorization;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaRoleService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class CcaRoleControllerTest {

    private static final String BASE_PATH = "/v1.0/cca-authorities";
    private static final String SECTOR_USER_ROLES_PATH = "/sector-user-roles";

    private MockMvc mockMvc;

    @InjectMocks
    private CcaRoleController ccaRoleController;

    @Mock
    private Validator validator;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private CcaRoleService ccaRoleService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver =
                new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect authorizedAspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(ccaRoleController);
        aspectJProxyFactory.addAspect(authorizedAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        ccaRoleController = (CcaRoleController) aopProxy.getProxy();
        mockMvc = MockMvcBuilders.standaloneSetup(ccaRoleController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setValidator(validator)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .build();
    }

    @Test
    void getSectorUserRoles() throws Exception {
        final long sectorId = 1L;
        AppUser user = AppUser.builder().userId("authId").roleType(SECTOR_USER).build();
        List<RoleDTO> roles = List.of(buildRole("code1"), buildRole("code2"));

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(ccaRoleService.getSectorUserRoles()).thenReturn(roles);

        // Invoke
        mockMvc.perform(MockMvcRequestBuilders
                        .get(BASE_PATH + "/sector-association/" + sectorId + SECTOR_USER_ROLES_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value(roles.get(0).getCode()))
                .andExpect(jsonPath("$[1].code").value(roles.get(1).getCode()));
    }

    @Test
    void getSectorUserRoles_forbidden() throws Exception {
        final long sectorId = 1L;
        AppUser appUser = AppUser.builder().userId("authId").roleType(SECTOR_USER).build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "getSectorUserRoles", String.valueOf(sectorId), null, null);

        // Invoke
        mockMvc.perform(MockMvcRequestBuilders
                        .get(BASE_PATH + "/sector-association/" + sectorId + SECTOR_USER_ROLES_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private RoleDTO buildRole(String code) {
        return RoleDTO.builder()
                .code(code)
                .build();
    }
}
