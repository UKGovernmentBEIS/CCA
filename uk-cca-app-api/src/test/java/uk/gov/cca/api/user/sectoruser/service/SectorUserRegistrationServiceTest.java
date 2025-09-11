package uk.gov.cca.api.user.sectoruser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import static org.mockito.Mockito.*;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class SectorUserRegistrationServiceTest {

    @InjectMocks
    private SectorUserRegistrationService service;

    @Mock
    private SectorUserAuthService sectorUserAuthService;

    @Mock
    private SectorUserAuthorityService sectorUserAuthorityService;

    @Test
    void registerUserToSectorAssociationWithStatusPending() {
        String roleCode = "roleCode";
        String userId = "userId";
        String email = "email";
        Long sectorAssociationId = 1L;
        String authorityUuid = "authorityUuid";
        AppUser currentUser = createAppUser("current_user_id", REGULATOR);
        SectorUserInvitationDTO sectorUserInvitationDTO = createSectorUserInvitationDTO(email, roleCode);

        when(sectorUserAuthService
                .registerSectorUser(sectorUserInvitationDTO.getEmail(), sectorUserInvitationDTO.getFirstName(), sectorUserInvitationDTO.getLastName()))
                .thenReturn(userId);
        when(sectorUserAuthorityService
                .createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, ContactType.SECTOR_ASSOCIATION, userId, currentUser))
                .thenReturn(authorityUuid);

        service.registerUserToSectorAssociationWithStatusPending(sectorUserInvitationDTO, sectorAssociationId, currentUser);

        verify(sectorUserAuthService, times(1))
                .registerSectorUser(sectorUserInvitationDTO.getEmail(), sectorUserInvitationDTO.getFirstName(), sectorUserInvitationDTO.getLastName());
        verify(sectorUserAuthorityService, times(1))
                .createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, ContactType.SECTOR_ASSOCIATION, userId, currentUser);
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
