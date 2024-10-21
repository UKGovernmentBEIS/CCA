package uk.gov.cca.api.user.sectoruser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserRegistrationWithCredentialsDTO;
import uk.gov.netz.api.user.core.domain.dto.InvitedUserCredentialsDTO;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectorUserActivationServiceTest {

    @InjectMocks
    private SectorUserActivationService sectorUserActivationService;

    @Mock
    private SectorUserAuthService sectorUserAuthService;

    @Mock
    private SectorUserTokenVerificationService sectorUserTokenVerificationService;
    
    @Mock
    private CcaAuthorityService authorityService;

    @Mock
    private SectorUserRegisteredAcceptInvitationService sectorUserRegisteredAcceptInvitationService;

    @Mock
    private SectorUserRegisterValidationService sectorUserRegisterValidationService;

    @Test
    void activateAndEnableSectorInvitedUser() {
        String userId = "userId";
        String token = "token";
        String email = "email";
        SectorUserRegistrationWithCredentialsDTO userRegistrationDTO = SectorUserRegistrationWithCredentialsDTO.builder()
                .organisationName("name")
                .emailToken(token).build();
        CcaAuthorityInfoDTO authority = CcaAuthorityInfoDTO.builder().userId(userId).sectorAssociationId(1L).build();
        SectorUserDTO userDTO = SectorUserDTO.builder().email(email).build();

        // Mock
        when(sectorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(token))
                .thenReturn(authority);
        when(sectorUserAuthService.enableAndUpdateUserAndSetPassword(userRegistrationDTO, authority.getUserId()))
                .thenReturn(userDTO);
        // Invoke
        sectorUserActivationService.acceptAuthorityAndEnableInvitedUserWithCredentials(userRegistrationDTO);

        // Verify
        verify(sectorUserTokenVerificationService, times(1))
                .verifyInvitationTokenForPendingAuthority(userRegistrationDTO.getEmailToken());
        verify(authorityService, times(1))
        	.updateCcaAuthorityDetailsOrganisationName(authority.getId(), userRegistrationDTO.getOrganisationName());
        verify(sectorUserAuthService, times(1))
                .enableAndUpdateUserAndSetPassword(userRegistrationDTO, authority.getUserId());
        verify(sectorUserRegisteredAcceptInvitationService, times(1)).acceptAuthorityAndNotify(authority.getId());
    }

    @Test
    void acceptAuthorityAndSetCredentialsToUser() {
        String userId = "userId";
        Long authorityId = 1L;
        Long sectorAssociationId = 1L;
        InvitedUserCredentialsDTO invitedUserCredentialsDTO = InvitedUserCredentialsDTO.builder()
                .invitationToken("token")
                .password("password")
                .build();
        CcaAuthorityInfoDTO authority = CcaAuthorityInfoDTO.builder().userId(userId).id(authorityId).sectorAssociationId(sectorAssociationId).build();

        SectorUserDTO userDTO = SectorUserDTO.builder().email("email").build();

        when(sectorUserTokenVerificationService
                .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken()))
                .thenReturn(authority);
        when(sectorUserAuthService
                .setUserPassword(authority.getUserId(), invitedUserCredentialsDTO.getPassword()))
                .thenReturn(userDTO);

        sectorUserActivationService.acceptAuthorityAndSetCredentialsToUser(invitedUserCredentialsDTO);

        verify(sectorUserTokenVerificationService, times(1))
                .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken());
        verify(sectorUserRegisterValidationService, times(1)).validateRegisterForSectorAssociation(userId, sectorAssociationId);
        verify(sectorUserAuthService, times(1))
                .setUserPassword(authority.getUserId(), invitedUserCredentialsDTO.getPassword());
        verify(sectorUserRegisteredAcceptInvitationService, times(1))
                .acceptAuthorityAndNotify(authorityId);
    }
}
