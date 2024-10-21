package uk.gov.cca.api.user.operator.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDetailsDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserRegistrationWithCredentialsDTO;
import uk.gov.cca.api.user.operator.transform.CcaOperatorUserMapper;
import uk.gov.cca.api.user.operator.transform.CcaOperatorUserRegistrationMapper;
import uk.gov.cca.api.user.operator.transform.CcaOperatorUserViewMapper;
import uk.gov.netz.api.user.core.service.auth.AuthService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaOperatorUserAuthServiceTest {

    @Mock
    private AuthService authService;

    @Mock
    private CcaOperatorUserViewMapper ccaOperatorUserViewMapper;

    @Mock
    private CcaOperatorUserRegistrationMapper operatorUserRegistrationMapper;

    @Mock
    private CcaOperatorUserMapper ccaOperatorUserMapper;

    @InjectMocks
    private CcaOperatorUserAuthService ccaOperatorUserAuthService;

    @Test
    void updateCcaOperatorUser() {
        CcaOperatorUserDetailsDTO updatedOperatorUserDTO = mock(CcaOperatorUserDetailsDTO.class);
        UserRepresentation updatedUser = mock(UserRepresentation.class);

        when(ccaOperatorUserViewMapper.toUserRepresentation(updatedOperatorUserDTO)).thenReturn(updatedUser);

        ccaOperatorUserAuthService.updateCcaOperatorUser(updatedOperatorUserDTO);

        verify(authService).saveUser(updatedUser);
    }

    @Test
    void updatePendingOperatorUserToRegisteredAndEnabled() {
        String userId = "userId";
        String email = "email";
        CcaOperatorUserDTO userDTO = CcaOperatorUserDTO
                .builder()
                .email("email")
                .firstName("fn")
                .jobTitle("Engineer")
                .lastName("ln").build();
        UserRepresentation userRepresentation = createUserRepresentation(userId, email, email);
        CcaOperatorUserRegistrationWithCredentialsDTO userRegistrationDTO = CcaOperatorUserRegistrationWithCredentialsDTO.builder()
                .emailToken("emailtoken").password("pass").organisationName("name").build();

        when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
        when(operatorUserRegistrationMapper.toUserRepresentation(userRegistrationDTO, userRepresentation.getEmail(), userRepresentation.getId()))
                .thenReturn(userRepresentation);
        when(ccaOperatorUserMapper.toCcaOperatorUserDTO(userRepresentation)).thenReturn(userDTO);

        CcaOperatorUserDTO result = ccaOperatorUserAuthService.enableAndUpdateUserAndSetPassword(userRegistrationDTO, userId);

        Assertions.assertThat(result).isEqualTo(userDTO);

        verify(authService, times(1)).getUserRepresentationById(userId);
        verify(authService, times(1)).enableAndSaveUserAndSetPassword(userRepresentation, "pass");
        verify(operatorUserRegistrationMapper, times(1)).toUserRepresentation(userRegistrationDTO,
                userRepresentation.getEmail(), userRepresentation.getId());
        verify(ccaOperatorUserMapper, times(1)).toCcaOperatorUserDTO(userRepresentation);
    }

    @Test
    void setUserPassword() {
        String userId = "userId";
        String email = "email";
        String password = "password";

        UserRepresentation userRepresentation = createUserRepresentation(userId, email, email);

        when(authService.setUserPassword(userId, password)).thenReturn(userRepresentation);

        ccaOperatorUserAuthService.setUserPassword(userId, password);

        verify(authService, times(1)).setUserPassword(userId, password);
        verify(ccaOperatorUserMapper, times(1)).toCcaOperatorUserDTO(userRepresentation);
    }

    private UserRepresentation createUserRepresentation(String id, String email, String username) {
        UserRepresentation user = new UserRepresentation();
        user.setId(id);
        user.setEmail(email);
        user.setUsername(username);
        user.setEnabled(false);
        return user;
    }
}

