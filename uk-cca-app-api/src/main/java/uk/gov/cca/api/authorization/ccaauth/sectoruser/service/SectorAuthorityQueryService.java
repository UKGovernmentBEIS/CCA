package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthoritiesDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorAssociationAuthorizationResourceService;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.netz.api.authorization.rules.domain.Scope;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SectorAuthorityQueryService {

    private final SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;
    private final CcaAuthorityRepository ccaAuthorityRepository;
    private final AuthorityRepository authorityRepository;

    public SectorUserAuthoritiesDTO getSectorUserAuthorities(AppUser appUser, Long sectorId) {
        boolean isEditable = sectorAssociationAuthorizationResourceService
                .hasUserScopeToSectorAssociation(appUser, Scope.EDIT_USER, sectorId);

        List<SectorUserAuthorityDTO> sectorUserAuthorityDTO = ccaAuthorityRepository.findAuthoritiesWithDetailsBySectorAssociationId(sectorId);
        return SectorUserAuthoritiesDTO.builder().authorities(sectorUserAuthorityDTO).editable(isEditable).build();
    }

    public List<SectorUserAuthorityDTO> getActiveSectorUserAuthoritiesByContactType(Long sectorId, ContactType contactType) {
        return ccaAuthorityRepository.findActiveAuthoritiesWithDetailsBySectorAssociationIdAndContactType(sectorId, contactType);
    }

    public List<AuthorityRoleDTO> findSectorUserAuthoritiesListBySectorAssociationId(Long sectorId) {
        return ccaAuthorityRepository.findSectorUserAuthoritiesListBySectorAssociationId(sectorId);
    }

	public boolean existsAuthorityNotForSectorAssociation(String userId) {
		return authorityRepository.findByUserId(userId).stream().anyMatch(
				auth -> !(auth instanceof CcaAuthority) || (((CcaAuthority) auth).getSectorAssociationId() == null));
	}

    public boolean existsNonPendingAuthorityForSectorAssociation(String userId, Long sectorAssociationId) {
        Optional<CcaAuthority> ccaAuthorityOpt = ccaAuthorityRepository.findByUserIdAndSectorAssociationId(userId, sectorAssociationId);
        return ccaAuthorityOpt.isPresent() && ccaAuthorityOpt.get().getStatus() != AuthorityStatus.PENDING;
    }
}
