package uk.gov.cca.api.web.controller.notification;

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
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.cca.api.web.controller.notification.NotificationTemplateController;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.notification.template.domain.dto.NotificationTemplateDTO;
import uk.gov.cca.api.notification.template.domain.dto.NotificationTemplateSearchCriteria;
import uk.gov.cca.api.notification.template.domain.dto.NotificationTemplateUpdateDTO;
import uk.gov.cca.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.cca.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.cca.api.notification.template.service.NotificationTemplateQueryService;
import uk.gov.cca.api.notification.template.service.NotificationTemplateUpdateService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.security.AppSecurityComponent;
import uk.gov.cca.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.cca.api.web.security.AuthorizedAspect;
import uk.gov.cca.api.web.security.AuthorizedRoleAspect;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.domain.RoleType.OPERATOR;
import static uk.gov.netz.api.common.domain.RoleType.REGULATOR;

@ExtendWith(MockitoExtension.class)
class NotificationTemplateControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private NotificationTemplateController notificationTemplateController;

    @Mock
    private NotificationTemplateQueryService notificationTemplateQueryService;

    @Mock
    private NotificationTemplateUpdateService notificationTemplateUpdateService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(notificationTemplateController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        notificationTemplateController = (NotificationTemplateController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(notificationTemplateController)
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setConversionService(conversionService)
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getCurrentUserNotificationTemplates() throws Exception {
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        AppAuthority appAuthority = AppAuthority.builder().competentAuthority(ca).build();
        AppUser appUser = AppUser.builder()
            .userId("userId")
            .roleType(RoleType.REGULATOR)
            .authorities(List.of(appAuthority))
            .build();
        NotificationTemplateSearchCriteria searchCriteria = NotificationTemplateSearchCriteria.builder()
            .competentAuthority(ca)
            .term("term")
            .roleType(RoleType.OPERATOR)
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
            .build();

        List<TemplateInfoDTO> notificationTemplates = List.of(
                new TemplateInfoDTO(1L, "template1", "Workflow Name", LocalDateTime.now()),
                new TemplateInfoDTO(2L, "template2", "Workflow Name", LocalDateTime.now())
        );
        TemplateSearchResults results = TemplateSearchResults.builder()
            .templates(notificationTemplates)
            .total(2L)
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(notificationTemplateQueryService.getNotificationTemplatesBySearchCriteria(searchCriteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1.0/notification-templates")
                .param("term", searchCriteria.getTerm())
                .param("role", searchCriteria.getRoleType().name())
                .param("page", String.valueOf(searchCriteria.getPaging().getPageNumber()))
                .param("size", String.valueOf(searchCriteria.getPaging().getPageSize()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.templates[0].id").value(1L))
                .andExpect(jsonPath("$.templates[0].name").value("template1"))
                .andExpect(jsonPath("$.templates[1].id").value(2L))
                .andExpect(jsonPath("$.templates[1].name").value("template2"));

        verify(notificationTemplateQueryService, times(1))
            .getNotificationTemplatesBySearchCriteria(searchCriteria);
    }

    @Test
    void getCurrentUserNotificationTemplates_forbidden() throws Exception {
        AppUser appUser = AppUser.builder()
            .userId("userId")
            .roleType(OPERATOR)
            .build();
        NotificationTemplateSearchCriteria searchCriteria = NotificationTemplateSearchCriteria.builder()
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .term("term")
            .roleType(RoleType.OPERATOR)
            .paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new RoleType[]{REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1.0/notification-templates")
                .param("term", searchCriteria.getTerm())
                .param("role", searchCriteria.getRoleType().name())
                .param("page", String.valueOf(searchCriteria.getPaging().getPageNumber()))
                .param("size", String.valueOf(searchCriteria.getPaging().getPageSize()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(notificationTemplateQueryService);
    }

    @Test
    void getNotificationTemplateById() throws Exception {
        Long notificationTemplateId = 1L;
        String notificationTemplateName = "notif_template_name";

        NotificationTemplateDTO notificationTemplateDTO = NotificationTemplateDTO.builder()
            .id(notificationTemplateId)
            .name(notificationTemplateName)
            .build();

        when(notificationTemplateQueryService.getManagedNotificationTemplateById(notificationTemplateId)).thenReturn(notificationTemplateDTO);

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1.0/notification-templates/" + notificationTemplateId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(notificationTemplateId))
            .andExpect(jsonPath("$.name").value(notificationTemplateName));

        verify(notificationTemplateQueryService, times(1)).getManagedNotificationTemplateById(notificationTemplateId);
    }

    @Test
    void getNotificationTemplateById_forbidden() throws Exception {
        long notificationTemplateId = 1L;
        AppUser appUser = AppUser.builder()
            .userId("userId")
            .roleType(OPERATOR)
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "getNotificationTemplateById", Long.toString(notificationTemplateId));

        mockMvc.perform(MockMvcRequestBuilders
            .get("/v1.0/notification-templates/" + notificationTemplateId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(notificationTemplateQueryService);
    }

    @Test
    void updateNotificationTemplate() throws Exception {
        long notificationTemplateId = 1L;
        NotificationTemplateUpdateDTO notificationTemplateUpdateDTO = NotificationTemplateUpdateDTO.builder()
            .subject("subject")
            .text("text")
            .build();

        mockMvc.perform(
            MockMvcRequestBuilders
                .put("/v1.0/notification-templates/" + notificationTemplateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationTemplateUpdateDTO)))
            .andExpect(status().isNoContent());

        verify(notificationTemplateUpdateService, times(1))
            .updateNotificationTemplate(notificationTemplateId, notificationTemplateUpdateDTO);
    }

    @Test
    void updateNotificationTemplate_forbidden() throws Exception {
        long notificationTemplateId = 1L;
        AppUser appUser = AppUser.builder()
            .userId("userId")
            .roleType(OPERATOR)
            .build();
        NotificationTemplateUpdateDTO notificationTemplateUpdateDTO = NotificationTemplateUpdateDTO.builder()
            .subject("subject")
            .text("text")
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "updateNotificationTemplate", "1");

        mockMvc.perform(
            MockMvcRequestBuilders
                .put("/v1.0/notification-templates/" + notificationTemplateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationTemplateUpdateDTO)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(notificationTemplateUpdateService);
    }
}