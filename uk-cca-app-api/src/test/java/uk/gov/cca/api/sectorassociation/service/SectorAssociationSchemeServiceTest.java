package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDocumentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationSchemeMapper;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorAssociationSchemeServiceTest {

    @InjectMocks
    private SectorAssociationSchemeService sectorAssociationSchemeService;

    @Mock
    private SectorAssociationSchemeRepository sectorAssociationSchemeRepository;
    @Mock
    private SectorAssociationSchemeMapper sectorAssociationSchemeMapper;

    @Test
    void getSectorAssociationSchemeWithoutTargetSet() {
        final Long sectorAssociationId = 1L;
        final SectorAssociationSchemeDTO sectorAssociationSchemeDTO = createSectorAssociationSchemeDTOWithoutTargetSet();

        SectorAssociationScheme sectorAssociationScheme = Mockito.mock(SectorAssociationScheme.class);

        when(sectorAssociationSchemeRepository.findSectorAssociationSchemeBySectorAssociationId(sectorAssociationId)).thenReturn(Optional.of(sectorAssociationScheme));
        when(sectorAssociationSchemeMapper.sectorAssociationSchemeToDTO(sectorAssociationScheme)).thenReturn(sectorAssociationSchemeDTO);

        SectorAssociationSchemeDTO result = sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorAssociationId);

        assertThat(result).isEqualTo(sectorAssociationSchemeDTO);
        verify(sectorAssociationSchemeRepository, times(1)).findSectorAssociationSchemeBySectorAssociationId(sectorAssociationId);
        verify(sectorAssociationSchemeMapper, times(1)).sectorAssociationSchemeToDTO(sectorAssociationScheme);
    }

    @Test
    void getSectorAssociationSchemeWithoutSubsectors() {
        final Long sectorAssociationId = 1L;
        final SectorAssociationSchemeDTO sectorAssociationSchemeDTO = createSectorAssociationSchemeDTOWithoutSubsectors();

        SectorAssociationScheme sectorAssociationScheme = Mockito.mock(SectorAssociationScheme.class);

        when(sectorAssociationSchemeRepository.findSectorAssociationSchemeBySectorAssociationId(sectorAssociationId)).thenReturn(Optional.of(sectorAssociationScheme));
        when(sectorAssociationSchemeMapper.sectorAssociationSchemeToDTO(sectorAssociationScheme)).thenReturn(sectorAssociationSchemeDTO);

        SectorAssociationSchemeDTO result = sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorAssociationId);

        assertThat(result).isEqualTo(sectorAssociationSchemeDTO);
        verify(sectorAssociationSchemeRepository, times(1)).findSectorAssociationSchemeBySectorAssociationId(sectorAssociationId);
        verify(sectorAssociationSchemeMapper, times(1)).sectorAssociationSchemeToDTO(sectorAssociationScheme);
    }

    @Test
    void getSubsectorAssociationInfoDTOBySectorAssociationId() {
        final Long sectorAssociationId = 1L;

        SubsectorAssociationInfoDTO subsectorAssociationInfoDTO = Mockito.mock(SubsectorAssociationInfoDTO.class);

        when(sectorAssociationSchemeRepository.findSubsectorAssociationsBySectorAssociationId(sectorAssociationId))
                .thenReturn(Collections.singletonList(subsectorAssociationInfoDTO));

        List<SubsectorAssociationInfoDTO> result = sectorAssociationSchemeService.getSubsectorAssociationInfoDTOBySectorAssociationId(sectorAssociationId);

        assertThat(result).isNotEmpty();
        verify(sectorAssociationSchemeRepository, times(1)).findSubsectorAssociationsBySectorAssociationId(sectorAssociationId);
    }

    private static SectorAssociationSchemeDTO createSectorAssociationSchemeDTOWithoutTargetSet() {
        return SectorAssociationSchemeDTO.builder()
                .umbrellaAgreement(SectorAssociationSchemeDocumentDTO.builder()
                        .id(1)
                        .uuid("test")
                        .fileName("umbrellaAgreement")
                        .fileType(".pdf")
                        .fileSize(1)
                        .build())
                .subsectorAssociationSchemes(Collections.singletonList(SubsectorAssociationSchemeInfoDTO.builder()
                        .subsectorAssociation(SubsectorAssociationDTO.builder()
                                .name("name")
                                .build())
                        .build()))
                .sectorDefinition("This is the sector definition")
                .umaDate(LocalDate.now())
                .build();
    }

    private static SectorAssociationSchemeDTO createSectorAssociationSchemeDTOWithoutSubsectors() {
        return SectorAssociationSchemeDTO.builder()
                .umbrellaAgreement(SectorAssociationSchemeDocumentDTO.builder()
                        .id(1)
                        .uuid("test")
                        .fileName("umbrellaAgreement")
                        .fileType(".pdf")
                        .fileSize(1)
                        .build())
                .targetSet(TargetSetDTO.builder()
                        .targetCurrencyType("Novem")
                        .energyOrCarbonUnit("kWh")
                        .throughputUnit("Throughput Unit")
                        .targetCommitments(Collections.singletonList(TargetCommitmentDTO.builder()
                                .targetImprovement(BigDecimal.valueOf(19.000))
                                .targetPeriod("2013-2014")
                                .build()))
                        .build())
                .sectorDefinition("This is the sector definition")
                .umaDate(LocalDate.now())
                .build();
    }
}
