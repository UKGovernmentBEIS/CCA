package uk.gov.cca.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityService;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserInvitationDTO;
import uk.gov.cca.api.user.operator.transform.OperatorUserInvitationMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.netz.api.user.operator.service.OperatorUserAuthService;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CcaExistingOperatorUserInvitationServiceTest {


    @InjectMocks
    private CcaExistingOperatorUserInvitationService existingOperatorUserInvitationService;

    @Mock
    private CcaOperatorAuthorityService operatorUserAuthorityService;
    @Mock
    private OperatorUserAuthService operatorUserAuthService;

    @Mock
    private OperatorAuthorityQueryService operatorAuthorityQueryService;

    @Mock
    private OperatorUserInvitationMapper operatorUserInvitationMapper;

    @Test
    void addExistingUserToTargetUnitWhenUserIsPending() {
        String email = "email";
        String roleCode = "operator_basic_user";
        String userId = "userId";
        Long accountId = 1L;
        String authorityUuid = "authUuid";
        ContactType contactType = ContactType.OPERATOR;
        AppUser currentUser = AppUser.builder().userId("current_user_id").build();
        CcaOperatorUserInvitationDTO ccaOperatorUserInvitationDTO = createOperatorUserInvitationDTO(email, roleCode, contactType);

        OperatorUserInvitationDTO operatorUserInvitationDTO = OperatorUserInvitationDTO.builder().email(email).roleCode(roleCode).build();

        when(operatorUserInvitationMapper.toUserInvitationDTO(ccaOperatorUserInvitationDTO)).thenReturn(operatorUserInvitationDTO);
        when(operatorAuthorityQueryService.existsAuthorityNotForAccount(userId)).thenReturn(false);
        when(operatorUserAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, contactType, userId, currentUser))
                .thenReturn(authorityUuid);

        existingOperatorUserInvitationService
                .addExistingUserToTargetUnit(ccaOperatorUserInvitationDTO, accountId, userId, currentUser);

        verify(operatorUserAuthService, times(1)).updateUser(operatorUserInvitationDTO);
        verify(operatorUserAuthorityService, times(1)).createPendingAuthorityForOperator(accountId, roleCode, contactType, userId, currentUser);
    }

    @Test
    void addExistingUserToTargetUnitWhenUserIsAlreadyRegisteredForAnotherAccount() {
        String email = "email";
        String roleCode = "operator_basic_user";
        String userId = "userId";
        Long accountId = 1L;
        ContactType contactType = ContactType.OPERATOR;
        AppUser currentUser = AppUser.builder().userId(userId).build();
        CcaOperatorUserInvitationDTO ccaOperatorUserInvitationDTO = createOperatorUserInvitationDTO(email, roleCode, contactType);

        OperatorUserInvitationDTO operatorUserInvitationDTO = OperatorUserInvitationDTO.builder().email(email).roleCode(roleCode).build();

        when(operatorAuthorityQueryService.existsAuthorityNotForAccount(userId)).thenReturn(false);
        when(operatorUserAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, contactType, userId, currentUser))
                .thenThrow(new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED));


        BusinessException ex = assertThrows(BusinessException.class, () -> {
            existingOperatorUserInvitationService
                    .addExistingUserToTargetUnit(ccaOperatorUserInvitationDTO, accountId, userId, currentUser);
        });
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);

        verify(operatorUserAuthService, never()).updateUser(operatorUserInvitationDTO);
        verify(operatorUserAuthorityService, times(1)).createPendingAuthorityForOperator(accountId, roleCode, contactType, userId, currentUser);

    }

    @Test
    void addExistingUserToTargetUnitWhenUserIsRegisteredAsRegulator() {
        String email = "email";
        String roleCode = "operator_basic_user";
        String userId = "userId";
        Long accountId = 1L;
        ContactType contactType = ContactType.OPERATOR;
        AppUser currentUser = AppUser.builder().userId("current_user_id").build();
        CcaOperatorUserInvitationDTO ccaOperatorUserInvitationDTO = createOperatorUserInvitationDTO(email, roleCode, contactType);

        OperatorUserInvitationDTO operatorUserInvitationDTO = OperatorUserInvitationDTO.builder().email(email).roleCode(roleCode).build();

        when(operatorAuthorityQueryService.existsAuthorityNotForAccount(userId)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            existingOperatorUserInvitationService
                    .addExistingUserToTargetUnit(ccaOperatorUserInvitationDTO, accountId, userId, currentUser);
        });

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_THAN_OPERATOR);

        verify(operatorUserAuthorityService, never()).createPendingAuthorityForOperator(accountId, roleCode, contactType, userId, currentUser);
        verify(operatorUserAuthService, never()).updateUser(operatorUserInvitationDTO);
        verify(operatorUserAuthorityService, never()).createPendingAuthorityForOperator(accountId, roleCode, contactType, userId, currentUser);

    }

    private CcaOperatorUserInvitationDTO createOperatorUserInvitationDTO(String email, String roleCode, ContactType contactType) {
        return CcaOperatorUserInvitationDTO.builder()
                .roleCode(roleCode)
                .contactType(contactType)
                .email(email)
                .build();
    }
}
