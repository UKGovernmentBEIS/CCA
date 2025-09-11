package uk.gov.cca.api.sectorassociation.service;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationRepository;
import uk.gov.netz.api.common.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class SubsectorAssociationService {

    private final SubsectorAssociationRepository subsectorAssociationRepository;

    public SubsectorAssociationDTO getSubsectorById(Long subsectorId) {
        return subsectorAssociationRepository.findById(subsectorId)
                .map(subsectorAssociation -> new SubsectorAssociationDTO(subsectorAssociation.getName()))
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
    
    public Long getIdByName(String subsectorName) {
        SubsectorAssociation subsectorAssociation = subsectorAssociationRepository.findByName(subsectorName)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        return subsectorAssociation.getId();
    }
    
    @Transactional(readOnly = true)
    public List<Long> getSubsectorAssociationIdsBySectorAssociationId(Long sectorAssociationId) {
        return getSubsectorAssociationsBySectorAssociationId(sectorAssociationId).stream()
        		.map(SubsectorAssociation::getId)
        		.collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubsectorAssociationInfoDTO> getSubsectorAssociationInfoDTOBySectorAssociationId(Long sectorAssociationId) {
        return getSubsectorAssociationsBySectorAssociationId(sectorAssociationId).stream()
        		.map(subsectorAssociation -> SubsectorAssociationInfoDTO.builder()
        				.id(subsectorAssociation.getId())
        				.name(subsectorAssociation.getName())
        				.build())
        		.collect(Collectors.toList());
    }
    
    private List<SubsectorAssociation> getSubsectorAssociationsBySectorAssociationId(Long sectorAssociationId) {
    	return subsectorAssociationRepository.findAllBySectorAssociationId(sectorAssociationId);
    }
}
