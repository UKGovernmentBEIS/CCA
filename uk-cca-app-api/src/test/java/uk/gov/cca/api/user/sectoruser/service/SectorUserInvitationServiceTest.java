package uk.gov.cca.api.user.sectoruser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class SectorUserInvitationServiceTest {

    @InjectMocks
    private SectorUserInvitationService service;

    @Mock
    private UserAuthService authUserService;

    @Mock
    private SectorUserRegistrationService sectorUserRegistrationService;

    @Mock
    private ExistingSectorUserInvitationService existingSectorUserInvitationService;
    
    @Mock
    private SectorUserNotificationGateway sectorUserNotificationGateway;

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Test
    void inviteUserToSectorAssociationWhenUserAlreadyExists() {
        String userId = "254cad93-d1f5-4951-bb0e-e9b0a1586844";
        String userRoleCode = "sector_user_administrator";
        String email = "email";
        Long sectorAssociationId = 1L;
        String sectorAssociationName = "sectorAssociationName";
        String currentUserId = "currentUserId";
        String authorityUuid = "authorityUuid";
        AppUser currentUser = createAppUser(currentUserId, SECTOR_USER);
        final SectorUserInvitationDTO sectorUserInvitationDTO = createSectorUserInvitationDTO(email, userRoleCode);
        final UserInfoDTO userInfoDTO = createUserInfoDTO(userId);

        when(authUserService.getUserByEmail(sectorUserInvitationDTO.getEmail())).thenReturn(Optional.of(userInfoDTO));
        when(existingSectorUserInvitationService.addExistingUserToSectorAssociation(sectorUserInvitationDTO, sectorAssociationId, userId, currentUser))
                .thenReturn(authorityUuid);
        when(sectorAssociationQueryService.getSectorAssociationAcronymAndName(sectorAssociationId)).thenReturn(sectorAssociationName);

        service.inviteUserToSectorAssociation(sectorAssociationId, sectorUserInvitationDTO, currentUser);

        verify(authUserService, times(1)).getUserByEmail(email);
        verify(existingSectorUserInvitationService, times(1))
                .addExistingUserToSectorAssociation(sectorUserInvitationDTO, sectorAssociationId, userId, currentUser);
        verify(sectorUserRegistrationService, never()).registerUserToSectorAssociationWithStatusPending(any(), anyLong(), any());
        verify(sectorUserNotificationGateway, times(1)).notifyInvitedUser(sectorUserInvitationDTO, sectorAssociationName, authorityUuid);

    }

    @Test
    void inviteUserToSectorAssociationWhenUserNotExists() {
        String userId = "254cad93-d1f5-4951-bb0e-e9b0a1586844";
        String userRoleCode = "sector_user_administrator";
        String email = "email";
        Long sectorAssociationId = 1L;
        String sectorAssociationName = "sectorAssociationName";
        String currentUserId = "currentUserId";
        String authorityUuid = "authorityUuid";
        AppUser currentUser = createAppUser(currentUserId, SECTOR_USER);
        final SectorUserInvitationDTO sectorUserInvitationDTO = createSectorUserInvitationDTO(email, userRoleCode);

        when(authUserService.getUserByEmail(sectorUserInvitationDTO.getEmail())).thenReturn(Optional.empty());
        when(sectorUserRegistrationService.registerUserToSectorAssociationWithStatusPending(sectorUserInvitationDTO, sectorAssociationId, currentUser))
        	.thenReturn(authorityUuid);
        when(sectorAssociationQueryService.getSectorAssociationAcronymAndName(sectorAssociationId)).thenReturn(sectorAssociationName);

        service.inviteUserToSectorAssociation(sectorAssociationId, sectorUserInvitationDTO, currentUser);

        verify(authUserService, times(1)).getUserByEmail(sectorUserInvitationDTO.getEmail());
        verify(sectorUserRegistrationService, times(1)).registerUserToSectorAssociationWithStatusPending(any(), anyLong(), any());
        verify(existingSectorUserInvitationService, never())
                .addExistingUserToSectorAssociation(sectorUserInvitationDTO, sectorAssociationId, userId, currentUser);
        verify(sectorUserNotificationGateway, times(1)).notifyInvitedUser(sectorUserInvitationDTO, sectorAssociationName, authorityUuid);
    }

    @Test
    void inviteUserToSectorAssociationWhenRegulatorRegisteredExists() {
        String userId = "254cad93-d1f5-4951-bb0e-e9b0a1586844";
        String userRoleCode = "regulator_administrator";
        String email = "email";
        Long sectorAssociationId = 1L;
        String sectorAssociationName = "sectorAssociationName";
        String currentUserId = "currentUserId";
        String authorityUuid = "authorityUuid";
        AppUser currentUser = createAppUser(currentUserId, REGULATOR);
        final SectorUserInvitationDTO sectorUserInvitationDTO = createSectorUserInvitationDTO(email, userRoleCode);
        final UserInfoDTO userInfoDTO = createUserInfoDTO(userId);

        when(authUserService.getUserByEmail(sectorUserInvitationDTO.getEmail())).thenReturn(Optional.of(userInfoDTO));
        when(existingSectorUserInvitationService.addExistingUserToSectorAssociation(sectorUserInvitationDTO, sectorAssociationId, userId, currentUser))
        	.thenThrow(new BusinessException(ErrorCode.USER_ROLE_ALREADY_EXISTS));
        when(sectorAssociationQueryService.getSectorAssociationAcronymAndName(sectorAssociationId)).thenReturn(sectorAssociationName);

        BusinessException businessException = 
    			assertThrows(BusinessException.class, () -> service.inviteUserToSectorAssociation(sectorAssociationId, sectorUserInvitationDTO, currentUser));

        assertEquals(ErrorCode.USER_ROLE_ALREADY_EXISTS, businessException.getErrorCode());

        verify(authUserService, times(1)).getUserByEmail(email);
        verify(existingSectorUserInvitationService, times(1))
                .addExistingUserToSectorAssociation(sectorUserInvitationDTO, sectorAssociationId, userId, currentUser);
        verify(sectorUserRegistrationService, never()).registerUserToSectorAssociationWithStatusPending(any(), anyLong(), any());
        verify(sectorUserNotificationGateway, times(0)).notifyInvitedUser(sectorUserInvitationDTO, sectorAssociationName, authorityUuid);
    }

    private AppUser createAppUser(String userId, String roleType) {
        return AppUser.builder()
                .userId(userId)
                .roleType(roleType)
                .build();
    }

    private SectorUserInvitationDTO createSectorUserInvitationDTO(String email, String roleCode) {
        return SectorUserInvitationDTO.builder()
                .email(email)
                .contactType(ContactType.SECTOR_ASSOCIATION)
                .roleCode(roleCode)
                .build();
    }

    private UserInfoDTO createUserInfoDTO(String userId) {
        UserInfoDTO user = new UserInfoDTO();
        user.setUserId(userId);
        return user;
    }
}