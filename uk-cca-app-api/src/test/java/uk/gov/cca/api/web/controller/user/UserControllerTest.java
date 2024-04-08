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
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.cca.api.web.controller.user.UserController;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.feedback.FeedbackRating;
import uk.gov.cca.api.feedback.UserFeedbackDto;
import uk.gov.cca.api.feedback.UserFeedbackService;
import uk.gov.netz.api.terms.UpdateTermsDTO;
import uk.gov.netz.api.token.FileToken;
import uk.gov.cca.api.user.application.UserService;
import uk.gov.cca.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.cca.api.user.core.service.UserSignatureService;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.security.AppSecurityComponent;
import uk.gov.cca.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.cca.api.web.security.AuthorizedAspect;
import uk.gov.cca.api.web.security.AuthorizedRoleAspect;

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
    private UserService userService;

    @Mock
    private UserAuthService userAuthService;

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
    public void setUp() {
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
        final Short termsVersion = 1;

        ApplicationUserDTO userDTO = ApplicationUserDTO.builder()
            .email(EMAIL)
            .firstName(firstName)
            .lastName(lastName)
            .termsVersion(termsVersion)
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(AppUser.builder().userId(userId).build());
        when(userService.getUserById(userId)).thenReturn(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(USER_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.firstName").value(firstName))
            .andExpect(jsonPath("$.lastName").value(lastName))
            .andExpect(jsonPath("$.termsVersion").isNumber());
    }

    @Test
    void editUserTerms() throws Exception {
        final String userId = "userId";
        UpdateTermsDTO updateTermsDTO = new UpdateTermsDTO(Short.valueOf("1"));
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(AppUser.builder().userId(userId).build());

        mockMvc.perform(MockMvcRequestBuilders.patch(USER_CONTROLLER_PATH + "/terms-and-conditions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTermsDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.version").value(1));

        verify(userAuthService, times(1)).updateUserTerms(userId, updateTermsDTO.getVersion());
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
            .roleType(RoleType.REGULATOR)
            .userId("userId")
            .build();
    }
}