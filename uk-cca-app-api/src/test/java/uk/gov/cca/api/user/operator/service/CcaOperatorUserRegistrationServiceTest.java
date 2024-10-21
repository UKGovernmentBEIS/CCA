package uk.gov.cca.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityService;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.user.operator.service.OperatorUserAuthService;

import static org.mockito.Mockito.*;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class CcaOperatorUserRegistrationServiceTest {

    @InjectMocks
    private CcaOperatorUserRegistrationService service;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;

    @Mock
    private CcaOperatorAuthorityService operatorUserAuthorityService;

    @Mock
    private CcaOperatorUserAuthService ccaOperatorUserAuthService;

    @Test
    void registerUserToTargetUnitWithStatusPending() {
        String roleCode = "operator_basic_user";
        String userId = "userId";
        String email = "email";
        Long accountId = 1L;
        String authorityUuid = "authorityUuid";
        AppUser currentUser = createAppUser("current_user_id", REGULATOR);
        CcaOperatorUserInvitationDTO operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, roleCode);

        when(ccaOperatorUserAuthService.registerOperatorUser(operatorUserInvitationDTO)).thenReturn(userId);
        when(operatorUserAuthorityService
                .createPendingAuthorityForOperator(accountId, roleCode, ContactType.OPERATOR, userId, currentUser))
                .thenReturn(authorityUuid);

        service.registerUserToAccountWithStatusPending(operatorUserInvitationDTO, accountId, currentUser);

        verify(ccaOperatorUserAuthService, times(1)).registerOperatorUser(operatorUserInvitationDTO);
        verify(operatorUserAuthorityService, times(1))
                .createPendingAuthorityForOperator(accountId, roleCode, ContactType.OPERATOR, userId, currentUser);
    }

    private AppUser createAppUser(String userId, String roleType) {
        return AppUser.builder()
                .userId(userId)
                .roleType(roleType)
                .build();
    }

    private CcaOperatorUserInvitationDTO createOperatorUserInvitationDTO(String email, String roleCode) {
        return CcaOperatorUserInvitationDTO.builder()
                .email(email)
                .roleCode(roleCode)
                .contactType(ContactType.OPERATOR)
                .build();
    }
}
