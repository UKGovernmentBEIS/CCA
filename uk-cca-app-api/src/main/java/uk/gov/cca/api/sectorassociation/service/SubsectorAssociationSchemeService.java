package uk.gov.cca.api.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemesDTO;
import uk.gov.cca.api.sectorassociation.transform.SubsectorAssociationSchemeMapper;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationSchemeRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubsectorAssociationSchemeService {

    private final SubsectorAssociationSchemeRepository subsectorAssociationSchemeRepository;
    private final SubsectorAssociationSchemeMapper subsectorAssociationSchemeMapper;
    private final SubsectorAssociationService subsectorAssociationService;

    @Transactional(readOnly = true)
    public SubsectorAssociationSchemesDTO getSubsectorAssociationSchemesBySubsectorAssociationId(Long sectorId, Long subsectorId) {
    	
    	validateSubsectorBelongsToSector(sectorId, subsectorId);
    	
    	String subsectorName = subsectorAssociationService.getSubsectorById(subsectorId).getName();
    	Map<SchemeVersion, SubsectorAssociationSchemeDTO> subsectorAssociationSchemeMap = getSubsectorAssociationSchemesMap(subsectorId);
    	
    	return subsectorAssociationSchemeMapper.toSubsectorAssociationSchemesDTO(subsectorName, subsectorAssociationSchemeMap);
    }

    @Transactional(readOnly = true)
	public Map<SchemeVersion, SubsectorAssociationSchemeDTO> getSubsectorAssociationSchemesMap(Long subsectorId) {
		return subsectorAssociationSchemeRepository.findSubsectorAssociationSchemeBySubsectorAssociationId(subsectorId)
				.stream()
    			.collect(Collectors.toMap(SubsectorAssociationScheme::getSchemeVersion, subsectorAssociationSchemeMapper::toSubsectorAssociationSchemeDTO));
	}
    
    private void validateSubsectorBelongsToSector(Long sectorId, Long subsectorId) {
    	List<Long> subsectorAssociationIds = subsectorAssociationService
                .getSubsectorAssociationIdsBySectorAssociationId(sectorId);
    	
    	if(!subsectorAssociationIds.contains(subsectorId)) {
            throw new BusinessException(CcaErrorCode.SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION);
        }
	}
}
