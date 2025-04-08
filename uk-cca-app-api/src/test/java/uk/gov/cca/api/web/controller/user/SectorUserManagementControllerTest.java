package uk.gov.cca.api.web.controller.user;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityDetailsDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserManagementService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
public class SectorUserManagementControllerTest {

    public static final String BASE_PATH = "/v1.0/sector-users";

    private MockMvc mockMvc;

    @InjectMocks
    private SectorUserManagementController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private SectorUserManagementService sectorUserAuthorityManagementService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private Validator validator;

    private ObjectMapper objectMapper;

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
        objectMapper = new ObjectMapper();
        controller = (SectorUserManagementController) aopProxy.getProxy();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setValidator(validator)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getSectorUserById() throws Exception {
        AppUser user = AppUser.builder().userId("authId").build();
        String userId = "userId";
        SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO = SectorUserAuthorityDetailsDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(sectorUserAuthorityManagementService.getSectorUserBySectorAssociationIdAndUserId(1L, userId))
                .thenReturn(sectorUserAuthorityDetailsDTO);

        //invoke
        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_PATH + "/sector-association/" + 1L + "/" + userId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(sectorUserAuthorityDetailsDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(sectorUserAuthorityDetailsDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(sectorUserAuthorityDetailsDTO.getEmail()));

        verify(sectorUserAuthorityManagementService, times(1)).getSectorUserBySectorAssociationIdAndUserId(1L, userId);
    }

    @Test
    void getCurrentSectorUser() throws Exception {
        AppUser user = AppUser.builder().userId("authId").build();
        String userId = user.getUserId();
        SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO = SectorUserAuthorityDetailsDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(sectorUserAuthorityManagementService.getSectorUserBySectorAssociationIdAndUserId(1L, userId))
                .thenReturn(sectorUserAuthorityDetailsDTO);

        //invoke
        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_PATH + "/sector-association/" + 1L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(sectorUserAuthorityDetailsDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(sectorUserAuthorityDetailsDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(sectorUserAuthorityDetailsDTO.getEmail()));

        verify(sectorUserAuthorityManagementService, times(1)).getSectorUserBySectorAssociationIdAndUserId(1L, userId);
    }

    @Test
    void getSectorUserById_forbidden() throws Exception {
        AppUser user = AppUser.builder().userId("authId").build();
        String userId = "userId";
        Long sectorAssociationId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getSectorUserById", sectorAssociationId.toString(), null, null);

        mockMvc.perform(
                        MockMvcRequestBuilders.get(BASE_PATH + "/sector-association/" + sectorAssociationId + "/" + userId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(sectorUserAuthorityManagementService, never()).getSectorUserBySectorAssociationIdAndUserId(anyLong(), anyString());
    }

    @Test
    void updateCurrentSectorUser() throws Exception {
        AppUser user = AppUser.builder().userId("authId").roleType(SECTOR_USER).build();
        SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO = SectorUserAuthorityDetailsDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .contactType(ContactType.SECTOR_ASSOCIATION)
                .organisationName("Giant")
                .build();
        Long sectorAssociationId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        //invoke
        mockMvc.perform(
                        MockMvcRequestBuilders.patch(BASE_PATH + "/sector-association/" + sectorAssociationId + "/sector-user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sectorUserAuthorityDetailsDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(sectorUserAuthorityDetailsDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(sectorUserAuthorityDetailsDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(sectorUserAuthorityDetailsDTO.getEmail()))
                .andExpect(jsonPath("$.contactType").value(sectorUserAuthorityDetailsDTO.getContactType().name()))
                .andExpect(jsonPath("$.organisationName").value(sectorUserAuthorityDetailsDTO.getOrganisationName()));

        verify(sectorUserAuthorityManagementService, times(1)).updateCurrentSectorUser(user, sectorAssociationId, sectorUserAuthorityDetailsDTO);
    }

    @Test
    void updateCurrentSectorUser_forbidden() throws Exception {
        AppUser user = AppUser.builder().userId("authId").roleType(REGULATOR).build();
        SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO = SectorUserAuthorityDetailsDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .build();
        Long sectorAssociationId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(user, new String[]{SECTOR_USER});

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(BASE_PATH + "/sector-association/" + sectorAssociationId + "/sector-user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sectorUserAuthorityDetailsDTO)))
                .andExpect(status().isForbidden());

        verify(sectorUserAuthorityManagementService, never()).updateSectorUser(any(), any(), any());
    }

    @Test
    void updateSectorUserById() throws Exception {
        AppUser user = AppUser.builder().userId("authId").build();
        String userId = "userId";
        SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO = SectorUserAuthorityDetailsDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .contactType(ContactType.SECTOR_ASSOCIATION)
                .organisationName("Giant")
                .build();
        Long sectorAssociationId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        //invoke
        mockMvc.perform(
                        MockMvcRequestBuilders.patch(BASE_PATH + "/sector-association/" + sectorAssociationId + "/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sectorUserAuthorityDetailsDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(sectorUserAuthorityDetailsDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(sectorUserAuthorityDetailsDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(sectorUserAuthorityDetailsDTO.getEmail()))
                .andExpect(jsonPath("$.contactType").value(sectorUserAuthorityDetailsDTO.getContactType().name()))
                .andExpect(jsonPath("$.organisationName").value(sectorUserAuthorityDetailsDTO.getOrganisationName()));

        verify(sectorUserAuthorityManagementService, times(1))
                .updateSectorUser(sectorAssociationId, userId, sectorUserAuthorityDetailsDTO);
    }

    @Test
    void updateSectorUserById_forbidden() throws Exception {
        AppUser user = AppUser.builder().userId("authId").build();
        String userId = "userId";
        SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO = SectorUserAuthorityDetailsDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .build();
        Long sectorAssociationId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "updateSectorUserBySectorAssociationIdAndUserId", sectorAssociationId.toString(), null, null);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(BASE_PATH + "/sector-association/" + sectorAssociationId + "/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sectorUserAuthorityDetailsDTO)))
                .andExpect(status().isForbidden());

        verify(sectorUserAuthorityManagementService, never()).updateSectorUser(anyLong(), anyString(), any());
    }

    @Test
    void resetOperator2Fa() throws Exception {
        AppUser user = AppUser.builder().userId("authId").build();
        String userId = "userId";
        Long sectorAssociationId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(BASE_PATH + "/sector-association/" + sectorAssociationId + "/" + userId + "/reset-2fa"))
                .andExpect(status().isNoContent());

        verify(sectorUserAuthorityManagementService, times(1)).resetSectorUser2Fa(sectorAssociationId, userId);
    }

    @Test
    void resetOperator2Fa_forbidden() throws Exception {
        AppUser user = AppUser.builder().userId("authId").build();
        String userId = "userId";
        Long sectorAssociationId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "resetSectorUser2Fa", sectorAssociationId.toString(), null, null);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch(BASE_PATH + "/sector-association/" + sectorAssociationId + "/" + userId + "/reset-2fa"))
                .andExpect(status().isForbidden());

        verify(sectorUserAuthorityManagementService, never()).resetSectorUser2Fa( anyLong(), anyString());
    }

}
