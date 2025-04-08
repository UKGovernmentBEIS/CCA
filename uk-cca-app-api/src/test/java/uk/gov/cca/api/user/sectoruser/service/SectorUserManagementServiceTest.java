package uk.gov.cca.api.user.sectoruser.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityUpdateService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityDetailsDTO;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.user.core.service.UserSecuritySetupService;
import uk.gov.netz.api.user.core.service.auth.AuthService;

@ExtendWith(MockitoExtension.class)
class SectorUserManagementServiceTest {

    @InjectMocks
    private SectorUserManagementService sectorUserManagementService;

    @Mock
    private SectorUserAuthService sectorUserAuthService;

    @Mock
    private UserSecuritySetupService userSecuritySetupService;

    @Mock
    private SectorUserAuthorityService authorityService;
    
    @Mock
    private SectorUserAuthorityUpdateService sectorUserAuthorityUpdateService;

    @Mock
    private SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;

    @Mock
    private AuthService authService;

    @Mock
    private SectorUserMapper sectorUserMapper;

    @Test
    void getSectorUserBySectorAssociationIdAndUserIdTest() {
        final Long sectorAssociationId = 1L;
        final String userId = "userId";
        final String email = "email";
        final CcaAuthorityDetails authorityDetails = CcaAuthorityDetails
                .builder()
                .organisationName("GIANT")
                .contactType(ContactType.SECTOR_ASSOCIATION)
                .build();

        SectorUserAuthorityDetailsDTO sectorUserDTO =
                SectorUserAuthorityDetailsDTO.builder()
                        .email(email)
                        .firstName("firstName")
                        .lastName("lastName")
                        .organisationName("GIANT")
                        .contactType(ContactType.SECTOR_ASSOCIATION).build();

        UserRepresentation userRepresentation = createUserRepresentation(userId, email, "username");

        when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
        when(authorityService.getSectorUserAuthorityDetails(userId, sectorAssociationId)).thenReturn(authorityDetails);
        when(sectorUserMapper.toSectorUserDTO(userRepresentation, authorityDetails)).thenReturn(sectorUserDTO);

        // Invoke
        final SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO =
                sectorUserManagementService.getSectorUserBySectorAssociationIdAndUserId(sectorAssociationId, userId);

        // Verify
        assertNotNull(sectorUserAuthorityDetailsDTO.getContactType());
        assertThat(sectorUserAuthorityDetailsDTO.getContactType()).isEqualTo(ContactType.SECTOR_ASSOCIATION);
        assertNotNull(sectorUserAuthorityDetailsDTO.getOrganisationName());
        assertThat(sectorUserAuthorityDetailsDTO.getOrganisationName()).isEqualTo("GIANT");

        verify(authorityService, times(1)).getSectorUserAuthorityDetails(userId, sectorAssociationId);
        verify(authService, times(1)).getUserRepresentationById(userId);
        verify(sectorUserMapper, times(1)).toSectorUserDTO(userRepresentation, authorityDetails);

    }

    @Test
    void getSectorUserBySectorAssociationIdAndUserIdWhenAuthorityWithUserIdAndSectorNotExistsTest() {
        final Long sectorAssociationId = 1L;
        final String userId = "userId1";

        UserRepresentation userRepresentation = createUserRepresentation(userId, "email", "username");

        when(authorityService.getSectorUserAuthorityDetails(userId, sectorAssociationId))
                .thenThrow(new BusinessException(CcaErrorCode.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION));

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> sectorUserManagementService.getSectorUserBySectorAssociationIdAndUserId(sectorAssociationId, userId));

        // Verify
        assertEquals(CcaErrorCode.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION, businessException.getErrorCode());
        verify(authorityService, times(1)).getSectorUserAuthorityDetails(userId, sectorAssociationId);
        verify(authService, never()).getUserRepresentationById(userId);
        verify(sectorUserMapper, never()).toSectorUserDTO(userRepresentation);
    }

    @Test
    void resetOperator2Fa() {
        final Long sectorAssociationId = 1L;
        final String userId = "userId";
        final CcaAuthority authority = CcaAuthority.builder().userId(userId).sectorAssociationId(sectorAssociationId).build();


        when(authorityService.getSectorUserAuthority(userId, sectorAssociationId))
                .thenReturn(authority);

        // Invoke
        sectorUserManagementService.resetSectorUser2Fa(sectorAssociationId, userId);

        // Verify
        verify(userSecuritySetupService, times(1)).resetUser2Fa(userId);
    }

    @Test
    void resetOperator2Fa_user_not_related_to_sector_association() {
        final Long sectorAssociationId = 1L;
        final String userId = "userId";

        when(authorityService.getSectorUserAuthorityDetails(userId, sectorAssociationId))
                .thenThrow(new BusinessException(CcaErrorCode.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION));

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> sectorUserManagementService.getSectorUserBySectorAssociationIdAndUserId(sectorAssociationId, userId));

        // Verify
        assertEquals(CcaErrorCode.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION, businessException.getErrorCode());
        verify(userSecuritySetupService, never()).resetUser2Fa(anyString());
    }

    @Test
    void updateSectorUserBySectorAssociationAndIdTest() {
        final Long sectorAssociationId = 1L;
        final String userId = "userId";
        final SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO = buildsectorUserAuthorityDetailsDTO();

        // Invoke
        sectorUserManagementService.updateSectorUser(sectorAssociationId, userId, sectorUserAuthorityDetailsDTO);

        // Verify
        verify(sectorUserAuthService, times(1)).updateSectorUser(sectorUserAuthorityDetailsDTO);
    }

    @Test
    void updateSectorUserBySectorAssociationAndIdWhenSectorOrUserNotExists() {
        final Long sectorAssociationId = 1L;
        final String userId = "userId";
        final SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO = buildsectorUserAuthorityDetailsDTO();

        when(sectorUserAuthorityUpdateService.updateSectorUserAuthorityDetails(sectorAssociationId, userId, sectorUserAuthorityDetailsDTO.getContactType(), sectorUserAuthorityDetailsDTO.getOrganisationName(), true))
                .thenThrow(new BusinessException(CcaErrorCode.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION));

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> sectorUserManagementService.updateSectorUser(sectorAssociationId, userId, sectorUserAuthorityDetailsDTO));

        // Verify
        assertEquals(CcaErrorCode.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION, businessException.getErrorCode());
        verify(sectorUserAuthService, never()).updateSectorUser(sectorUserAuthorityDetailsDTO);
    }

    @Test
    void updateCurrentSectorUserTest() {
        final Long sectorAssociationId = 1L;
        final String userId = "userId";

        AppUser appUser = AppUser.builder()
                .userId(userId)
                .roleType(SECTOR_USER)
                .build();

        final SectorUserAuthorityDetailsDTO sectorUserAuthorityDetailsDTO = buildsectorUserAuthorityDetailsDTO();

        final CcaAuthority ccaAuthority = CcaAuthority.builder()
                .userId(userId)
                .sectorAssociationId(sectorAssociationId)
                .build();

        final CcaAuthorityDetails authorityDetails = CcaAuthorityDetails.builder()
                .authority(ccaAuthority)
                .contactType(ContactType.SECTOR_ASSOCIATION)
                .organisationName("GIANT")
                .build();

        when(sectorAssociationAuthorizationResourceService
                .hasUserScopeToSectorAssociation(appUser, Scope.EDIT_USER, sectorAssociationId)).thenReturn(false);
        doReturn(authorityDetails)
                .when(sectorUserAuthorityUpdateService)
                .updateSectorUserAuthorityDetails(sectorAssociationId, userId, sectorUserAuthorityDetailsDTO.getContactType(), sectorUserAuthorityDetailsDTO.getOrganisationName(), false);
        // Invoke
        sectorUserManagementService.updateCurrentSectorUser(appUser, sectorAssociationId, sectorUserAuthorityDetailsDTO);

        // Verify
        verify(sectorAssociationAuthorizationResourceService, times(1)).hasUserScopeToSectorAssociation(appUser, Scope.EDIT_USER, sectorAssociationId);
        verify(sectorUserAuthorityUpdateService, times(1)).updateSectorUserAuthorityDetails(sectorAssociationId, userId, ContactType.CONSULTANT, "GIANT", false);
        verify(sectorUserAuthService, times(1)).updateSectorUser(sectorUserAuthorityDetailsDTO);

        assertThat(authorityDetails.getContactType()).isEqualTo(ContactType.SECTOR_ASSOCIATION);
        assertThat(authorityDetails.getOrganisationName()).isEqualTo("GIANT");
    }

    private SectorUserAuthorityDetailsDTO buildsectorUserAuthorityDetailsDTO() {
        return SectorUserAuthorityDetailsDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .contactType(ContactType.CONSULTANT)
                .organisationName("GIANT")
                .build();
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
