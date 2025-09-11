package uk.gov.cca.api.authorization.ccaauth.core.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.netz.api.authorization.core.domain.*;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.authorization.core.repository.RoleRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.utils.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CcaAuthorityAssignmentService {
    private final CcaAuthorityDetailsRepository authorityDetailsRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    @Transactional
    public String createAuthorityInfoForRole(CcaAuthorityDetails authorityDetails, Role role) {
        final Authority authority = authorityDetails.getAuthority();
        this.updateAuthorityPermissionsForRole(authority, role);

        final CcaAuthorityDetails savedAuthorityDetails = this.authorityDetailsRepository.save(authorityDetails);
        return savedAuthorityDetails.getAuthority().getUuid();
    }

    @Transactional
    public String updatePendingAuthority(CcaAuthorityDetails authorityDetails, String roleCode, ContactType contactType, String authModificationUserId) {
        Authority authority = authorityDetails.getAuthority();
        if (!authority.getCode().equals(roleCode)) {
            this.assignAuthorityWithNewRole(authority, getRoleByRoleCode(roleCode));
        }

        authority.setUuid(UuidGenerator.generate());
        authorityDetails.setContactType(contactType);
        this.updateAuthorityAuditingInfo(authority, authModificationUserId);
        final CcaAuthorityDetails savedAuthorityDetails = this.authorityDetailsRepository.save(authorityDetails);
        return savedAuthorityDetails.getAuthority().getUuid();
    }

    public Authority updateAuthorityPermissionsForRole(Authority authority, Role role) {
        final List<String> permissions = role.getRolePermissions()
                .stream()
                .map(RolePermission::getPermission)
                .toList();

        return this.updateAuthorityWithPermissions(authority, permissions);
    }

    public Authority updateAuthorityWithPermissions(Authority authority, List<String> permissions) {
        permissions.forEach(permission ->
            this.addPermissionToAuthority(authority, permission)
        );
        return authority;
    }

    @Transactional
    public Authority updateAuthorityStatus(Long authorityId, AuthorityStatus status) {
        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        authority.setStatus(status);
        return authority;
    }

    protected Role getRoleByRoleCode(String roleCode) {
        return this.roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private void addPermissionToAuthority(Authority authority, String permission) {
        authority.addPermission(AuthorityPermission.builder().permission(permission).build());
    }

    public void assignAuthorityWithNewRole(Authority authority, Role roleDTO) {
        authority.setCode(roleDTO.getCode());
        Set<AuthorityPermission> newAuthorityPermissions = this.buildNewAuthorityPermissions(authority, roleDTO);
        this.assignNewAuthorityPermissions(authority, newAuthorityPermissions);
    }

    private void assignNewAuthorityPermissions(Authority authority, Set<AuthorityPermission> newAuthorityPermissions) {
        Objects.requireNonNull(authority);
        authority.getAuthorityPermissions().removeIf(p -> !newAuthorityPermissions.contains(p));
        newAuthorityPermissions
                .stream()
                .filter(nap -> !authority.getAuthorityPermissions().contains(nap))
                .forEach(authority::addPermission);
    }

    private Set<AuthorityPermission> buildNewAuthorityPermissions(Authority authority, Role newRole) {
        return newRole.getRolePermissions()
                .stream()
                .map(rp -> AuthorityPermission.builder()
                        .authority(authority)
                        .permission(rp.getPermission())
                        .build())
                .collect(Collectors.toSet());
    }

    private void updateAuthorityAuditingInfo(Authority authority, String authModificationUserId) {
        authority.setCreatedBy(authModificationUserId);
        authority.setCreationDate(LocalDateTime.now());
    }
}
