package uk.gov.cca.api.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemesDTO;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationSchemeMapper;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.netz.api.common.exception.BusinessException;


import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SectorAssociationSchemeService {

    private final SectorAssociationSchemeRepository sectorAssociationSchemeRepository;
    private final SectorAssociationRepository sectorAssociationRepository;
    private final SectorAssociationSchemeMapper sectorAssociationSchemeMapper;

    @Transactional(readOnly = true)
    public SectorAssociationSchemesDTO getSectorAssociationSchemesBySectorAssociationId(long sectorId) {
    	SectorAssociation sectorAssociation = sectorAssociationRepository.findById(sectorId)
        		.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    	
    	Map<SchemeVersion, SectorAssociationSchemeDTO> sectorAssociationSchemeMap = getSectorAssociationSchemesMap(sectorId);

        return sectorAssociationSchemeMapper.toSectorAssociationSchemesDTO(sectorAssociationSchemeMap, sectorAssociation.getSubsectorAssociations());
    }

    @Transactional(readOnly = true)
	public Map<SchemeVersion, SectorAssociationSchemeDTO> getSectorAssociationSchemesMap(long sectorId) {
    	return sectorAssociationSchemeRepository.findSectorAssociationSchemesBySectorAssociationId(sectorId)
    			.stream()
    			.collect(Collectors.toMap(SectorAssociationScheme::getSchemeVersion, sectorAssociationSchemeMapper::toSectorAssociationSchemeDTO));
	}
    
    @Transactional(readOnly = true)
    public SectorAssociationSchemeDTO getSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(long id, SchemeVersion version) {
        SectorAssociationScheme sectorAssociationScheme = sectorAssociationSchemeRepository
        		.findSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(id, version)
        		.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return sectorAssociationSchemeMapper.toSectorAssociationSchemeDTO(sectorAssociationScheme);
    }

}
