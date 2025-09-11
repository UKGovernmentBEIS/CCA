package uk.gov.cca.api.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
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
        final Map<SchemeVersion, SubsectorAssociationSchemeDTO> subsectorAssociationSchemeMap =
                subsectorAssociationSchemeService.getSubsectorAssociationSchemesMap(subsectorAssociationId);
        
        final String subsectorAssociationName = subsectorAssociationService.getSubsectorById(subsectorAssociationId).getName();
        
		return SectorAssociationMeasurementInfoDTO.builder()
				.subsectorAssociationName(subsectorAssociationName)
				.schemeDataMap(getSubsectorSchemeDataMap(subsectorAssociationSchemeMap))
				.build();
    }

    private SectorAssociationMeasurementInfoDTO getMeasurementInfoDTOFromSector(Long sectorAssociationId) {
        final Map<SchemeVersion, SectorAssociationSchemeDTO> sectorAssociationSchemeMap =
                sectorAssociationSchemeService.getSectorAssociationSchemesMap(sectorAssociationId);

		return SectorAssociationMeasurementInfoDTO.builder()
				.schemeDataMap(getSectorSchemeDataMap(sectorAssociationSchemeMap))
				.build();     
    }

	private Map<SchemeVersion, SchemeData> getSectorSchemeDataMap(
			Map<SchemeVersion, SectorAssociationSchemeDTO> sectorAssociationSchemeMap) {
		return sectorAssociationSchemeMap.entrySet().stream()
			.collect(Collectors.toMap(Entry::getKey, entry -> SchemeData.builder()
					.sectorMeasurementType(MeasurementType.getMeasurementTypeByUnit(getMeasurementUnit(entry.getValue().getTargetSet())))
					.sectorThroughputUnit(getThroughputUnit(entry.getValue().getTargetSet()))
					.build()));
	}

	private Map<SchemeVersion, SchemeData> getSubsectorSchemeDataMap(
			Map<SchemeVersion, SubsectorAssociationSchemeDTO> subsectorAssociationSchemeMap) {
		return subsectorAssociationSchemeMap.entrySet().stream()
			.collect(Collectors.toMap(Entry::getKey, entry -> SchemeData.builder()
					.sectorMeasurementType(MeasurementType.getMeasurementTypeByUnit(getMeasurementUnit(entry.getValue().getTargetSet())))
					.sectorThroughputUnit(getThroughputUnit(entry.getValue().getTargetSet()))
					.build()));
	}
	
	private String getMeasurementUnit(final TargetSetDTO targetSetDTO) {
		return Optional.ofNullable(targetSetDTO)
                .map(TargetSetDTO::getEnergyOrCarbonUnit)
                .orElse(null);
	}
	
	private String getThroughputUnit(final TargetSetDTO targetSetDTO) {
		return Optional.ofNullable(targetSetDTO)
                .map(TargetSetDTO::getThroughputUnit)
                .orElse(null);
	}

}
