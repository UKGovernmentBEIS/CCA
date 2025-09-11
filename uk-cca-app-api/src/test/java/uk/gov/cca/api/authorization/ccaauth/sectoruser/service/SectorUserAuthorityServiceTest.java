package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityAssignmentService;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.Role;
import uk.gov.netz.api.authorization.core.repository.RoleRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectorUserAuthorityServiceTest {

    @InjectMocks
    private SectorUserAuthorityService sectorUserAuthorityService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CcaAuthorityRepository authorityRepository;
    
    @Mock
    private CcaAuthorityService authorityService;

    @Mock
    private CcaAuthorityAssignmentService authorityAssignmentService;


    @Test
    void createPendingAuthorityForSectorUser_pending_authority_exists() {
        Long sectorAssociationId = 1L;
        String roleCode = "roleCode";
        String userId = "userId";
        String authorityUuid = "uuid";
        AppUser modificationUser = AppUser.builder().userId("current_user_id").build();
        CcaAuthority existingAuthority = createAuthority(userId, roleCode, sectorAssociationId, AuthorityStatus.PENDING, authorityUuid);
        CcaAuthorityDetails existingAuthorityDetails = CcaAuthorityDetails.builder()
                .authority(existingAuthority)
                .contactType(ContactType.SECTOR_ASSOCIATION)
                .build();

        when(authorityRepository.findByUserIdAndSectorAssociationId(userId, sectorAssociationId))
                .thenReturn(Optional.of(existingAuthority));

        when(authorityService.getAuthorityDetails(existingAuthority.getId())).thenReturn(existingAuthorityDetails);

        when(authorityAssignmentService.updatePendingAuthority(existingAuthorityDetails, roleCode, ContactType.SECTOR_ASSOCIATION, modificationUser.getUserId()))
                .thenReturn(authorityUuid);

        String result = sectorUserAuthorityService
                .createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, ContactType.SECTOR_ASSOCIATION, userId, modificationUser);

        assertThat(result).isEqualTo(authorityUuid);

        verify(authorityRepository, times(1)).findByUserIdAndSectorAssociationId(userId, sectorAssociationId);
        verify(roleRepository, never()).findByCode(anyString());
        verify(authorityAssignmentService, times(1))
                .updatePendingAuthority(existingAuthorityDetails, roleCode, ContactType.SECTOR_ASSOCIATION, modificationUser.getUserId());
    }

    @Test
    void createPendingAuthorityForSectorUser_throws_exception_when_active_authority_exists() {
        Long sectorAssociationId = 1L;
        String roleCode = "roleCode";
        String userId = "userId";
        AppUser currentUser = AppUser.builder().userId("current_user_id").build();
        CcaAuthority existingAuthority = createAuthority(userId, "anotherRoleCode", sectorAssociationId, AuthorityStatus.ACTIVE, "authorityUuid");

        when(authorityRepository.findByUserIdAndSectorAssociationId(userId, sectorAssociationId)).thenReturn(Optional.of(existingAuthority));

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> sectorUserAuthorityService.createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, ContactType.SECTOR_ASSOCIATION, userId, currentUser));

        assertEquals(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED, businessException.getErrorCode());

        verify(authorityRepository, times(1)).findByUserIdAndSectorAssociationId(userId, sectorAssociationId);
        verify(authorityRepository, never()).save(any());
        verify(roleRepository, never()).findByCode(anyString());
    }

    @Test
    void createPendingAuthorityForSectorUser_authority_not_exists() {
        Long sectorAssociationId = 1L;
        String roleCode = "roleCode";
        String userId = "userId";
        AppUser currentUser = AppUser.builder().userId("current_user_id").build();
        Role role = Role.builder().code(roleCode).build();

        String authorityUuid = "uuid";

        when(authorityRepository.findByUserIdAndSectorAssociationId(userId, sectorAssociationId)).thenReturn(Optional.empty());
        when(roleRepository.findByCode(roleCode)).thenReturn(Optional.of(role));
        when(authorityAssignmentService.createAuthorityInfoForRole(Mockito.any(CcaAuthorityDetails.class), Mockito.eq(role)))
                .thenReturn(authorityUuid);
        String result = sectorUserAuthorityService.createPendingAuthorityForSectorUser(sectorAssociationId, roleCode, ContactType.SECTOR_ASSOCIATION, userId, currentUser);
        assertThat(result).isEqualTo(authorityUuid);

        ArgumentCaptor<CcaAuthorityDetails> authorityCaptor = ArgumentCaptor.forClass(CcaAuthorityDetails.class);
        verify(authorityRepository, times(1)).findByUserIdAndSectorAssociationId(userId, sectorAssociationId);
        verify(roleRepository, times(1)).findByCode(roleCode);
        verify(authorityAssignmentService, times(1))
                .createAuthorityInfoForRole(authorityCaptor.capture(), eq(role));

        CcaAuthority authoritySaved = (CcaAuthority) authorityCaptor.getValue().getAuthority();

        assertThat(authoritySaved).isNotNull();
        assertThat(authoritySaved.getUserId()).isEqualTo(userId);
        assertThat(authoritySaved.getCode()).isEqualTo(role.getCode());
        assertThat(authoritySaved.getUuid()).isNotNull();
        assertThat(authoritySaved.getCreatedBy()).isEqualTo(currentUser.getUserId());
        assertThat(authoritySaved.getStatus()).isEqualTo(AuthorityStatus.PENDING);
        assertThat(authoritySaved.getSectorAssociationId()).isEqualTo(sectorAssociationId);
        assertThat(authoritySaved.getVerificationBodyId()).isNull();
        assertThat(authoritySaved.getCompetentAuthority()).isNull();
    }
    
    @Test
    void findSectorUsersByCompetentAuthority() {
    	Long sectorAssociationId = 1L;
        List<String> sectorUsers = List.of("user1", "user2");

        when(authorityRepository.findActiveSectorUsersBySectorAssociationId(sectorAssociationId))
            .thenReturn(sectorUsers);

        List<String> res = sectorUserAuthorityService.findActiveSectorUsersBySectorAssociationId(sectorAssociationId);

        assertThat(res).containsExactlyInAnyOrder("user1", "user2");

        verify(authorityRepository, times(1)).findActiveSectorUsersBySectorAssociationId(sectorAssociationId);
    }


    private CcaAuthority createAuthority(String userId, String roleCode, Long sectorAssociationId, AuthorityStatus status, String authorityUuid) {
        return CcaAuthority.builder()
                .id(1L)
                .userId(userId)
                .code(roleCode)
                .sectorAssociationId(sectorAssociationId)
                .status(status)
                .authorityPermissions(new ArrayList<>())
                .uuid(authorityUuid)
                .build();
    }

}
