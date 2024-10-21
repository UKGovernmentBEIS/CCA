package uk.gov.cca.api.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;

import java.util.Optional;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;

@Service
@RequiredArgsConstructor
public class SectorAssociationInfoService {

    private final SubsectorAssociationService subsectorAssociationService;
    private final SubsectorAssociationSchemeService subsectorAssociationSchemeService;
    private final SectorAssociationSchemeService sectorAssociationSchemeService;
    private final SectorAssociationQueryService sectorAssociationQueryService;

    public SectorAssociationMeasurementInfoDTO getSectorAssociationMeasurementInfo(Long sectorAssociationId, Long subsectorAssociationId) {
     			
        return subsectorAssociationId != null
                ? getMeasurementInfoDTOFromSubsector(subsectorAssociationId)
                : getMeasurementInfoDTOFromSector(sectorAssociationId);
    }

    public SectorAssociationContactDTO getSectorAssociationContact(Long sectorId) {
        return sectorAssociationQueryService
                .getSectorAssociationById(sectorId)
                .getSectorAssociationContact();
    }

    private SectorAssociationMeasurementInfoDTO getMeasurementInfoDTOFromSubsector(Long subsectorAssociationId) {
        final SubsectorAssociationSchemeDTO subsectorAssociationScheme =
                subsectorAssociationSchemeService.getSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId);
        
        final String subsectorAssociationName = subsectorAssociationService.getSubsectorById(subsectorAssociationId).getName();
        
		return SectorAssociationMeasurementInfoDTO.builder()
				.subsectorAssociationName(subsectorAssociationName)
				.measurementUnit(subsectorAssociationScheme.getTargetSet().getEnergyOrCarbonUnit())
				.throughputUnit(subsectorAssociationScheme.getTargetSet().getThroughputUnit())
				.build();
    }

    private SectorAssociationMeasurementInfoDTO getMeasurementInfoDTOFromSector(Long sectorAssociationId) {
        final SectorAssociationSchemeDTO sectorAssociationScheme =
                sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorAssociationId);

        String measurementUnit =  Optional.ofNullable(sectorAssociationScheme.getTargetSet())
                .map(TargetSetDTO::getEnergyOrCarbonUnit)
                .orElse(null);
        
        String throughputUnit =  Optional.ofNullable(sectorAssociationScheme.getTargetSet())
                .map(TargetSetDTO::getThroughputUnit)
                .orElse(null);
        
		return SectorAssociationMeasurementInfoDTO.builder()
				.measurementUnit(measurementUnit)
				.throughputUnit(throughputUnit)
				.build();
        
    }

}
