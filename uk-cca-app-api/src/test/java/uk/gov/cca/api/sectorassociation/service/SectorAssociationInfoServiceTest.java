package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class SectorAssociationInfoServiceTest {

    @InjectMocks
    private SectorAssociationInfoService service;

    @Mock
    private SubsectorAssociationSchemeService subsectorAssociationSchemeService;

    @Mock
    private SubsectorAssociationService subsectorAssociationService;

    @Mock
    private SectorAssociationSchemeService sectorAssociationSchemeService;


    @Test
    void getSectorAssociationMeasurementInfo_from_sector() {
        final Long sectorAssociationId = 1L;

        Map<SchemeVersion, SectorAssociationSchemeDTO> sectorAssociationSchemeMap = 
        		Map.of(SchemeVersion.CCA_2, SectorAssociationSchemeDTO.builder()
        				.targetSet(TargetSetDTO.builder().build())
        				.build(), 
        				SchemeVersion.CCA_3, SectorAssociationSchemeDTO.builder()
        				.targetSet(TargetSetDTO.builder().build())
        				.build()) ;

        when(sectorAssociationSchemeService.getSectorAssociationSchemesMap(sectorAssociationId)).thenReturn(sectorAssociationSchemeMap);

        // invoke
        final SectorAssociationMeasurementInfoDTO result = service.getSectorAssociationMeasurementInfo(sectorAssociationId, null);

        // verify
        verify(sectorAssociationSchemeService, times(1)).getSectorAssociationSchemesMap(sectorAssociationId);
        
        assertThat(result.getSchemeDataMap().get(SchemeVersion.CCA_2).getSectorMeasurementType()).isNull();
        assertThat(result.getSchemeDataMap().get(SchemeVersion.CCA_2).getSectorThroughputUnit()).isNull();
        assertThat(result.getSchemeDataMap().get(SchemeVersion.CCA_3).getSectorMeasurementType()).isNull();
        assertThat(result.getSchemeDataMap().get(SchemeVersion.CCA_3).getSectorThroughputUnit()).isNull();
    }
    
    @Test
    void getSectorAssociationMeasurementInfo_from_subsector() {

        final Long subsectorAssociationId = 1L;
        final Long sectorAssociationId = 1L;
        final String subsectorAssociationName = "SUBSECTOR";
        final String unit = "kWh";

        Map<SchemeVersion, SubsectorAssociationSchemeDTO> subsectorAssociationSchemeMap = 
        		Map.of(SchemeVersion.CCA_2, SubsectorAssociationSchemeDTO.builder()
        				.targetSet(TargetSetDTO.builder()
        						.energyOrCarbonUnit(unit)
        						.build())
        				.build()) ;

        when(subsectorAssociationService.getSubsectorById(subsectorAssociationId)).thenReturn(SubsectorAssociationDTO.builder().name(subsectorAssociationName).build());
        when(subsectorAssociationSchemeService.getSubsectorAssociationSchemesMap(subsectorAssociationId)).thenReturn(subsectorAssociationSchemeMap);

        // invoke
        final SectorAssociationMeasurementInfoDTO result = service.getSectorAssociationMeasurementInfo(sectorAssociationId, subsectorAssociationId);

        // verify
        verify(subsectorAssociationSchemeService, times(1)).getSubsectorAssociationSchemesMap(subsectorAssociationId);
        verify(sectorAssociationSchemeService, never()).getSectorAssociationSchemesMap(sectorAssociationId);
        
        assertThat(result.getSubsectorAssociationName()).isEqualTo(subsectorAssociationName);
        assertThat(result.getSchemeDataMap().get(SchemeVersion.CCA_2).getSectorMeasurementType().getUnit()).isEqualTo(unit);
    }

}
