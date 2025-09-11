package uk.gov.cca.api.web.controller.facility;

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
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.web.orchestrator.facility.service.FacilityIdGeneratorServiceOrchestrator;
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

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class FacilityControllerTest {

    private static final String FACILITY_CONTROLLER_PATH = "/v1.0/facility";

    private MockMvc mockMvc;

    @InjectMocks
    private FacilityController facilityController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private FacilityIdGeneratorServiceOrchestrator facilityIdGeneratorServiceOrchestrator;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;
    
    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(facilityController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        facilityController = (FacilityController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(facilityController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void generateFacilityId() throws Exception {
        final Long accountId = 1L;
        final String facilityId = "SA-F00001";
        final AppUser user = AppUser.builder().roleType(SECTOR_USER).build();

        final FacilityDTO facilityDTO = FacilityDTO.builder().facilityId(facilityId).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(facilityIdGeneratorServiceOrchestrator.generateFacilityId(accountId)).thenReturn(facilityDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(FACILITY_CONTROLLER_PATH + "/generate/" + accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.facilityId").value(facilityId));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(facilityIdGeneratorServiceOrchestrator, times(1)).generateFacilityId(accountId);
    }

    @Test
    void generateFacilityId_forbidden() throws Exception {
        final long sectorAssociationId = 1L;
        final AppUser user = AppUser.builder().userId("userId").build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "generateFacilityId", Long.toString(1L), null, null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(FACILITY_CONTROLLER_PATH + "/generate/" + sectorAssociationId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(facilityIdGeneratorServiceOrchestrator, never()).generateFacilityId(anyLong());
    }
    
    @Test
    void getActiveFacilityParticipatingSchemeVersions() throws Exception {
        final String facilityId = "SA-F00001";
        final AppUser user = AppUser.builder().roleType(SECTOR_USER).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(facilityDataQueryService.getActiveFacilityParticipatingSchemeVersions(facilityId))
                .thenReturn(Set.of(SchemeVersion.CCA_2));

        mockMvc.perform(MockMvcRequestBuilders.get(FACILITY_CONTROLLER_PATH + "/facilityId")
        		.param("facilityId", facilityId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(Set.of(SchemeVersion.CCA_2.name()).toString()));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(facilityDataQueryService, times(1)).getActiveFacilityParticipatingSchemeVersions(facilityId);
    }

    @Test
    void getSchemeVersionsIfFacility_IsActive_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(OPERATOR).userId("userId").build();
        final String facilityId = "SA-F00001";

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
        	.when(roleAuthorizationService)
        	.evaluate(user, new String[] {REGULATOR, SECTOR_USER});

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(FACILITY_CONTROLLER_PATH + "/facilityId")
                                .param("facilityId", facilityId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(facilityDataQueryService, never()).getActiveFacilityParticipatingSchemeVersions(facilityId);
    }
}
