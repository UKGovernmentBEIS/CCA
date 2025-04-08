package uk.gov.cca.api.web.controller.account;

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

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.account.service.NoticeRecipientsServiceOrchestrator;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NoticeRecipientsControllerTest {

    private static final String NOTICE_RECIPIENTS_CONTROLLER_PATH = "/v1.0/account-notice/";

    private MockMvc mockMvc;

    @InjectMocks
    private NoticeRecipientsController noticeRecipientsController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private NoticeRecipientsServiceOrchestrator noticeRecipientsServiceOrchestrator;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(noticeRecipientsController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        noticeRecipientsController = (NoticeRecipientsController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(noticeRecipientsController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void getAdditionalNoticeRecipients() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final Long accountId = 1L;

        List<AdditionalNoticeRecipientDTO> list = Collections.singletonList(
        		AdditionalNoticeRecipientDTO.builder()
        				.userId("id")
                        .firstName("fn")
                        .lastName("ln")
                        .email("email")
                        .type(NoticeRecipientType.SECTOR_USER)
                        .build());

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(noticeRecipientsServiceOrchestrator.getAdditionalNoticeRecipients(user, accountId))
                .thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders.get(NOTICE_RECIPIENTS_CONTROLLER_PATH + accountId + "/additional-recipients" )
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$[0].userId").value("id"))
                .andExpect(jsonPath("$[0].email").value("email"))
                .andExpect(jsonPath("$[0].firstName").value("fn"))
                .andExpect(jsonPath("$[0].lastName").value("ln"))
                .andExpect(jsonPath("$[0].type").value(NoticeRecipientType.SECTOR_USER.name()));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(noticeRecipientsServiceOrchestrator, times(1))
                .getAdditionalNoticeRecipients(user, accountId);
    }

    @Test
    void getAdditionalNoticeRecipients_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();
        final Long accountId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(user,new String[] {RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(NOTICE_RECIPIENTS_CONTROLLER_PATH + accountId + "/additional-recipients" )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(noticeRecipientsServiceOrchestrator, never()).getAdditionalNoticeRecipients(user, accountId);
    }
}
