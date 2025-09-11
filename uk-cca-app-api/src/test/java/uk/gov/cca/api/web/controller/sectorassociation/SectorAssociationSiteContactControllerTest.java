package uk.gov.cca.api.web.controller.sectorassociation;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactInfoResponse;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSiteContactService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SectorAssociationSiteContactControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/sector-association/site-contacts";

    private MockMvc mockMvc;

    @InjectMocks
    private SectorAssociationSiteContactController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private SectorAssociationSiteContactService sectorAssociationSiteContactService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (SectorAssociationSiteContactController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getSectorAssociationSiteContacts() throws Exception {
        final AppUser user = AppUser.builder()
            .roleType(RoleTypeConstants.REGULATOR)
            .build();

        SectorAssociationSiteContactInfoResponse sectorAssociationSiteContactInfoResponse =
            SectorAssociationSiteContactInfoResponse.builder()
            .siteContacts(List.of(
                SectorAssociationSiteContactInfoDTO.builder()
                    .sectorName("SCT1 - sectorName1")
                    .sectorAssociationId(1L)
                    .build(),
                SectorAssociationSiteContactInfoDTO.builder()
                    .sectorName("SCT2 - sectorName2")
                    .sectorAssociationId(2L)
                    .build()
            ))
            .editable(false)
            .totalItems(2L)
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(sectorAssociationSiteContactService.getSectorAssociationSiteContacts(user,0,2))
            .thenReturn(sectorAssociationSiteContactInfoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("siteContacts[0].sectorAssociationId").value(1L))
            .andExpect(jsonPath("siteContacts[0].sectorName").value("SCT1 - sectorName1"))
            .andExpect(jsonPath("siteContacts[1].sectorAssociationId").value(2L))
            .andExpect(jsonPath("siteContacts[1].sectorName").value("SCT2 - sectorName2"));


        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(sectorAssociationSiteContactService, times(1))
            .getSectorAssociationSiteContacts(user,0, 2);
    }

    @Test
    void getSectorAssociationSiteContacts_forbidden() throws Exception {
        final AppUser user = AppUser.builder().build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(user,new String[] {RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(sectorAssociationSiteContactService, never()).getSectorAssociationSiteContacts(any(), anyInt(), anyInt());
    }

    @Test
    void updateSectorAssociationSiteContacts() throws Exception {
        final AppUser user = AppUser.builder()
            .roleType(RoleTypeConstants.REGULATOR)
            .build();

        List<SectorAssociationSiteContactDTO> sectorAssociationSiteContacList = List.of(
            SectorAssociationSiteContactDTO.builder()
                .userId("userId1")
                .sectorAssociationId(1L)
                .build(),
            SectorAssociationSiteContactDTO.builder()
                .userId("userId1")
                .sectorAssociationId(1L)
                .build()
        );

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sectorAssociationSiteContacList)))
            .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(sectorAssociationSiteContactService, times(1))
            .updateSectorAssociationSiteContacts(user, sectorAssociationSiteContacList);
    }

   @Test
   void updateSectorAssociationSiteContacts_forbidden() throws Exception {
       final AppUser user = AppUser.builder()
           .roleType(RoleTypeConstants.REGULATOR)
           .build();

       List<SectorAssociationSiteContactDTO> sectorAssociationSiteContacList = List.of(
           SectorAssociationSiteContactDTO.builder()
               .userId("userId1")
               .sectorAssociationId(1L)
               .build(),
           SectorAssociationSiteContactDTO.builder()
               .userId("userId1")
               .sectorAssociationId(1L)
               .build()
       );

       when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
       doThrow(new BusinessException(ErrorCode.FORBIDDEN))
           .when(appUserAuthorizationService)
           .authorize(user, "updateSectorAssociationSiteContacts");

       mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(sectorAssociationSiteContacList)))
           .andExpect(status().isForbidden());

       verify(appSecurityComponent, times(1)).getAuthenticatedUser();
       verify(sectorAssociationSiteContactService, never()).updateSectorAssociationSiteContacts(any(), anyList());
   }
}
