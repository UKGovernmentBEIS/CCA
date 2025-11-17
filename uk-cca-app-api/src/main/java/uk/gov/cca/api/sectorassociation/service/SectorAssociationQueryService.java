package uk.gov.cca.api.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.core.service.AppUserService;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SectorAssociationAuthorityInfoProvider;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoNameDTO;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationCustomRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    
    public List<SectorAssociationDetailsDTO> getSectorsByIds(List<Long> sectorAssociationIds) {
        return sectorAssociationRepository.findAllByIdIn(sectorAssociationIds)
            .stream()
            .map(sectorAssociationMapper::toSectorAssociationDetailsDTO)
            .collect(Collectors.toList());
    }

    public String getSectorAssociationAcronymById(Long sectorAssociationId) {
        return sectorAssociationRepository.findSectorAssociationAcronymById(sectorAssociationId);
    }

    public SectorAssociation exclusiveLockSectorAssociation(final Long sectorAssociationId) {
        return sectorAssociationCustomRepository.findByIdForUpdate(sectorAssociationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public String getSectorAssociationName(Long sectorAssociationId) {
        return getById(sectorAssociationId).getName();
    }

    public String getSectorAssociationAcronymAndName(Long sectorAssociationId) {
        final SectorAssociation sectorAssociation = getById(sectorAssociationId);
        return sectorAssociation.getAcronym() + " - " + sectorAssociation.getName();
    }
    
    public Set<Long> getUserSectorAssociationIds(AppUser appUser) {
		return appUserService.getUserSectorAssociations(appUser);
	}

    @Transactional(readOnly = true)
    public String getSectorAssociationFacilitatorUserId(Long id) {
        return getById(id).getFacilitatorUserId();
    }
    
    public Optional<Long> getSectorAssociationIdByAcronym(String acronym) {
        return sectorAssociationRepository.findSectorAssociationIdByAcronym(acronym);
    }

    public List<SectorAssociationInfoDTO> getRegulatorSectorAssociations(AppUser appUser) {
        final CompetentAuthorityEnum competentAuthority = appUser.getCompetentAuthority();
        return sectorAssociationRepository.findSectorAssociations(competentAuthority);
    }

    public List<SectorAssociationInfoDTO> getSectorUserSectorAssociations(AppUser appUser) {
        final Set<Long> sectorIds = getUserSectorAssociationIds(appUser);
        return getUserSectorAssociations(sectorIds);
    }

    public List<SectorAssociationInfoDTO> getUserSectorAssociations(Set<Long> sectorAssociationsIds) {

        return sectorAssociationRepository.findSectorAssociations(sectorAssociationsIds);
    }

    @Transactional(readOnly = true)
    public SectorAssociationInfoNameDTO getSectorAssociationInfoNameDTO(Long id) {
        return sectorAssociationMapper.toSectorAssociationInfoNameDTO(getById(id));
    }
    
    @Transactional(readOnly = true)
    public List<SectorAssociationInfoNameDTO> getSectorAssociationsInfoNameDTO(List<Long> ids) {
    	return getByIds(ids).stream()
    			.map(sectorAssociationMapper::toSectorAssociationInfoNameDTO)
    			.collect(Collectors.toList());
    }

	public Optional<SectorAssociation> findSectorAssociationByAcronymAndScheme(String acronym, SchemeVersion version) {
		return sectorAssociationRepository.findByAcronymAndSectorAssociationSchemesSchemeVersionIs(acronym, version);
	}

    SectorAssociation getById(Long id) {
        return sectorAssociationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
    
    List<SectorAssociation> getByIds(List<Long> ids) {
        return sectorAssociationRepository.findAllByIdIn(ids);
    }
}
