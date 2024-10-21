package uk.gov.cca.api.web.controller.workflow.item;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.cca.api.workflow.request.application.item.service.ItemUnassignedSectorUserService;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.workflow.request.application.item.service.ItemUnassignedRegulatorService;
import uk.gov.netz.api.workflow.request.application.item.service.ItemUnassignedService;

@ExtendWith(MockitoExtension.class)
class ItemUnassignedControllerTest {

    private static final String BASE_PATH = "/v1.0/items";
    private static final String UNASSIGNED = "unassigned";

    private static final String USER_ID = "user_id";
    private static final PagingRequest PAGING = PagingRequest.builder().pageNumber(0L).pageSize(10L).build();

    private MockMvc mockMvc;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private ItemUnassignedRegulatorService itemUnassignedRegulatorService;
    
    @Mock
    private ItemUnassignedSectorUserService itemUnassignedSectorUserService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    public void setUp() {
        List<ItemUnassignedService> services = List.of(itemUnassignedRegulatorService, itemUnassignedSectorUserService);
        ItemUnassignedController itemController = new ItemUnassignedController(services);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(itemController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        itemController = (ItemUnassignedController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setConversionService(conversionService)
            .build();
    }

    @Test
    void getUnassignedItems_regulator() throws Exception {
        AppUser appUser = buildMockappUser(REGULATOR);
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemUnassignedRegulatorService.getUnassignedItems(appUser, PAGING))
                .thenReturn(itemDTOResponse);
        when(itemUnassignedRegulatorService.getRoleType()).thenReturn(REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + UNASSIGNED + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemUnassignedRegulatorService, times(1))
                .getUnassignedItems(appUser, PAGING);
        verify(itemUnassignedSectorUserService, never())
        		.getUnassignedItems(appUser, PAGING);
    }
    
    @Test
    void getUnassignedItems_sector_user() throws Exception {
        AppUser appUser = buildMockappUser(SECTOR_USER);
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemUnassignedSectorUserService.getUnassignedItems(appUser, PAGING))
                .thenReturn(itemDTOResponse);
        when(itemUnassignedSectorUserService.getRoleType()).thenReturn(SECTOR_USER);
        when(itemUnassignedRegulatorService.getRoleType()).thenReturn(REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + UNASSIGNED + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemUnassignedRegulatorService, never())
                .getUnassignedItems(appUser, PAGING);
        verify(itemUnassignedSectorUserService, times(1))
        		.getUnassignedItems(appUser, PAGING);
    }

    @Test
    void getUnassignedItems_forbidden() throws Exception {
        AppUser appUser = buildMockappUser(REGULATOR);

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{OPERATOR, REGULATOR, SECTOR_USER});


        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + UNASSIGNED + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(itemUnassignedRegulatorService, never()).getUnassignedItems(any(), any(PagingRequest.class));
        verify(itemUnassignedSectorUserService, never()).getUnassignedItems(any(), any(PagingRequest.class));
    }

    private AppUser buildMockappUser(String roleType) {
        AppAuthority appAuthority = AppAuthority.builder()
            .competentAuthority(ENGLAND)
            .build();

        return AppUser.builder()
            .userId(USER_ID)
            .authorities(List.of(appAuthority))
            .roleType(roleType)
            .build();
    }
}
