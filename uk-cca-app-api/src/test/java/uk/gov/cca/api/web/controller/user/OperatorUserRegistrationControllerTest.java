package uk.gov.cca.api.web.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.user.operator.domain.CcaOperatorInvitedUserInfoDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserRegistrationWithCredentialsDTO;
import uk.gov.cca.api.user.operator.service.CcaOperatorUserAcceptInvitationService;
import uk.gov.cca.api.user.operator.service.CcaOperatorUserActivationService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.netz.api.user.core.domain.dto.TokenDTO;
import uk.gov.netz.api.user.core.domain.enumeration.UserInvitationStatus;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OperatorUserRegistrationControllerTest {

    private static final String USER_CONTROLLER_PATH = "/v1.0/operator-users/registration";
    private static final String ACCEPT_INVITATION_PATH = "/accept-invitation";
    private static final String ENABLE_WITH_CREDENTIALS_FROM_INVITATION = "/accept-authority-and-enable-invited-operator-with-credentials";
    private static final String ACCEPT_AUTHORITY_AND_SET_CREDENTIALS_TO_USER = "/accept-authority-and-set-credentials-to-operator-user";

    private MockMvc mockMvc;

    @InjectMocks
    private OperatorUserRegistrationController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private CcaOperatorUserActivationService ccaOperatorUserActivationService;

    @Mock
    private CcaOperatorUserAcceptInvitationService operatorUserAcceptInvitationService;

    @Mock
    private Validator validator;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setValidator(validator)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void acceptInvitation() throws Exception {
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setToken("token");

        CcaOperatorInvitedUserInfoDTO operatorInvitedUserInfo = CcaOperatorInvitedUserInfoDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .roleCode("code")
                .invitationStatus(UserInvitationStatus.ACCEPTED)
                .contactType(ContactType.OPERATOR.getName())
                .build();

        when(operatorUserAcceptInvitationService.acceptInvitation(tokenDTO.getToken()))
                .thenReturn(operatorInvitedUserInfo);

        mockMvc.perform(MockMvcRequestBuilders.post(USER_CONTROLLER_PATH + ACCEPT_INVITATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(tokenDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(operatorInvitedUserInfo.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(operatorInvitedUserInfo.getLastName()))
                .andExpect(jsonPath("$.roleCode").value(operatorInvitedUserInfo.getRoleCode()))
                .andExpect(jsonPath("$.contactType").value(operatorInvitedUserInfo.getContactType()))
                .andExpect(jsonPath("$.invitationStatus").value(operatorInvitedUserInfo.getInvitationStatus().name()));

        verify(operatorUserAcceptInvitationService, times(1)).acceptInvitation(tokenDTO.getToken());
    }

    @Test
    void acceptAuthorityAndEnableInvitedUserWithCredentials() throws Exception {
        CcaOperatorUserRegistrationWithCredentialsDTO user = CcaOperatorUserRegistrationWithCredentialsDTO.builder()
                .emailToken("token")
                .firstName("fn")
                .lastName("ln")
                .organisationName("test_organization")
                .contactType(ContactType.CONSULTANT)
                .build();
        CcaOperatorUserDTO userDTO = CcaOperatorUserDTO
                .builder()
                .email("email")
                .firstName("fn")
                .jobTitle("Engineer")
                .lastName("ln").build();

        when(ccaOperatorUserActivationService.acceptAuthorityAndEnableInvitedUserWithCredentials(user)).thenReturn(userDTO);

        mockMvc.perform(
                        MockMvcRequestBuilders.put(USER_CONTROLLER_PATH + ENABLE_WITH_CREDENTIALS_FROM_INVITATION)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("fn"))
                .andExpect(jsonPath("$.lastName").value("ln"))
                .andExpect(jsonPath("$.jobTitle").value("Engineer"))
                .andExpect(jsonPath("$.email").value("email"));

        verify(ccaOperatorUserActivationService, times(1)).acceptAuthorityAndEnableInvitedUserWithCredentials(user);
    }

    @Test
    void acceptAuthorityAndSetCredentialsToUser() throws Exception {
        InvitedUserCredentialsDTO operatorUser = InvitedUserCredentialsDTO.builder()
                .invitationToken("token")
                .password("password")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.put(USER_CONTROLLER_PATH + ACCEPT_AUTHORITY_AND_SET_CREDENTIALS_TO_USER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(operatorUser)))
                .andExpect(status().isNoContent());

        verify(ccaOperatorUserActivationService, times(1)).acceptAuthorityAndSetCredentialsToUser(operatorUser);
    }
}