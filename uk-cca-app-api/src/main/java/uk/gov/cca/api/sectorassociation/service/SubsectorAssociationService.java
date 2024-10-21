package uk.gov.cca.api.sectorassociation.service;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationRepository;
import uk.gov.netz.api.common.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class SubsectorAssociationService {

    private final SubsectorAssociationRepository subsectorAssociationRepository;

    public SubsectorAssociationDTO getSubsectorById(Long subsectorId) {
        return subsectorAssociationRepository.findById(subsectorId)
                .map(subsectorAssociation -> new SubsectorAssociationDTO(subsectorAssociation.getName()))
                .orElse(new SubsectorAssociationDTO());
    }
    
    public Long getIdByName(String subsectorName) {
        SubsectorAssociation subsectorAssociation = subsectorAssociationRepository.findByName(subsectorName)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        return subsectorAssociation.getId();
    }
}
