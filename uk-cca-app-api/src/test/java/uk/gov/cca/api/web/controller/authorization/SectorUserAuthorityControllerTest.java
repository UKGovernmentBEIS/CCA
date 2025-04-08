package uk.gov.cca.api.web.controller.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
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
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.authorization.service.SectorUserAuthorityUpdateOrchestrator;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityInfoDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUsersAuthoritiesInfoDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserAuthorityInfoService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityUpdateDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityDeletionService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityUpdateWrapperDTO;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class SectorUserAuthorityControllerTest {

    private static final String BASE_PATH = "/v1.0/sector-authorities";

    private MockMvc mockMvc;

    @InjectMocks
    private SectorUserAuthorityController sectorUserAuthorityController;

    @Mock
    private SectorUserAuthorityInfoService sectorUserAuthorityInfoService;

    @Mock
    private SectorUserAuthorityDeletionService deletionService;
    
    @Mock
    private SectorUserAuthorityUpdateOrchestrator orchestrator;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        final AopProxy aopProxy = getAopProxy(authorizationAspectUserResolver);

        sectorUserAuthorityController = (SectorUserAuthorityController) aopProxy.getProxy();

        objectMapper = new ObjectMapper();


        mockMvc = MockMvcBuilders.standaloneSetup(sectorUserAuthorityController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getSectorUsersAuthoritiesInfo_whenRegulator() throws Exception {
        final AppUser regulatorUser = getAppUser(REGULATOR);
        Long sectorId = 1L;

        SectorUsersAuthoritiesInfoDTO sectorUsersAuthoritiesInfoDTO = SectorUsersAuthoritiesInfoDTO.builder()
                .authorities(Collections.singletonList(SectorUserAuthorityInfoDTO.builder()
                        .contactType("Sector Association")
                        .roleCode("roleCode")
                        .roleName("Administrator User")
                        .firstName("FirstName")
                        .lastName("Lastname")
                        .userId("b87a35cf-e85e-483c-9c3d-0de3fccb283f")
                        .status(AuthorityStatus.ACTIVE).build()))
                .editable(true)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(regulatorUser);
        when(sectorUserAuthorityInfoService.getSectorUsersAuthoritiesInfo(regulatorUser, sectorId))
                .thenReturn(sectorUsersAuthoritiesInfoDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(BASE_PATH + "/sector-association/" + sectorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(sectorUserAuthorityInfoService, times(1))
                .getSectorUsersAuthoritiesInfo(regulatorUser, sectorId);
    }

    @Test
    void getSectorUsersAuthoritiesInfo_whenSectorUser() throws Exception {
        final AppUser sectorUser = getAppUser(SECTOR_USER);
        Long sectorId = 1L;

        SectorUsersAuthoritiesInfoDTO sectorUsersAuthoritiesInfoDTO = SectorUsersAuthoritiesInfoDTO.builder()
                .authorities(Collections.singletonList(SectorUserAuthorityInfoDTO.builder()
                        .contactType("Sector Association")
                        .roleCode("roleCode")
                        .roleName("Administrator User")
                        .firstName("FirstName")
                        .lastName("Lastname")
                        .userId("b87a35cf-e85e-483c-9c3d-0de3fccb283f")
                        .status(AuthorityStatus.ACTIVE).build()))
                .editable(true)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(sectorUser);
        when(sectorUserAuthorityInfoService.getSectorUsersAuthoritiesInfo(sectorUser, sectorId))
                .thenReturn(sectorUsersAuthoritiesInfoDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(BASE_PATH + "/sector-association/" + sectorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(sectorUserAuthorityInfoService, times(1))
                .getSectorUsersAuthoritiesInfo(sectorUser, sectorId);
    }

    @Test
    void deleteSectorUserBySectorAndUser() throws Exception {

        final AppUser sectorUser = getAppUser(SECTOR_USER);
        final long sectorId = 1L;
        final String userId = "d55b90b7-e7dd-4576-8ac7-e05a9c6cc8d9";

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(sectorUser);

        //invoke
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_PATH + "/sector-association/" + sectorId + "/" + userId)
                )
                .andExpect(status().isNoContent());

        verify(deletionService, times(1))
                .deleteSectorUserByUserIdAndSectorAssociation(userId, sectorId);
    }

    @Test
    void deleteSectorUserBySectorAndUser_forbidden() throws Exception {

        final AppUser currentUser = getAppUser(SECTOR_USER);
        final long sectorId = 1L;
        final String user = "d55b90b7-e7dd-4576-8ac7-e05a9c6cc8d9";

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(currentUser, "deleteSectorUser", Long.toString(sectorId), null, null);

        //invoke
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_PATH + "/sector-association/" + sectorId + "/" + user)
                )
                .andExpect(status().isForbidden());

        verify(deletionService, never()).deleteSectorUserByUserIdAndSectorAssociation(anyString(), any());
    }

    @Test
    void deleteCurrentSectorUser() throws Exception {

        final AppUser currentUser = AppUser.builder().userId("currentuser").build();
        final long sectorId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        //invoke
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_PATH + "/sector-association/" + sectorId)
                )
                .andExpect(status().isNoContent());

        verify(deletionService, times(1))
                .deleteSectorUserByUserIdAndSectorAssociation(currentUser.getUserId(), sectorId);
    }

    @Test
    void deleteCurrentSectorUser_forbidden() throws Exception {

        final AppUser currentUser = getAppUser(SECTOR_USER);
        final long sectorId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(currentUser, "deleteCurrentSectorUser", Long.toString(sectorId), null, null);

        //invoke
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(BASE_PATH + "/sector-association/" + sectorId)
                )
                .andExpect(status().isForbidden());

        verify(deletionService, never()).deleteSectorUserByUserIdAndSectorAssociation(anyString(), any());
    }

    @NotNull
    private AopProxy getAopProxy(AuthorizationAspectUserResolver authorizationAspectUserResolver) {
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(sectorUserAuthorityController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        return proxyFactory.createAopProxy(aspectJProxyFactory);
    }

    private AppUser getAppUser(String roleType) {
        return switch (roleType) {
            case REGULATOR -> {
                final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

                yield AppUser.builder()
                        .roleType(REGULATOR)
                        .authorities(List.of(AppAuthority.builder().competentAuthority(ca).build()))
                        .build();
            }
            case SECTOR_USER -> AppUser.builder()
                    .roleType(SECTOR_USER)
                    .authorities(List.of(AppCcaAuthority.builder().sectorAssociationId(1L).build()))
                    .build();
            default -> AppUser.builder()
                    .roleType(OPERATOR)
                    .authorities(List.of(AppAuthority.builder().accountId(1L).build()))
                    .build();
        };
    }

    @Test
    void updateSectorUserAuthorities() throws Exception {

        AppUser currentUser = AppUser.builder().userId("currentuser").build();

        List<SectorUserAuthorityUpdateDTO> sectorUsers = List.of(
                SectorUserAuthorityUpdateDTO.builder().userId("1").roleCode("sector_user_administrator").authorityStatus(AuthorityStatus.ACTIVE).build()
        );
        SectorUserAuthorityUpdateWrapperDTO wrapper = new SectorUserAuthorityUpdateWrapperDTO(sectorUsers);

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        doNothing().when(orchestrator).updateSectorAuthorities(any(SectorUserAuthorityUpdateWrapperDTO.class), anyLong());

        //invoke
        mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_PATH + "/sector-association/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrapper))
        )
                .andExpect(status().isNoContent());

        verify(orchestrator, times(1))
                .updateSectorAuthorities(any(SectorUserAuthorityUpdateWrapperDTO.class), anyLong());

    }

    @Test
    void updateSectorUserAuthorities_forbidden() throws Exception {
        AppUser currentUser = AppUser.builder().userId("currentuser").build();
        long sectorId = 1L;

        List<SectorUserAuthorityUpdateDTO> sectorUsers = List.of(
                SectorUserAuthorityUpdateDTO.builder().userId("1").roleCode("role1").authorityStatus(AuthorityStatus.ACTIVE).build(),
                SectorUserAuthorityUpdateDTO.builder().userId("2").roleCode("invalid_role").authorityStatus(AuthorityStatus.ACTIVE).build()
        );

        SectorUserAuthorityUpdateWrapperDTO wrapper = new SectorUserAuthorityUpdateWrapperDTO(sectorUsers);

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(currentUser, "updateSectorUserAuthorities", String.valueOf(sectorId), null, null);

        //invoke
        mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_PATH + "/sector-association/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrapper))
        )
                .andExpect(status().isForbidden());

        verify(orchestrator, never())
                .updateSectorAuthorities(any(), anyLong());
    }

}
