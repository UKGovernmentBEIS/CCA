package uk.gov.cca.api.web.orchestrator.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserInvitationDTO;
import uk.gov.cca.api.user.operator.service.CcaOperatorUserInvitationService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import static org.mockito.Mockito.*;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;


@ExtendWith(MockitoExtension.class)
public class OperatorInvitationOrchestratorServiceTest {
    
    @InjectMocks
    private OperatorInvitationOrchestratorService service;

    @Mock
    private CcaOperatorUserInvitationService operatorUserInvitationService;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Test
    void inviteUserToAccount() {
        Long sectorAssociationId = 1L;
        String accountName = "accountName";
        String currentUserId = "currentUserId";
        String userRoleCode = "operator";
        String email = "email";
        AppUser currentUser = createAppUser(currentUserId, REGULATOR);
        final CcaOperatorUserInvitationDTO sectorUserInvitationDTO = createCcaOperatorUserInvitationDTO(email, userRoleCode);

        when(targetUnitAccountQueryService.getAccountName(sectorAssociationId)).thenReturn(accountName);

        service.inviteUserToAccount(sectorAssociationId, sectorUserInvitationDTO, currentUser);

        verify(operatorUserInvitationService, times(1))
                .inviteUserToTargetUnit(sectorAssociationId, accountName, sectorUserInvitationDTO, currentUser);
        verify(targetUnitAccountQueryService, times(1)).getAccountName(sectorAssociationId);
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
}
