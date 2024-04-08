package uk.gov.cca.api.web.controller.workflow.item;

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
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.cca.api.web.controller.workflow.item.ItemAssignedToMeController;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.security.AppSecurityComponent;
import uk.gov.cca.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.cca.api.web.security.AuthorizedAspect;
import uk.gov.cca.api.web.security.AuthorizedRoleAspect;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.cca.api.workflow.request.application.item.service.ItemAssignedToMeOperatorService;
import uk.gov.cca.api.workflow.request.application.item.service.ItemAssignedToMeRegulatorService;
import uk.gov.cca.api.workflow.request.application.item.service.ItemAssignedToMeService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.domain.RoleType.OPERATOR;
import static uk.gov.netz.api.common.domain.RoleType.REGULATOR;
import static uk.gov.netz.api.common.domain.RoleType.VERIFIER;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToMeControllerTest {

    private static final String BASE_PATH = "/v1.0/items";
    private static final String ASSIGNED_TO_ME_PATH = "assigned-to-me";

    private MockMvc mockMvc;

    @Mock
    private ItemAssignedToMeOperatorService itemAssignedToMeOperatorService;

    @Mock
    private ItemAssignedToMeRegulatorService itemAssignedToMeRegulatorService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    public void setUp() {
        List<ItemAssignedToMeService> services = List.of(itemAssignedToMeOperatorService, itemAssignedToMeRegulatorService);
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
    void getAssignedItems_operator() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleType.OPERATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemAssignedToMeOperatorService
                .getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build())).thenReturn(itemDTOResponse);
        when(itemAssignedToMeOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemAssignedToMeOperatorService, times(1))
                .getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemAssignedToMeRegulatorService, never())
                .getItemsAssignedToMe(any(), any(PagingRequest.class));
    }

    @Test
    void getAssignedItems_regulator() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleType.REGULATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemAssignedToMeRegulatorService.getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build()))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToMeOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);
        when(itemAssignedToMeRegulatorService.getRoleType()).thenReturn(RoleType.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemAssignedToMeOperatorService, never())
                .getItemsAssignedToMe(any(), any(PagingRequest.class));
        verify(itemAssignedToMeRegulatorService, times(1))
                .getItemsAssignedToMe(appUser, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
    }

    @Test
    void getAssignedItems_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new RoleType[]{OPERATOR, REGULATOR, VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(itemAssignedToMeOperatorService, never())
            .getItemsAssignedToMe(any(), any(PagingRequest.class));
        verify(itemAssignedToMeRegulatorService, never())
            .getItemsAssignedToMe(any(), any(PagingRequest.class));
    }
}
