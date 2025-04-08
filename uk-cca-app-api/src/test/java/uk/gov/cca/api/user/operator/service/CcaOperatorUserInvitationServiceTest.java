package uk.gov.cca.api.user.operator.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserInvitationDTO;
import uk.gov.cca.api.user.operator.transform.OperatorUserInvitationMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.netz.api.user.operator.service.OperatorUserNotificationGateway;
import uk.gov.netz.api.userinfoapi.AuthenticationStatus;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class CcaOperatorUserInvitationServiceTest {


    @InjectMocks
    private CcaOperatorUserInvitationService service;

    @Mock
    private UserAuthService authUserService;

    @Mock
    private CcaOperatorUserRegistrationService operatorUserRegistrationService;

    @Mock
    private CcaExistingOperatorUserInvitationService existingOperatorUserInvitationService;

    @Mock
    private OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Mock
    private OperatorUserInvitationMapper operatorUserInvitationMapper;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Test
    void inviteUserToAccountWhenUserAlreadyExists() {
        String userId = "254cad93-d1f5-4951-bb0e-e9b0a1586844";
        String userRoleCode = "operator";
        String email = "email";
        Long accountId = 1L;
        String accountName = "accountName";
        String currentUserId = "currentUserId";
        String authorityUuid = "authorityUuid";
        AppUser currentUser = createAppUser(currentUserId, OPERATOR);
        final CcaOperatorUserInvitationDTO ccaOperatorUserInvitationDTO = createCcaOperatorUserInvitationDTO(email, userRoleCode);
        final UserInfoDTO userInfoDTO = createUserInfoDTO(userId, AuthenticationStatus.PENDING);
        OperatorUserInvitationDTO operatorUserInvitationDTO = OperatorUserInvitationDTO.builder().email(email).roleCode(userRoleCode).build();

        when(operatorUserInvitationMapper.toUserInvitationDTO(ccaOperatorUserInvitationDTO)).thenReturn(operatorUserInvitationDTO);
        when(authUserService.getUserByEmail(ccaOperatorUserInvitationDTO.getEmail())).thenReturn(Optional.of(userInfoDTO));
        when(existingOperatorUserInvitationService.addExistingUserToTargetUnit(ccaOperatorUserInvitationDTO, accountId, userId, currentUser))
                .thenReturn(authorityUuid);
        when(targetUnitAccountQueryService.getAccountName(accountId)).thenReturn(accountName);

        service.inviteUserToAccount(accountId, ccaOperatorUserInvitationDTO, currentUser);

        verify(authUserService, times(1)).getUserByEmail(email);
        verify(existingOperatorUserInvitationService, times(1))
                .addExistingUserToTargetUnit(ccaOperatorUserInvitationDTO, accountId, userId, currentUser);
        verify(operatorUserRegistrationService, never()).registerUserToAccountWithStatusPending(any(), anyLong(), any());
        verify(operatorUserNotificationGateway, times(1)).notifyInvitedUser(operatorUserInvitationDTO, accountName, authorityUuid);

    }

    @Test
    void inviteUserToAccountWhenUserNotExists() {
        String userId = "254cad93-d1f5-4951-bb0e-e9b0a1586844";
        String userRoleCode = "operator";
        String email = "email";
        Long accountId = 1L;
        String accountName = "accountName";
        String currentUserId = "currentUserId";
        String authorityUuid = "authorityUuid";
        AppUser currentUser = createAppUser(currentUserId, OPERATOR);
        final CcaOperatorUserInvitationDTO ccaOperatorUserInvitationDTO = createCcaOperatorUserInvitationDTO(email, userRoleCode);
        OperatorUserInvitationDTO operatorUserInvitationDTO = OperatorUserInvitationDTO.builder().email(email).roleCode(userRoleCode).build();

        when(operatorUserInvitationMapper.toUserInvitationDTO(ccaOperatorUserInvitationDTO)).thenReturn(operatorUserInvitationDTO);
        when(authUserService.getUserByEmail(operatorUserInvitationDTO.getEmail())).thenReturn(Optional.empty());
        when(operatorUserRegistrationService.registerUserToAccountWithStatusPending(ccaOperatorUserInvitationDTO, accountId, currentUser))
                .thenReturn(authorityUuid);
        when(targetUnitAccountQueryService.getAccountName(accountId)).thenReturn(accountName);

        service.inviteUserToAccount(accountId, ccaOperatorUserInvitationDTO, currentUser);

        verify(authUserService, times(1)).getUserByEmail(operatorUserInvitationDTO.getEmail());
        verify(operatorUserRegistrationService, times(1)).registerUserToAccountWithStatusPending(any(), anyLong(), any());
        verify(existingOperatorUserInvitationService, never())
                .addExistingUserToTargetUnit(ccaOperatorUserInvitationDTO, accountId, userId, currentUser);
        verify(operatorUserNotificationGateway, times(1)).notifyInvitedUser(operatorUserInvitationDTO, accountName, authorityUuid);
    }

    @Test
    void inviteUserToAccountWhenRegulatorRegisteredExists() throws Exception {
        String userId = "254cad93-d1f5-4951-bb0e-e9b0a1586844";
        String userRoleCode = "regulator_administrator";
        String email = "email";
        Long accountId = 1L;
        String accountName = "accountName";
        String currentUserId = "currentUserId";
        String authorityUuid = "authorityUuid";
        AppUser currentUser = createAppUser(currentUserId, REGULATOR);
        final CcaOperatorUserInvitationDTO ccaOperatorUserInvitationDTO = createCcaOperatorUserInvitationDTO(email, userRoleCode);
        final UserInfoDTO userInfoDTO = createUserInfoDTO(userId, AuthenticationStatus.REGISTERED);
        OperatorUserInvitationDTO operatorUserInvitationDTO = OperatorUserInvitationDTO.builder().email(email).roleCode(userRoleCode).build();

        when(authUserService.getUserByEmail(operatorUserInvitationDTO.getEmail())).thenReturn(Optional.of(userInfoDTO));
        when(existingOperatorUserInvitationService.addExistingUserToTargetUnit(ccaOperatorUserInvitationDTO, accountId, userId, currentUser))
                .thenThrow(new BusinessException(ErrorCode.USER_ROLE_ALREADY_EXISTS));
        when(targetUnitAccountQueryService.getAccountName(accountId)).thenReturn(accountName);

        BusinessException businessException =
                assertThrows(BusinessException.class, () -> service.inviteUserToAccount(accountId, ccaOperatorUserInvitationDTO, currentUser));

        assertEquals(ErrorCode.USER_ROLE_ALREADY_EXISTS, businessException.getErrorCode());

        verify(authUserService, times(1)).getUserByEmail(email);
        verify(existingOperatorUserInvitationService, times(1))
                .addExistingUserToTargetUnit(ccaOperatorUserInvitationDTO, accountId, userId, currentUser);
        verify(operatorUserRegistrationService, never()).registerUserToAccountWithStatusPending(any(), anyLong(), any());
        verify(operatorUserNotificationGateway, times(0)).notifyInvitedUser(operatorUserInvitationDTO, accountName, authorityUuid);
    }

    private AppUser createAppUser(String userId, String roleType) {
        return AppUser.builder()
                .userId(userId)
                .roleType(roleType)
                .build();
    }

    private CcaOperatorUserInvitationDTO createCcaOperatorUserInvitationDTO(String email, String roleCode) {
        return CcaOperatorUserInvitationDTO.builder()
                .email(email)
                .contactType(ContactType.OPERATOR)
                .build();
    }

    private UserInfoDTO createUserInfoDTO(String userId, AuthenticationStatus authenticationStatus) {
        UserInfoDTO user = new UserInfoDTO();
        user.setUserId(userId);
        return user;
    }
}
