package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.*;
import uk.gov.cca.api.sectorassociation.transform.SubsectorAssociationSchemeMapper;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationSchemeRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubsectorAssociationSchemeServiceTest {

    @InjectMocks
    private SubsectorAssociationSchemeService subsectorAssociationSchemeService;

    @Mock
    private SubsectorAssociationSchemeRepository subsectorAssociationSchemeRepository;
    @Mock
    private SubsectorAssociationSchemeMapper subsectorAssociationSchemeMapper;

    @Test
    void getSectorAssociationSchemeWithoutTargetSet() {
        final Long sectorAssociationSchemeId = 1L;
        final SubsectorAssociationSchemeDTO subsectorAssociationSchemeDTO = createSubsectorAssociationSchemeDTO();

        SubsectorAssociationScheme subsectorAssociationScheme = Mockito.mock(SubsectorAssociationScheme.class);

        when(subsectorAssociationSchemeRepository.findSubsectorAssociationSchemesById(sectorAssociationSchemeId)).thenReturn(Optional.of(subsectorAssociationScheme));
        when(subsectorAssociationSchemeMapper.subsectorAssociationSchemeToDTO(subsectorAssociationScheme)).thenReturn(subsectorAssociationSchemeDTO);

        SubsectorAssociationSchemeDTO result = subsectorAssociationSchemeService.getSubsectorAssociationSchemeBySubsectorAssociationSchemeId(sectorAssociationSchemeId);

        assertThat(result).isEqualTo(subsectorAssociationSchemeDTO);
        verify(subsectorAssociationSchemeRepository, times(1)).findSubsectorAssociationSchemesById(sectorAssociationSchemeId);
        verify(subsectorAssociationSchemeMapper, times(1)).subsectorAssociationSchemeToDTO(subsectorAssociationScheme);
    }

    @Test
    void getSectorAssociationSchemeWithSubSectorAssociationId() {
        final Long sectorAssociationSchemeId = 1L;
        final SubsectorAssociationSchemeDTO subsectorAssociationSchemeDTO = createSubsectorAssociationSchemeDTO();

        SubsectorAssociationScheme subsectorAssociationScheme = Mockito.mock(SubsectorAssociationScheme.class);

        final Long subsectorAssociationId = 1L;

        when(subsectorAssociationSchemeRepository.findSubsectorAssociationSchemesBySubsectorAssociationId(subsectorAssociationId)).thenReturn(Optional.of(subsectorAssociationScheme));
        when(subsectorAssociationSchemeMapper.subsectorAssociationSchemeToDTO(subsectorAssociationScheme)).thenReturn(subsectorAssociationSchemeDTO);

        SubsectorAssociationSchemeDTO result = subsectorAssociationSchemeService.getSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId);

        assertThat(result).isEqualTo(subsectorAssociationSchemeDTO);
        verify(subsectorAssociationSchemeRepository, times(1)).findSubsectorAssociationSchemesBySubsectorAssociationId(subsectorAssociationId);
        verify(subsectorAssociationSchemeMapper, times(1)).subsectorAssociationSchemeToDTO(subsectorAssociationScheme);
    }

    private SubsectorAssociationSchemeDTO createSubsectorAssociationSchemeDTO() {
        return SubsectorAssociationSchemeDTO.builder()
                .subsectorAssociation(SubsectorAssociationDTO.builder()
                        .name("name")
                        .build())
                .targetSet(TargetSetDTO.builder()
                        .targetCurrencyType("Novem")
                        .energyOrCarbonUnit("kWh")
                        .targetCommitments(Collections.singletonList(TargetCommitmentDTO.builder()
                                .targetImprovement(BigDecimal.valueOf(19.000))
                                .targetPeriod("2013-2014")
                                .build()))
                        .build())
                .build();
    }
}