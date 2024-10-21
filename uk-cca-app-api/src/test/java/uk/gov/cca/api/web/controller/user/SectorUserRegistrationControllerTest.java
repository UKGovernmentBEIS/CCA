package uk.gov.cca.api.web.controller.user;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.user.sectoruser.domain.SectorInvitedUserInfoDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserRegistrationWithCredentialsDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserActivationService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.user.service.SectorUserRegistrationOrchestratorService;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.netz.api.user.core.domain.dto.TokenDTO;
import uk.gov.netz.api.user.core.domain.enumeration.UserInvitationStatus;

@ExtendWith(MockitoExtension.class)
public class SectorUserRegistrationControllerTest {

    private static final String USER_CONTROLLER_PATH = "/v1.0/sector-users/registration";
    private static final String ACCEPT_INVITATION_PATH = "/accept-invitation";
    private static final String ENABLE_WITH_CREDENTIALS_FROM_INVITATION = "/accept-authority-and-enable-invited-sector-user-with-credentials";
    private static final String ACCEPT_AUTHORITY_AND_SET_CREDENTIALS_TO_USER = "/accept-authority-and-set-credentials-to-sector-user";

    private MockMvc mockMvc;

    @InjectMocks
    private SectorUserRegistrationController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;
    @Mock
    private SectorUserActivationService sectorUserActivationService;

    @Mock
    private SectorUserRegistrationOrchestratorService sectorUserRegistrationOrchestratorService;

    @Mock
    private Validator validator;

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
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

        SectorInvitedUserInfoDTO sectorInvitedUserInfoDTO = SectorInvitedUserInfoDTO.builder()
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .roleCode("code")
                .invitationStatus(UserInvitationStatus.ACCEPTED)
                .build();

        when(sectorUserRegistrationOrchestratorService.acceptInvitation(tokenDTO.getToken())).thenReturn(sectorInvitedUserInfoDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(USER_CONTROLLER_PATH + ACCEPT_INVITATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(tokenDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(sectorInvitedUserInfoDTO.getEmail()))
                .andExpect(jsonPath("$.firstName").value(sectorInvitedUserInfoDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(sectorInvitedUserInfoDTO.getLastName()))
                .andExpect(jsonPath("$.roleCode").value(sectorInvitedUserInfoDTO.getRoleCode()))
                .andExpect(jsonPath("$.invitationStatus").value(sectorInvitedUserInfoDTO.getInvitationStatus().name()));

        verify(sectorUserRegistrationOrchestratorService, times(1)).acceptInvitation(tokenDTO.getToken());
    }

    @Test
    void acceptInvitation_throw_business_exception() throws Exception {
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setToken("token");

        when(sectorUserRegistrationOrchestratorService.acceptInvitation(tokenDTO.getToken()))
                .thenThrow(new BusinessException(CcaErrorCode.AUTHORITY_USER_IS_NOT_SECTOR_USER));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(USER_CONTROLLER_PATH + ACCEPT_INVITATION_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(tokenDTO)))
                .andExpect(status().isBadRequest());

        verify(sectorUserRegistrationOrchestratorService, times(1)).acceptInvitation(tokenDTO.getToken());
    }

    @Test
    void registerSectorUserFromInvitation() throws Exception {
        SectorUserRegistrationWithCredentialsDTO user = SectorUserRegistrationWithCredentialsDTO.builder()
                .emailToken("token").firstName("fn").lastName("ln").build();
        SectorUserDTO userDTO = SectorUserDTO.builder().email("email").firstName("fn").lastName("ln").jobTitle("jobTitle").build();

        when(sectorUserActivationService.acceptAuthorityAndEnableInvitedUserWithCredentials(user)).thenReturn(userDTO);

        mockMvc.perform(
                        MockMvcRequestBuilders.put(USER_CONTROLLER_PATH + ENABLE_WITH_CREDENTIALS_FROM_INVITATION)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("fn"))
                .andExpect(jsonPath("$.lastName").value("ln"))
                .andExpect(jsonPath("$.email").value("email"));

        verify(sectorUserActivationService, times(1)).acceptAuthorityAndEnableInvitedUserWithCredentials(user);
    }

    @Test
    void acceptAuthorityAndSetCredentialsToUser() throws Exception {
        InvitedUserCredentialsDTO sectorUser = InvitedUserCredentialsDTO.builder()
                .invitationToken("token")
                .password("password")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.put(USER_CONTROLLER_PATH + ACCEPT_AUTHORITY_AND_SET_CREDENTIALS_TO_USER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(sectorUser)))
                .andExpect(status().isNoContent());

        verify(sectorUserActivationService, times(1)).acceptAuthorityAndSetCredentialsToUser(sectorUser);
    }
}
