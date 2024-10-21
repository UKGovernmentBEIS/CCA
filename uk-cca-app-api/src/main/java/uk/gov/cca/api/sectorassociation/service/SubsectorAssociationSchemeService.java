package uk.gov.cca.api.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.transform.SubsectorAssociationSchemeMapper;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationSchemeRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SubsectorAssociationSchemeService {

    private final SubsectorAssociationSchemeRepository subsectorAssociationSchemeRepository;
    private final SubsectorAssociationSchemeMapper subsectorAssociationSchemeMapper;

    @Transactional(readOnly = true)
    public SubsectorAssociationSchemeDTO getSubsectorAssociationSchemeBySubsectorAssociationSchemeId(Long subsectorSchemeId) {
        SubsectorAssociationScheme subsectorAssociationScheme = subsectorAssociationSchemeRepository.findSubsectorAssociationSchemesById(subsectorSchemeId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return subsectorAssociationSchemeMapper.subsectorAssociationSchemeToDTO(subsectorAssociationScheme);
    }

    @Transactional(readOnly = true)
    public SubsectorAssociationSchemeDTO getSubsectorAssociationSchemeBySubsectorAssociationId(Long subsectorAssociationId) {
        SubsectorAssociationScheme subsectorAssociationScheme = subsectorAssociationSchemeRepository.findSubsectorAssociationSchemesBySubsectorAssociationId(subsectorAssociationId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return subsectorAssociationSchemeMapper.subsectorAssociationSchemeToDTO(subsectorAssociationScheme);
    }
}
