package uk.gov.cca.api.user.sectoruser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorAuthorityQueryService;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistingSectorUserInvitationServiceTest {


    @InjectMocks
    private ExistingSectorUserInvitationService existingSectorUserInvitationService;
    @Mock
    private SectorUserAuthorityService sectorUserAuthorityService;
    @Mock
    private SectorUserAuthService sectorUserAuthService;

    @Mock
    private SectorAuthorityQueryService sectorAuthorityQueryService;

    @Test
    void addExistingUserToSectorAssociationWhenUserIsPending() {
        String email = "email";
        String roleCode = "roleCode";
        String userId = "userId";
        Long sectorAssociationId = 1L;
        String authorityUuid = "authUuid";
        ContactType contactType = ContactType.SECTOR_ASSOCIATION;
        AppUser currentUser = AppUser.builder().userId("current_user_id").build();
        SectorUserInvitationDTO sectorUserInvitationDTO = createSectorUserInvitationDTO(email, roleCode, contactType);

        when(sectorAuthorityQueryService.existsAuthorityNotForSectorAssociation(userId)).thenReturn(false);
        when(sectorUserAuthorityService.createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, contactType, userId, currentUser))
                .thenReturn(authorityUuid);

        existingSectorUserInvitationService
                .addExistingUserToSectorAssociation(sectorUserInvitationDTO, sectorAssociationId, userId, currentUser);

        verify(sectorUserAuthService, times(1)).updateUser(sectorUserInvitationDTO);
        verify(sectorUserAuthorityService, times(1)).createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, contactType, userId, currentUser);
    }

    @Test
    void addExistingUserToSectorAssociationWhenUserIsAlreadyRegisteredForOtherSector() {
        String email = "email";
        String roleCode = "roleCode";
        String userId = "userId";
        Long sectorAssociationId = 1L;
        ContactType contactType = ContactType.SECTOR_ASSOCIATION;
        AppUser currentUser = AppUser.builder().userId(userId).build();
        SectorUserInvitationDTO sectorUserInvitationDTO = createSectorUserInvitationDTO(email, roleCode, contactType);

        when(sectorAuthorityQueryService.existsAuthorityNotForSectorAssociation(userId)).thenReturn(false);
        when(sectorUserAuthorityService.createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, contactType, userId, currentUser))
                .thenThrow(new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED));


        BusinessException ex = assertThrows(BusinessException.class, () -> {
            existingSectorUserInvitationService
                    .addExistingUserToSectorAssociation(sectorUserInvitationDTO, sectorAssociationId, userId, currentUser);
        });
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);

        verify(sectorUserAuthorityService, times(1)).createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, contactType, userId, currentUser);

    }

    @Test
    void addExistingUserToSectorAssociationWhenUserIsRegisteredAsRegulator() {
        String email = "email";
        String roleCode = "roleCode";
        String userId = "userId";
        Long sectorAssociationId = 1L;
        ContactType contactType = ContactType.SECTOR_ASSOCIATION;
        AppUser currentUser = AppUser.builder().userId("current_user_id").build();
        SectorUserInvitationDTO sectorUserInvitationDTO = createSectorUserInvitationDTO(email, roleCode, contactType);

        when(sectorAuthorityQueryService.existsAuthorityNotForSectorAssociation(userId)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            existingSectorUserInvitationService
                    .addExistingUserToSectorAssociation(sectorUserInvitationDTO, sectorAssociationId, userId, currentUser);
        });

        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_THAN_SECTOR_USER);

        verify(sectorUserAuthorityService, never()).createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, contactType, userId, currentUser);
        verify(sectorUserAuthService, never()).updateUser(sectorUserInvitationDTO);
        verify(sectorUserAuthorityService, never()).createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, contactType, userId, currentUser);

    }

    private SectorUserInvitationDTO createSectorUserInvitationDTO(String email, String roleCode, ContactType contactType) {
        return SectorUserInvitationDTO.builder()
                .contactType(contactType)
                .email(email)
                .roleCode(roleCode)
                .build();
    }
}
