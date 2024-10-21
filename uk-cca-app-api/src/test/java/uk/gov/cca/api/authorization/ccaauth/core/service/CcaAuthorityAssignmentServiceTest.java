package uk.gov.cca.api.authorization.ccaauth.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.netz.api.authorization.core.domain.*;
import uk.gov.netz.api.authorization.core.repository.RoleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_SECTOR_ASSOCIATION_EDIT;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_SECTOR_USERS_EDIT;


@ExtendWith(MockitoExtension.class)
public class CcaAuthorityAssignmentServiceTest {
    @InjectMocks
    private CcaAuthorityAssignmentService authorityAssignmentService;
    @Mock
    private CcaAuthorityDetailsRepository authorityDetailsRepository;
    @Mock
    private RoleRepository roleRepository;

    @Test
    void createAuthorityInfoForRole() {
        CcaAuthority authority = CcaAuthority.builder().userId("user").status(AuthorityStatus.ACTIVE).build();
        CcaAuthorityDetails ccaAuthorityDetails = CcaAuthorityDetails.builder()
                .authority(authority)
                .contactType(ContactType.SECTOR_ASSOCIATION)
                .build();

        List<AuthorityPermission> expectedAuthPermissions = List.of(
                AuthorityPermission.builder().authority(authority).permission(PERM_SECTOR_ASSOCIATION_EDIT).build(),
                AuthorityPermission.builder().authority(authority).permission(PERM_SECTOR_USERS_EDIT).build()
        );

        Role role = Role.builder().code("code").build();
        expectedAuthPermissions.forEach(permission -> role.addPermission(
                RolePermission.builder().permission(permission.getPermission()).role(role).build()));

        when(authorityDetailsRepository.save(ccaAuthorityDetails)).thenReturn(ccaAuthorityDetails);

        authorityAssignmentService.createAuthorityInfoForRole(ccaAuthorityDetails, role);

        ArgumentCaptor<CcaAuthorityDetails> authorityCaptor = ArgumentCaptor.forClass(CcaAuthorityDetails.class);
        verify(authorityDetailsRepository, times(1)).save(authorityCaptor.capture());
        Authority savedAuthority = authorityCaptor.getValue().getAuthority();

        assertThat(savedAuthority.getAuthorityPermissions()).containsExactlyInAnyOrderElementsOf(expectedAuthPermissions);
    }


    @Test
    void updateAuthorityPermissionsForRole() {
        CcaAuthority authority = CcaAuthority.builder().userId("user").status(AuthorityStatus.ACTIVE).build();

        List<AuthorityPermission> expectedAuthPermissions = List.of(
                AuthorityPermission.builder().authority(authority).permission(PERM_SECTOR_ASSOCIATION_EDIT).build(),
                AuthorityPermission.builder().authority(authority).permission(PERM_SECTOR_USERS_EDIT).build()
        );

        Role role = Role.builder().code("code").build();
        expectedAuthPermissions.forEach(permission -> role.addPermission(
                RolePermission.builder().permission(permission.getPermission()).role(role).build()));

        authorityAssignmentService.updateAuthorityPermissionsForRole(authority, role);

        assertThat(authority.getAuthorityPermissions()).containsExactlyInAnyOrderElementsOf(expectedAuthPermissions);
    }

    @Test
    void updateAuthorityWithPermissions() {
        CcaAuthority authority = CcaAuthority.builder().userId("user").status(AuthorityStatus.ACTIVE).build();
        CcaAuthorityDetails ccaAuthorityDetails = CcaAuthorityDetails.builder()
                .authority(authority)
                .contactType(ContactType.SECTOR_ASSOCIATION)
                .build();

        List<AuthorityPermission> expectedAuthPermissions = List.of(
                AuthorityPermission.builder().authority(authority).permission(PERM_SECTOR_ASSOCIATION_EDIT).build(),
                AuthorityPermission.builder().authority(authority).permission(PERM_SECTOR_USERS_EDIT).build()
        );

        authorityAssignmentService.updateAuthorityWithPermissions(ccaAuthorityDetails.getAuthority(),
                expectedAuthPermissions.stream().map(AuthorityPermission::getPermission).collect(Collectors.toList()));

        assertThat(authority.getAuthorityPermissions()).containsExactlyInAnyOrderElementsOf(expectedAuthPermissions);
    }

    @Test
    void updateAuthorityWithNewRole() {
        final String newRoleCode = "newRoleCode";
        CcaAuthority authority = CcaAuthority.builder()
                .userId("user")
                .status(AuthorityStatus.ACTIVE)
                .code("roleCode")
                .build();

        CcaAuthorityDetails ccaAuthorityDetails = CcaAuthorityDetails.builder()
                .authority(authority)
                .contactType(ContactType.CONSULTANT)
                .build();

        authority.setAuthorityPermissions(new ArrayList<>(List.of(
                AuthorityPermission.builder().authority(authority).permission(PERM_SECTOR_USERS_EDIT).build(),
                AuthorityPermission.builder().authority(authority).permission(PERM_SECTOR_ASSOCIATION_EDIT).build()
        )));

        List<AuthorityPermission> expectedAuthPermissions = List.of(
                AuthorityPermission.builder().authority(authority).permission(PERM_SECTOR_ASSOCIATION_EDIT).build(),
                AuthorityPermission.builder().authority(authority).permission(PERM_SECTOR_USERS_EDIT).build()
        );

        Role newRole = Role.builder().code(newRoleCode).build();

        expectedAuthPermissions.forEach(permission -> newRole.addPermission(
                RolePermission.builder().permission(permission.getPermission()).role(newRole).build()));

        when(roleRepository.findByCode(newRoleCode)).thenReturn(Optional.ofNullable(newRole));
        when(authorityDetailsRepository.save(ccaAuthorityDetails)).thenReturn(ccaAuthorityDetails);

        authorityAssignmentService.updatePendingAuthority(ccaAuthorityDetails, newRoleCode, ContactType.SECTOR_ASSOCIATION,"user");

        ArgumentCaptor<CcaAuthorityDetails> authorityCaptor = ArgumentCaptor.forClass(CcaAuthorityDetails.class);
        verify(authorityDetailsRepository, times(1)).save(authorityCaptor.capture());
        CcaAuthorityDetails  savedCcaAuthorityDetails = authorityCaptor.getValue();

        Authority savedAuthority = savedCcaAuthorityDetails.getAuthority();

        assertThat(savedCcaAuthorityDetails.getContactType()).isEqualTo(ContactType.SECTOR_ASSOCIATION);
        assertThat(savedAuthority.getCode()).isEqualTo(newRole.getCode());
        assertThat(savedAuthority.getUserId()).isEqualTo(authority.getUserId());
        assertThat(savedAuthority.getStatus()).isEqualTo(authority.getStatus());
        assertThat(savedAuthority.getAuthorityPermissions()).containsExactlyInAnyOrderElementsOf(expectedAuthPermissions);
    }

}
