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
import uk.gov.cca.api.workflow.request.application.item.service.ItemAssignedToOthersSectorUserService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.workflow.request.application.item.service.ItemAssignedToOthersOperatorService;
import uk.gov.netz.api.workflow.request.application.item.service.ItemAssignedToOthersRegulatorService;
import uk.gov.netz.api.workflow.request.application.item.service.ItemAssignedToOthersService;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToOthersControllerTest {

    private static final String BASE_PATH = "/v1.0/items";
    private static final String ASSIGNED_TO_OTHERS_PATH = "assigned-to-others";

    private MockMvc mockMvc;

    @Mock
    private ItemAssignedToOthersOperatorService itemAssignedToOthersOperatorService;

    @Mock
    private ItemAssignedToOthersRegulatorService itemAssignedToOthersRegulatorService;
    
    @Mock
    private ItemAssignedToOthersSectorUserService itemAssignedToOthersSectorUserService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    public void setUp() {
        List<ItemAssignedToOthersService> services = List.of(itemAssignedToOthersOperatorService, itemAssignedToOthersRegulatorService, itemAssignedToOthersSectorUserService);
        ItemAssignedToOthersController itemController = new ItemAssignedToOthersController(services);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(itemController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        itemController = (ItemAssignedToOthersController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setConversionService(conversionService)
            .build();
    }

    @Test
    void getItemsAssignedToOthers_operator() throws Exception {
        AppUser appUser = AppUser.builder().roleType(OPERATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemAssignedToOthersOperatorService.getItemsAssignedToOthers(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build()))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToOthersOperatorService.getRoleType()).thenReturn(OPERATOR);

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(itemAssignedToOthersOperatorService, times(1))
                .getItemsAssignedToOthers(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemAssignedToOthersRegulatorService, never())
                .getItemsAssignedToOthers(any(), any(PagingRequest.class));
        verify(itemAssignedToOthersSectorUserService, never())
        		.getItemsAssignedToOthers(any(), any(PagingRequest.class));
    }

    @Test
    void getItemsAssignedToOthers_regulator() throws Exception {
        AppUser appUser = AppUser.builder().roleType(REGULATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemAssignedToOthersRegulatorService.getItemsAssignedToOthers(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build()))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToOthersOperatorService.getRoleType()).thenReturn(OPERATOR);
        when(itemAssignedToOthersRegulatorService.getRoleType()).thenReturn(REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemAssignedToOthersOperatorService, never())
                .getItemsAssignedToOthers(any(), any(PagingRequest.class));
        verify(itemAssignedToOthersRegulatorService, times(1))
                .getItemsAssignedToOthers(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemAssignedToOthersSectorUserService, never())
				.getItemsAssignedToOthers(any(), any(PagingRequest.class));
    }
    
    @Test
    void getItemsAssignedToOthers_sector_user() throws Exception {
        AppUser appUser = AppUser.builder().roleType(SECTOR_USER).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemAssignedToOthersSectorUserService.getItemsAssignedToOthers(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build()))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToOthersSectorUserService.getRoleType()).thenReturn(SECTOR_USER);
        when(itemAssignedToOthersOperatorService.getRoleType()).thenReturn(OPERATOR);
        when(itemAssignedToOthersRegulatorService.getRoleType()).thenReturn(REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(itemAssignedToOthersSectorUserService, times(1))
                .getItemsAssignedToOthers(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemAssignedToOthersRegulatorService, never())
                .getItemsAssignedToOthers(any(), any(PagingRequest.class));
        verify(itemAssignedToOthersOperatorService, never())
        		.getItemsAssignedToOthers(any(), any(PagingRequest.class));
    }

    @Test
    void getItemsAssignedToOthers_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{OPERATOR, REGULATOR, SECTOR_USER});

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(itemAssignedToOthersOperatorService, never())
            .getItemsAssignedToOthers(any(), any(PagingRequest.class));
        verify(itemAssignedToOthersRegulatorService, never())
            .getItemsAssignedToOthers(any(), any(PagingRequest.class));
        verify(itemAssignedToOthersSectorUserService, never())
        		.getItemsAssignedToOthers(any(), any(PagingRequest.class));
    }
}
