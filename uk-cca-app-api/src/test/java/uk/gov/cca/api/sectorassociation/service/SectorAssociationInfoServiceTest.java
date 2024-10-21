package uk.gov.cca.api.sectorassociation.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SectorAssociationInfoServiceTest {

    @InjectMocks
    private SectorAssociationInfoService service;

    @Mock
    private SubsectorAssociationSchemeService subsectorAssociationSchemeService;

    @Mock
    private SubsectorAssociationService subsectorAssociationService;

    @Mock
    private SectorAssociationSchemeService sectorAssociationSchemeService;


    @Test
    void getSectorAssociationMeasurementInfo() {

        final Long subsectorAssociationId = 1L;
        final Long sectorAssociationId = 1L;
        final String subsectorAssociationName = "SUBSECTOR";
        final String unit = "kWh";

        final SubsectorAssociationSchemeDTO subsectorAssociationScheme = SubsectorAssociationSchemeDTO.builder()
                .targetSet(TargetSetDTO.builder().energyOrCarbonUnit(unit).build())
                .build();

        when(subsectorAssociationService.getSubsectorById(subsectorAssociationId)).thenReturn(SubsectorAssociationDTO.builder().name(subsectorAssociationName).build());
        when(subsectorAssociationSchemeService.getSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId)).thenReturn(subsectorAssociationScheme);

        // invoke
        final SectorAssociationMeasurementInfoDTO result = service.getSectorAssociationMeasurementInfo(sectorAssociationId, subsectorAssociationId);

        // verify
        verify(subsectorAssociationSchemeService, times(1)).getSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId);
        verify(sectorAssociationSchemeService, never()).getSectorAssociationSchemeBySectorAssociationId(sectorAssociationId);

        assertThat(result.getSubsectorAssociationName()).isEqualTo(subsectorAssociationName);
        assertThat(result.getMeasurementUnit()).isEqualTo(unit);

    }

}
