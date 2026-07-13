package uk.gov.cca.api.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SectorAssociationSchemeAuthorityInfoProvider;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeInfo;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemesDTO;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationSchemeMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class SectorAssociationSchemeService implements SectorAssociationSchemeAuthorityInfoProvider {

    private final SectorAssociationSchemeRepository sectorAssociationSchemeRepository;
    private final SectorAssociationRepository sectorAssociationRepository;
    private final SectorAssociationSchemeMapper sectorAssociationSchemeMapper;
    private final SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;

    @Transactional(readOnly = true)
    public SectorAssociationSchemesDTO getSectorAssociationSchemesBySectorAssociationId(long sectorId, AppUser appUser) {
        SectorAssociation sectorAssociation = sectorAssociationRepository.findById(sectorId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        boolean hasEditPermission = sectorAssociationAuthorizationResourceService.hasUserScopeToSectorAssociationSchemesBySectorId(appUser, CcaScope.EDIT_SECTOR_ADVANCED_DETAILS, sectorId);

        Map<SchemeVersion, SectorAssociationSchemeDTO> sectorAssociationSchemesViewMap = getSectorAssociationSchemesViewMap(sectorId, hasEditPermission);

        return sectorAssociationSchemeMapper.toSectorAssociationSchemesDTO(sectorAssociationSchemesViewMap, sectorAssociation.getSubsectorAssociations());
    }

    @Transactional(readOnly = true)
	public Map<SchemeVersion, SectorAssociationSchemeInfo> getSectorAssociationSchemesMap(long sectorId) {
    	return sectorAssociationSchemeRepository.findSectorAssociationSchemesBySectorAssociationId(sectorId)
    			.stream()
    			.collect(Collectors.toMap(SectorAssociationScheme::getSchemeVersion, sectorAssociationSchemeMapper::toSectorAssociationSchemeInfo));
	}
    
    @Transactional(readOnly = true)
    public SectorAssociationSchemeInfo getSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(long id, SchemeVersion version) {
        SectorAssociationScheme sectorAssociationScheme = sectorAssociationSchemeRepository
        		.findSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(id, version)
        		.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return sectorAssociationSchemeMapper.toSectorAssociationSchemeInfo(sectorAssociationScheme);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSectorAssociationIdBySchemeId(Long schemeId) {
        return sectorAssociationSchemeRepository.findById(schemeId)
                .map(sectorAssociationScheme -> sectorAssociationScheme.getSectorAssociation().getId())
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    private Map<SchemeVersion, SectorAssociationSchemeDTO> getSectorAssociationSchemesViewMap(long sectorId, boolean hasEditPermission) {
        return sectorAssociationSchemeRepository.findSectorAssociationSchemesBySectorAssociationId(sectorId)
                .stream()
                .collect(Collectors.toMap(SectorAssociationScheme::getSchemeVersion, scheme -> sectorAssociationSchemeMapper.toSectorAssociationSchemeDTO(scheme, hasEditPermission)));
    }
}
