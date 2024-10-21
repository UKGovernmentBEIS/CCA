package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.Generated;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityAssignmentService;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaAuthorityService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.Role;
import uk.gov.netz.api.authorization.core.repository.RoleRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.utils.UuidGenerator;

@Service
@AllArgsConstructor
public class SectorUserAuthorityService {
    @Generated
    private static final Logger log = LogManager.getLogger(SectorUserAuthorityService.class);
    private final RoleRepository roleRepository;
    private final CcaAuthorityService authorityService;
    private final CcaAuthorityRepository authorityRepository;
    private final CcaAuthorityAssignmentService authorityAssignmentService;

    @Transactional
    public String createPendingAuthorityForSectorUser(Long sectorAssociationId, String roleCode, ContactType contactType, String userId, AppUser authModificationUser) {

        Optional<CcaAuthority> userAuthorityForSectorAssociation =
                this.authorityRepository.findByUserIdAndSectorAssociationId(userId, sectorAssociationId);

        if (userAuthorityForSectorAssociation.isPresent()) {
            CcaAuthority authority = userAuthorityForSectorAssociation.get();
            if (AuthorityStatus.PENDING.equals(authority.getStatus())) {
                final CcaAuthorityDetails authorityDetails = authorityService.getAuthorityDetails(authority.getId());
                return this.authorityAssignmentService.updatePendingAuthority(authorityDetails, roleCode, contactType, authModificationUser.getUserId());
            } else {
                log.warn("Authority for user '{}' in sector association '{}' exists with code '{}' and status'{}'",
                        authority.getUserId(), authority.getSectorAssociationId(), authority.getCode(), authority.getStatus());
                throw new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
            }
        }

        return this.createSectorUserAuthorityForRole(sectorAssociationId, roleCode, contactType, userId, authModificationUser.getUserId());

    }
    
    public CcaAuthorityDetails getSectorUserAuthorityDetails(String userId, Long sectorAssociationId) {
        return authorityService.getAuthorityDetails(getSectorUserAuthority(userId, sectorAssociationId).getId());
    }
    
    public CcaAuthority getSectorUserAuthority(String userId, Long sectorAssociationId) {
        // Check if user id exists on sector's users
        return authorityRepository.findByUserIdAndSectorAssociationId(userId, sectorAssociationId)
                .orElseThrow(() -> new BusinessException(CcaErrorCode.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION));
    }
    
    public List<String> findActiveSectorUsersBySectorAssociationId(Long sectorAssociationId) {
        return authorityRepository.findActiveSectorUsersBySectorAssociationId(sectorAssociationId);
    }

    public Authority acceptAuthority(Long authorityId) {
        return authorityAssignmentService
                .updateAuthorityStatus(authorityId, AuthorityStatus.ACCEPTED);
    }

    private Role getRoleByCode(String roleCode) {
        return this.roleRepository.findByCode(roleCode).orElseThrow(() ->
                new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private String createSectorUserAuthorityForRole(Long sectorAssociationId, String roleCode, ContactType contactType, String userId, String createdByUserId) {
        final CcaAuthority authority = CcaAuthority.builder()
                .userId(userId)
                .code(roleCode)
                .sectorAssociationId(sectorAssociationId)
                .status(AuthorityStatus.PENDING)
                .createdBy(createdByUserId)
                .uuid(UuidGenerator.generate())
                .build();

        final CcaAuthorityDetails authorityDetails = CcaAuthorityDetails.builder()
                .contactType(contactType)
                .authority(authority).build();

        final Role role = this.getRoleByCode(roleCode);

        return this.authorityAssignmentService.createAuthorityInfoForRole(authorityDetails, role);
    }
}
