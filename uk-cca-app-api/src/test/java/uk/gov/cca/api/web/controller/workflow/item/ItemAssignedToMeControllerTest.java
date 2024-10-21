package uk.gov.cca.api.web.controller.workflow.item;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

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
import uk.gov.cca.api.workflow.request.application.item.service.ItemAssignedToMeSectorUserService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.workflow.request.application.item.service.ItemAssignedToMeRegulatorService;
import uk.gov.netz.api.workflow.request.application.item.service.ItemAssignedToMeService;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToMeControllerTest {

    private static final String BASE_PATH = "/v1.0/items";
    private static final String ASSIGNED_TO_ME_PATH = "assigned-to-me";

    private MockMvc mockMvc;

    @Mock
    private ItemAssignedToMeRegulatorService itemAssignedToMeRegulatorService;
    
    @Mock
    private ItemAssignedToMeSectorUserService itemAssignedToMeSectorUserService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    public void setUp() {
        List<ItemAssignedToMeService> services = List.of(itemAssignedToMeSectorUserService, itemAssignedToMeRegulatorService);
        ItemAssignedToMeController itemController = new ItemAssignedToMeController(services);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(itemController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        itemController = (ItemAssignedToMeController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void getAssignedItems_regulator() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemAssignedToMeRegulatorService.getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build()))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToMeSectorUserService.getRoleType()).thenReturn(SECTOR_USER);
        when(itemAssignedToMeRegulatorService.getRoleType()).thenReturn(RoleTypeConstants.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemAssignedToMeRegulatorService, times(1))
                .getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemAssignedToMeSectorUserService, never())
        		.getItemsAssignedToMe(any(), any(PagingRequest.class));
    }
    
    @Test
    void getAssignedItems_sector_user() throws Exception {
        AppUser appUser = AppUser.builder().roleType(SECTOR_USER).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemAssignedToMeSectorUserService
                .getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build())).thenReturn(itemDTOResponse);
        when(itemAssignedToMeSectorUserService.getRoleType()).thenReturn(SECTOR_USER);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemAssignedToMeSectorUserService, times(1))
                .getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemAssignedToMeRegulatorService, never())
                .getItemsAssignedToMe(any(), any(PagingRequest.class));
    }

    @Test
    void getAssignedItems_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{SECTOR_USER, REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(itemAssignedToMeSectorUserService, never())
            .getItemsAssignedToMe(any(), any(PagingRequest.class));
        verify(itemAssignedToMeRegulatorService, never())
            .getItemsAssignedToMe(any(), any(PagingRequest.class));
    }
}
