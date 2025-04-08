package uk.gov.cca.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationInfoService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeService;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.core.transform.SectorReferenceDataMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SectorReferenceDetailsService {

    private final SectorAssociationQueryService sectorAssociationQueryService;
    private final SectorAssociationInfoService sectorAssociationInfoService;
    private final SectorAssociationSchemeService sectorAssociationSchemeService;
    private final SectorReferenceDataMapper sectorReferenceDataMapper;

    public SectorAssociationDTO getSectorAssociationDetails(Long sectorAssociationId) {
        return sectorAssociationQueryService.getSectorAssociationById(sectorAssociationId);
    }

    public SectorAssociationDetails getSectorAssociationMeasurementDetails(Long sectorAssociationId, Long subSectorAssociationId) {
        return sectorReferenceDataMapper.toSectorAssociationDetails(
                sectorAssociationInfoService.getSectorAssociationMeasurementInfo(sectorAssociationId, subSectorAssociationId));
    }

    public SectorAssociationSchemeDTO getSectorAssociationSchemeBySectorAssociationId(Long sectorAssociationId) {
        return sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorAssociationId);
    }

    public SectorAssociationInfo getSectorAssociationInfo(Long sectorAssociationId) {
        return sectorReferenceDataMapper.toSectorAssociationInfo(
                sectorAssociationQueryService.getSectorAssociationInfoNameDTO(sectorAssociationId));
    }

    public List<SectorAssociationInfo> getSectorAssociationsInfo(List<Long> sectorAssociationIds) {
        return sectorAssociationQueryService.getSectorAssociationsInfoNameDTO(sectorAssociationIds).stream()
                .map(sectorReferenceDataMapper::toSectorAssociationInfo)
                .collect(Collectors.toList());
    }
}
