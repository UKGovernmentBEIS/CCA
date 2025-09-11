package uk.gov.cca.api.user.sectoruser.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityInfoDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityDetailsDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserRegistrationWithCredentialsDTO;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserMapper;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserRegistrationMapper;
import uk.gov.netz.api.user.core.service.auth.AuthService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SectorUserAuthServiceTest {

    @InjectMocks
    private SectorUserAuthService sectorUserAuthService;

    @Mock
    private AuthService authService;

    @Mock
    private SectorUserMapper sectorUserMapper;

    @Mock
    private SectorUserRegistrationMapper sectorUserRegistrationMapper;

    @Test
    void registerSectorUserAsPending() {
        String email = "email";
        String firstName = "firstName";
        String lastName = "lastName";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(email);
        userRepresentation.setFirstName(firstName);
        userRepresentation.setLastName(lastName);

        // mock
        when(sectorUserMapper.toUserRepresentation(email, firstName, lastName)).thenReturn(userRepresentation);
        when(authService.saveUser(userRepresentation)).thenReturn("254cad93-d1f5-4951-bb0e-e9b0a1586844");

        // invoke
        String userId = sectorUserAuthService.registerSectorUser(email, firstName, lastName);

        //assert
        assertThat(userId).isNotBlank();

        // verify mocks
        verify(sectorUserMapper, times(1)).toUserRepresentation(email, firstName, lastName);
        verify(authService, times(1)).saveUser(userRepresentation);
    }

    @Test
    void updateSectorUser() {
        String userId = "user";
        String username = "username";
        UserRepresentation userRepresentationUpdated = createUserRepresentation(userId, "email2", username);

        SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO =
                SectorUserAuthorityDetailsDTO.builder()
                        .email("email2")
                        .firstName("fn")
                        .lastName("ln")
                        .contactType(ContactType.SECTOR_ASSOCIATION)
                        .build();

        when(sectorUserMapper.toUserRepresentation(sectorUserAuthorityDetailsDTO)).thenReturn(userRepresentationUpdated);

        //invoke
        sectorUserAuthService.updateSectorUser(sectorUserAuthorityDetailsDTO);

        //verify
        verify(sectorUserMapper, times(1)).toUserRepresentation(sectorUserAuthorityDetailsDTO);
        verify(authService, times(1)).saveUser(userRepresentationUpdated);
    }

    @Test
    void updatePendingSectorUserToRegisteredAndEnabled() {
        String userId = "userId";
        String email = "email";

        UserRepresentation userRepresentation = createUserRepresentation(userId, email, email);
        SectorUserRegistrationWithCredentialsDTO userRegistrationDTO = SectorUserRegistrationWithCredentialsDTO.
                builder().organisationName("name").emailToken("emailtoken").password("pass").build();
        CcaAuthorityInfoDTO userAuthInfo = CcaAuthorityInfoDTO.builder().userId(userId).sectorAssociationId(1L).build();
        SectorUserDTO sectorUserDTO = SectorUserDTO.builder().email("email1").build();

        // Mock
        when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
        when(sectorUserRegistrationMapper.toUserRepresentation(userRegistrationDTO, userRepresentation.getEmail(), userRepresentation.getId()))
                .thenReturn(userRepresentation);
        when(sectorUserMapper.toSectorUserDTO(userRepresentation)).thenReturn(sectorUserDTO);
        // Invoke
        SectorUserDTO result = sectorUserAuthService.enableAndUpdateUserAndSetPassword(userRegistrationDTO, userAuthInfo.getUserId());

        // Verify
        Assertions.assertThat(result).isEqualTo(sectorUserDTO);

        verify(authService, times(1)).getUserRepresentationById(userId);
        verify(authService, times(1)).enableAndSaveUserAndSetPassword(userRepresentation, "pass");
        verify(sectorUserRegistrationMapper, times(1)).toUserRepresentation(userRegistrationDTO,
                userRepresentation.getEmail(), userRepresentation.getId());
        verify(sectorUserMapper, times(1)).toSectorUserDTO(userRepresentation);
    }

    @Test
    void setUserPassword() {
        String userId = "userId";
        String email = "email";
        String password = "password";

        UserRepresentation userRepresentation = createUserRepresentation(userId, email, email);

        when(authService.setUserPassword(userId, password)).thenReturn(userRepresentation);

        sectorUserAuthService.setUserPassword(userId, password);

        verify(authService, times(1)).setUserPassword(userId, password);
        verify(sectorUserMapper, times(1)).toSectorUserDTO(userRepresentation);
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
