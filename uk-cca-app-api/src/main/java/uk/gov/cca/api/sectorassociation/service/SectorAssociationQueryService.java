package uk.gov.cca.api.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.authorization.ccaauth.core.service.AppUserService;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SectorAssociationAuthorityInfoProvider;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationMapper;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationCustomRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SectorAssociationQueryService implements SectorAssociationAuthorityInfoProvider {

    private final SectorAssociationRepository sectorAssociationRepository;
    private final SectorAssociationCustomRepository sectorAssociationCustomRepository;
    private final SectorAssociationMapper sectorAssociationMapper;
    private final AppUserService appUserService;

    @Override
    public CompetentAuthorityEnum getSectorAssociationCa(Long sectorAssociationId) {
        return getById(sectorAssociationId).getCompetentAuthority();
    }

    @Transactional(readOnly = true)
    public SectorAssociationDTO getSectorAssociationById(Long id) {
        SectorAssociation sectorAssociation = getById(id);
        return sectorAssociationMapper.toSectorAssociationDTO(sectorAssociation);
    }

    public String getSectorAssociationAcronymById(Long sectorAssociationId) {
        return sectorAssociationRepository.findSectorAssociationAcronymById(sectorAssociationId);
    }

    public SectorAssociation exclusiveLockSectorAssociation(final Long sectorAssociationId) {
        return sectorAssociationCustomRepository.findByIdForUpdate(sectorAssociationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public List<SectorAssociationInfoDTO> getSectorAssociations(AppUser appUser) {
        return switch (appUser.getRoleType()) {
            case REGULATOR -> getRegulatorSectorAssociations(appUser);
            case SECTOR_USER -> getSectorUserSectorAssociations(appUser);
            default -> Collections.emptyList();
        };
    }
    
    public String getSectorAssociationName(Long sectorAssociationId) {
        return getById(sectorAssociationId).getName();
    }

    public String getSectorAssociationIdentifier(Long sectorAssociationId) {
        final SectorAssociation sectorAssociation = getById(sectorAssociationId);
        return sectorAssociation.getAcronym() + " - " + sectorAssociation.getName();
    }
    
    public Set<Long> getUserSectorAssociations(AppUser appUser) {
		return appUserService.getUserSectorAssociations(appUser);
	}

    public NoticeRecipientDTO getSectorAssociationNoticeRecipientById(Long sectorAssociationId) {
        return sectorAssociationRepository.findSectorAssociationNoticeRecipientById(sectorAssociationId);
    }

    @Transactional(readOnly = true)
    public String getSectorAssociationFacilitatorUserId(Long id) {
        return getById(id).getFacilitatorUserId();
    }
    
    public Optional<Long> getSectorAssociationIdByAcronym(String acronym) {
        return sectorAssociationRepository.findSectorAssociationIdByAcronym(acronym);
    }

    private List<SectorAssociationInfoDTO> getRegulatorSectorAssociations(AppUser appUser) {
        final CompetentAuthorityEnum competentAuthority = appUser.getCompetentAuthority();
        return sectorAssociationRepository.findSectorAssociations(competentAuthority);
    }

    private List<SectorAssociationInfoDTO> getSectorUserSectorAssociations(AppUser appUser) {
        final Set<Long> sectorIds = getUserSectorAssociations(appUser);
        return sectorAssociationRepository.findSectorAssociations(sectorIds);
    }
	
    SectorAssociation getById(Long id) {
        return sectorAssociationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
