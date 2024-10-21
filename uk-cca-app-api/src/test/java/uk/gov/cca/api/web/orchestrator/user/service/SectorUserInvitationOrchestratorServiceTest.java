package uk.gov.cca.api.web.orchestrator.user.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserInvitationService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@ExtendWith(MockitoExtension.class)
class SectorUserInvitationOrchestratorServiceTest {

    @InjectMocks
    private SectorUserInvitationOrchestratorService service;

    @Mock
    private SectorUserInvitationService sectorUserInvitationService;

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Test
    void inviteUserToSectorAssociation() {
        Long sectorAssociationId = 1L;
        String sectorAssociationName = "sectorAssociationName";
        String currentUserId = "currentUserId";
        String userRoleCode = "sector_user_administrator";
        String userId = "254cad93-d1f5-4951-bb0e-e9b0a1586844";
        String email = "email";
        String authorityUuid = "authorityUuid";
        AppUser currentUser = createAppUser(currentUserId, SECTOR_USER);
        final SectorUserInvitationDTO sectorUserInvitationDTO = createSectorUserInvitationDTO(email, userRoleCode);

        when(sectorAssociationQueryService.getSectorAssociationIdentifier(sectorAssociationId)).thenReturn(sectorAssociationName);

        service.inviteUserToSectorAssociation(sectorAssociationId, sectorUserInvitationDTO, currentUser);

        verify(sectorUserInvitationService, times(1))
                .inviteUserToSectorAssociation(sectorAssociationId, sectorAssociationName, sectorUserInvitationDTO, currentUser);
        verify(sectorAssociationQueryService, times(1)).getSectorAssociationIdentifier(sectorAssociationId);
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
}
