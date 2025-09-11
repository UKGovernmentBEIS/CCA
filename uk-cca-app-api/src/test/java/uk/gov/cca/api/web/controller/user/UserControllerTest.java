package uk.gov.cca.api.web.controller.user;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.feedback.FeedbackRating;
import uk.gov.netz.api.feedback.UserFeedbackDto;
import uk.gov.netz.api.feedback.UserFeedbackService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.netz.api.user.application.UserServiceDelegator;
import uk.gov.netz.api.user.core.domain.dto.UserDTO;
import uk.gov.netz.api.user.core.service.UserSignatureService;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String USER_CONTROLLER_PATH = "/v1.0/users";
    private static final String EMAIL = "random@test.gr";

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private UserServiceDelegator userServiceDelegator;

    @Mock
    private UserSignatureService userSignatureService;

    @Mock
    private UserFeedbackService userFeedbackService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect authorizedAspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(userController);
        aspectJProxyFactory.addAspect(authorizedAspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        userController = (UserController) aopProxy.getProxy();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void getCurrentUser() throws Exception {
        final String userId = "userId";
        final String firstName = "firstName";
        final String lastName = "lastName";

        UserDTO userDTO = UserDTO.builder()
            .email(EMAIL)
            .firstName(firstName)
            .lastName(lastName)
            .build();

        AppUser currentUser = AppUser.builder().userId(userId).build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        when(userServiceDelegator.getCurrentUserDTO(currentUser)).thenReturn(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(USER_CONTROLLER_PATH + "/current")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.firstName").value(firstName))
            .andExpect(jsonPath("$.lastName").value(lastName));
        verify(userServiceDelegator, times(1)).getCurrentUserDTO(currentUser);
    }

    @Test
    void generateGetCurrentUserSignatureToken() throws Exception {
        String userId = "userId";
        UUID signatureUuid = UUID.randomUUID();
        FileToken fileToken = FileToken.builder().token("token").tokenExpirationMinutes(10L).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(AppUser.builder().userId(userId).build());

        when(userSignatureService.generateSignatureFileToken(userId, signatureUuid)).thenReturn(fileToken);

        mockMvc.perform(MockMvcRequestBuilders
                .get(USER_CONTROLLER_PATH + "/signature")
                .param("signatureUuid", signatureUuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(fileToken.getToken()))
            .andExpect(jsonPath("$.tokenExpirationMinutes").value(fileToken.getTokenExpirationMinutes()));

        verify(userSignatureService, times(1))
            .generateSignatureFileToken(userId, signatureUuid);
    }

    @Test
    void provideUserFeedback() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        UserFeedbackDto userFeedbackDto = UserFeedbackDto.builder()
            .creatingAccountRate(FeedbackRating.DISSATISFIED)
            .creatingAccountRateReason("Optional")
            .improvementSuggestion("Very bad")
            .onBoardingRate(FeedbackRating.DISSATISFIED)
            .onBoardingRateReason("")
            .onlineGuidanceRate(FeedbackRating.SATISFIED)
            .onlineGuidanceRateReason("")
            .satisfactionRate(FeedbackRating.DISSATISFIED)
            .satisfactionRateReason("")
            .tasksRate(FeedbackRating.DISSATISFIED)
            .tasksRateReason("")
            .userRegistrationRate(FeedbackRating.DISSATISFIED)
            .userRegistrationRateReason("")
            .build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        mockMvc.perform(MockMvcRequestBuilders
                .post(USER_CONTROLLER_PATH + "/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userFeedbackDto)))
            .andExpect(status().isNoContent());

        verify(userFeedbackService, times(1)).sendFeedback("http://localhost", userFeedbackDto);
    }

    @Test
    void provideUserFeedback_bad_request() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        UserFeedbackDto userFeedbackDto = UserFeedbackDto.builder()
            .build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        mockMvc.perform(MockMvcRequestBuilders
                .post(USER_CONTROLLER_PATH + "/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userFeedbackDto)))
            .andExpect(status().isBadRequest());

        verify(userFeedbackService, times(0)).sendFeedback(anyString(), any());
    }

    private AppUser buildMockAuthenticatedUser() {
        return AppUser.builder()
            .authorities(
                Collections.singletonList(
                    AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()
                )
            )
            .roleType(RoleTypeConstants.REGULATOR)
            .userId("userId")
            .build();
    }
}