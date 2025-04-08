package uk.gov.cca.api.web.controller.workflow;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.workflow.request.core.service.AvailableRequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class AvailableRequestControllerTest {

    private static final String BASE_PATH = "/v1.0/requests/available-workflows";

    private MockMvc mockMvc;

    @InjectMocks
    private AvailableRequestController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private AvailableRequestService availableRequestService;

    @BeforeEach
    public void setUp() {

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (AvailableRequestController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getAvailableWorkflows() throws Exception {
        final String resourceId = "1";
        final String resourceType = "SECTOR_ASSOCIATION";
        final AppUser appUser = AppUser.builder().userId("id").build();
        final Map<String, RequestCreateValidationResult> results =
                Map.of("DUMMY_REQUEST_CREATE_ACTION_TYPE",
                        RequestCreateValidationResult.builder().valid(true).build());

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(availableRequestService.getAvailableWorkflows(resourceId, resourceType, appUser)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + resourceType + "/" + resourceId))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"DUMMY_REQUEST_CREATE_ACTION_TYPE\":{\"valid\":true}}"));

        verify(availableRequestService, times(1)).getAvailableWorkflows(resourceId, resourceType, appUser);
    }
}
