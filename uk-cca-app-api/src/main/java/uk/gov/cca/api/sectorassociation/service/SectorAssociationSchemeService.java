package uk.gov.cca.api.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationSchemeMapper;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class SectorAssociationSchemeService {

    private final SectorAssociationSchemeRepository sectorAssociationSchemeRepository;
    private final SectorAssociationSchemeMapper sectorAssociationSchemeMapper;

    @Transactional(readOnly = true)
    public SectorAssociationSchemeDTO getSectorAssociationSchemeBySectorAssociationId(long id) {
        SectorAssociationScheme sectorAssociationScheme = sectorAssociationSchemeRepository.findSectorAssociationSchemeBySectorAssociationId(id)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return sectorAssociationSchemeMapper.sectorAssociationSchemeToDTO(sectorAssociationScheme);
    }

    @Transactional(readOnly = true)
    public List<Long> getSubsectorAssociationIdsBySectorAssociationId(Long sectorAssociationId) {
        return sectorAssociationSchemeRepository.findSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId);
    }

    @Transactional(readOnly = true)
    public List<SubsectorAssociationInfoDTO> getSubsectorAssociationInfoDTOBySectorAssociationId(Long sectorAssociationId) {
        return sectorAssociationSchemeRepository.findSubsectorAssociationsBySectorAssociationId(sectorAssociationId);
    }
}
