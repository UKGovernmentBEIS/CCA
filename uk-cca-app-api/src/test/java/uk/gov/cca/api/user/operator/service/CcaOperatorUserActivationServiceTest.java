package uk.gov.cca.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityService;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserRegistrationWithCredentialsDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.netz.api.user.operator.service.OperatorUserRegisterValidationService;
import uk.gov.netz.api.user.operator.service.OperatorUserRegisteredAcceptInvitationService;
import uk.gov.netz.api.user.operator.service.OperatorUserTokenVerificationService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaOperatorUserActivationServiceTest {
    @Mock
    private OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    @Mock
    private CcaAuthorityService authorityService;
    @Mock
    private CcaOperatorUserAuthService ccaOperatorUserAuthService;
    @Mock
    private OperatorUserRegisterValidationService operatorUserRegisterValidationService;
    @Mock
    private OperatorUserRegisteredAcceptInvitationService operatorUserRegisteredAcceptInvitationService;

    @InjectMocks
    private CcaOperatorUserActivationService activationService;

    @Test
    void testActivateAndEnableOperatorInvitedUser_Success_WithOrganisationUpdate() {
    	AppUser	user = AppUser.builder().build();
        String userId = "userId";
        String email = "email";
        Long accountId = 1L;
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder().id(1L).userId("userId").id(1L).code("").accountId(1L).build();
        CcaOperatorUserRegistrationWithCredentialsDTO userRegistrationDTO = CcaOperatorUserRegistrationWithCredentialsDTO.builder()
                .emailToken("12345678")
                .organisationName("test")
                .jobTitle("Engineer")
                .contactType(ContactType.CONSULTANT)
                .build();
        CcaOperatorUserDTO userDTO = CcaOperatorUserDTO.builder().email(email).build();

        // Arrange
        when(operatorUserTokenVerificationService.verifyInvitationToken(anyString(), any())).thenReturn(authorityInfo);
        when(ccaOperatorUserAuthService.enableAndUpdateUserAndSetPassword(userRegistrationDTO, userId))
                .thenReturn(userDTO);

        // Act
        activationService.acceptAuthorityAndEnableInvitedUserWithCredentials(userRegistrationDTO, user);

        // Assert
        verify(operatorUserTokenVerificationService, times(1))
                .verifyInvitationToken(userRegistrationDTO.getEmailToken(), user);
        verify(ccaOperatorUserAuthService, times(1))
                .enableAndUpdateUserAndSetPassword(userRegistrationDTO, userId);
        verify(operatorUserRegisterValidationService, times(1))
                .validateRegisterForAccount(userId, accountId);
        verify(operatorUserRegisteredAcceptInvitationService, times(1))
                .acceptAuthorityAndNotify(authorityInfo.getId());
        verify(authorityService, times(1))
                .updateCcaAuthorityDetailsOrganisationName(anyLong(), anyString());
    }

    @Test
    void testActivateAndEnableOperatorInvitedUser_Success_WithNoOrganisationUpdate() {
    	AppUser	user = AppUser.builder().build();
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder().id(1L).userId("123456").id(1L).code("").accountId(1L).build();
        CcaOperatorUserRegistrationWithCredentialsDTO userRegistrationDTO = CcaOperatorUserRegistrationWithCredentialsDTO.builder()
                .emailToken("12345678")
                .organisationName(null)
                .jobTitle("Engineer")
                .contactType(ContactType.CONSULTANT)
                .build();

        // Arrange
        when(operatorUserTokenVerificationService.verifyInvitationToken(anyString(), any()))
                .thenReturn(authorityInfo);
        when(ccaOperatorUserAuthService.enableAndUpdateUserAndSetPassword(any(), anyString())).thenReturn(CcaOperatorUserDTO.builder().build());

        // Act
        CcaOperatorUserDTO result = activationService.acceptAuthorityAndEnableInvitedUserWithCredentials(userRegistrationDTO, user);

        // Assert
        assertNotNull(result);
        verify(authorityService, times(0)).updateCcaAuthorityDetailsOrganisationName(anyLong(), anyString());
        verify(ccaOperatorUserAuthService, times(1)).enableAndUpdateUserAndSetPassword(any(), anyString());
    }

    @Test
    void acceptAuthorityAndSetCredentialsToUser() {
    	AppUser	user = AppUser.builder().build();
        String userId = "userId";
        Long authorityId = 1L;
        Long accountId = 1L;
        InvitedUserCredentialsDTO invitedUserCredentialsDTO = InvitedUserCredentialsDTO.builder()
                .invitationToken("token")
                .password("password")
                .build();
        AuthorityInfoDTO authority = AuthorityInfoDTO.builder().userId(userId).id(authorityId).accountId(accountId).build();
        CcaOperatorUserDTO userDTO = CcaOperatorUserDTO.builder().email("email").build();

        when(operatorUserTokenVerificationService
                .verifyInvitationToken(invitedUserCredentialsDTO.getInvitationToken(), user))
                .thenReturn(authority);
        when(ccaOperatorUserAuthService
                .setUserPassword(authority.getUserId(), invitedUserCredentialsDTO.getPassword()))
                .thenReturn(userDTO);

        activationService.acceptAuthorityAndSetCredentialsToUser(invitedUserCredentialsDTO, user);

        verify(operatorUserTokenVerificationService, times(1))
                .verifyInvitationToken(invitedUserCredentialsDTO.getInvitationToken(), user);
        verify(operatorUserRegisterValidationService, times(1)).validateRegisterForAccount(userId, accountId);
        verify(ccaOperatorUserAuthService, times(1))
                .setUserPassword(authority.getUserId(), invitedUserCredentialsDTO.getPassword());
        verify(operatorUserRegisteredAcceptInvitationService, times(1))
                .acceptAuthorityAndNotify(authorityId);
    }
}