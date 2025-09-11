package uk.gov.cca.api.user.operator.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityService;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityUpdateService;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDetailsDTO;
import uk.gov.cca.api.user.operator.transform.CcaOperatorUserViewMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.user.core.service.auth.AuthService;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class CcaOperatorUserManagementServiceTest {

    @Mock
    private CcaOperatorUserViewMapper ccaOperatorUserViewMapper;

    @Mock
    private CcaOperatorAuthorityService ccaOperatorAuthorityService;
    
    @Mock
    private CcaOperatorAuthorityUpdateService ccaOperatorAuthorityUpdateService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private CcaOperatorUserManagementService ccaOperatorUserManagementService;

    @Mock
    private CcaOperatorUserAuthService ccaOperatorUserAuthService;


    @Test
    void testGetOperatorUserByAccountIdAndUserId_Success() {

        final String userId = "someUserId";
        final Long accountId = 123L;
        CcaAuthorityDetails ccaAuthorityDetails = new CcaAuthorityDetails();
        UserRepresentation userRepresentation = new UserRepresentation();
        CcaOperatorUserDetailsDTO ccaOperatorUserDetailsDTO = new CcaOperatorUserDetailsDTO();
        when(ccaOperatorAuthorityService.getOperatorUserAuthorityDetails(userId, accountId)).thenReturn(ccaAuthorityDetails);
        when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
        when(ccaOperatorUserViewMapper.toCcaOperatorUserDetailsDTO(userRepresentation, ccaAuthorityDetails)).thenReturn(ccaOperatorUserDetailsDTO);
        // Calling the method under test
        CcaOperatorUserDetailsDTO result = ccaOperatorUserManagementService.getOperatorUserByAccountIdAndUserId(userId, accountId);

        // Assertions to verify the expected behavior
        assertNotNull(result);
        assertEquals(ccaOperatorUserDetailsDTO, result);
    }

    @Test
    void testUpdateCurrentOperatorUser() {
        AppUser appUser = new AppUser();
        appUser.setUserId("user123");
        Long accountId = 1L;
        CcaOperatorUserDetailsDTO updatedOperatorUserDetailsDTO = new CcaOperatorUserDetailsDTO();
        updatedOperatorUserDetailsDTO.setOrganisationName("Test Organisation");

        ccaOperatorUserManagementService.updateCurrentOperatorUser(appUser, accountId, updatedOperatorUserDetailsDTO);

        verify(ccaOperatorAuthorityUpdateService).updateOperatorUserAuthorityDetails("user123", accountId, "Test Organisation");
        verify(ccaOperatorUserAuthService).updateCcaOperatorUser(updatedOperatorUserDetailsDTO);
    }

    @Test
    void testUpdateOperatorUserByAccountAndId() {
        Long accountId = 1L;
        String userId = "user123";
        CcaOperatorUserDetailsDTO ccaOperatorUserDetailsDTO = new CcaOperatorUserDetailsDTO();
        ccaOperatorUserDetailsDTO.setOrganisationName("Test Organisation");
        ccaOperatorUserDetailsDTO.setContactType(ContactType.OPERATOR);

        ccaOperatorUserManagementService.updateOperatorUserByAccountAndUserId(accountId, userId, ccaOperatorUserDetailsDTO);

        verify(ccaOperatorAuthorityUpdateService).updateOperatorUserAuthorityDetailsWithContactType(userId, accountId, "Test Organisation", ContactType.OPERATOR);
        verify(ccaOperatorUserAuthService).updateCcaOperatorUser(ccaOperatorUserDetailsDTO);
    }

}
