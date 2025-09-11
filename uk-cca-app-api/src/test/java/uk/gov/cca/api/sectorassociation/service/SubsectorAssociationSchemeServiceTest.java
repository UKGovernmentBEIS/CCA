package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.*;
import uk.gov.cca.api.sectorassociation.transform.SubsectorAssociationSchemeMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationSchemeRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubsectorAssociationSchemeServiceTest {

    @InjectMocks
    private SubsectorAssociationSchemeService subsectorAssociationSchemeService;

    @Mock
    private SubsectorAssociationSchemeRepository subsectorAssociationSchemeRepository;
    
    @Mock
    private SubsectorAssociationSchemeMapper subsectorAssociationSchemeMapper;
    
    @Mock
    private SubsectorAssociationService subsectorAssociationService;

    @Test
    void getSubsectorAssociationSchemesBySubsectorAssociationId() {
        final Long sectorAssociationId = 1L;
        final Long subsectorAssociationId = 2L;
        final String name = "name";
        final SubsectorAssociationDTO subsectorAssociationDTO = SubsectorAssociationDTO.builder().name(name).build();
        final SubsectorAssociationSchemeDTO subsectorAssociationSchemeDTO = createSubsectorAssociationSchemeDTO();
        final SubsectorAssociationSchemesDTO subsectorAssociationSchemesDTO = SubsectorAssociationSchemesDTO.builder()
        		.name(name)
        		.subsectorAssociationSchemeMap(Map.of(SchemeVersion.CCA_2, subsectorAssociationSchemeDTO))
        		.build();

        SubsectorAssociationScheme subsectorAssociationScheme = SubsectorAssociationScheme.builder().schemeVersion(SchemeVersion.CCA_2).build();

        when(subsectorAssociationSchemeRepository.findSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId))
        		.thenReturn(List.of(subsectorAssociationScheme));
        when(subsectorAssociationSchemeMapper.toSubsectorAssociationSchemeDTO(subsectorAssociationScheme)).thenReturn(subsectorAssociationSchemeDTO);
        when(subsectorAssociationSchemeMapper.toSubsectorAssociationSchemesDTO(name, Map.of(SchemeVersion.CCA_2, subsectorAssociationSchemeDTO))).thenReturn(subsectorAssociationSchemesDTO);
        when(subsectorAssociationService.getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId)).thenReturn(List.of(subsectorAssociationId));
        when(subsectorAssociationService.getSubsectorById(subsectorAssociationId)).thenReturn(subsectorAssociationDTO);

        SubsectorAssociationSchemesDTO result = subsectorAssociationSchemeService.getSubsectorAssociationSchemesBySubsectorAssociationId(sectorAssociationId, subsectorAssociationId);

        assertThat(result).isEqualTo(subsectorAssociationSchemesDTO);
        verify(subsectorAssociationSchemeRepository, times(1)).findSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId);
        verify(subsectorAssociationSchemeMapper, times(1)).toSubsectorAssociationSchemeDTO(subsectorAssociationScheme);
        verify(subsectorAssociationService, times(1)).getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId);
    }
    
    @Test
    void getSubsectorAssociationSchemesBySubsectorAssociationId_subsector_not_belongs_to_sector() {
        final Long sectorAssociationId = 1L;
        final Long subsectorAssociationId = 2L;

        when(subsectorAssociationService.getSubsectorAssociationIdsBySectorAssociationId(sectorAssociationId)).thenReturn(List.of(1L));

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> subsectorAssociationSchemeService.getSubsectorAssociationSchemesBySubsectorAssociationId(sectorAssociationId, subsectorAssociationId));
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION);
        verifyNoMoreInteractions(subsectorAssociationSchemeRepository);
    }

    @Test
    void getSubsectorAssociationSchemesMap() {
        final SubsectorAssociationSchemeDTO subsectorAssociationSchemeDTO = createSubsectorAssociationSchemeDTO();
        final Map<SchemeVersion, SubsectorAssociationSchemeDTO> subsectorAssociationSchemeMap = 
        		Map.of(SchemeVersion.CCA_2, subsectorAssociationSchemeDTO);

        SubsectorAssociationScheme subsectorAssociationScheme = SubsectorAssociationScheme.builder()
        		.schemeVersion(SchemeVersion.CCA_2)
        		.build();

        final Long subsectorAssociationId = 1L;

        when(subsectorAssociationSchemeRepository.findSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId))
        		.thenReturn(List.of(subsectorAssociationScheme));
        when(subsectorAssociationSchemeMapper.toSubsectorAssociationSchemeDTO(subsectorAssociationScheme)).thenReturn(subsectorAssociationSchemeDTO);

        Map<SchemeVersion, SubsectorAssociationSchemeDTO> result = subsectorAssociationSchemeService.getSubsectorAssociationSchemesMap(subsectorAssociationId);

        assertThat(result).isEqualTo(subsectorAssociationSchemeMap);
        verify(subsectorAssociationSchemeRepository, times(1)).findSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId);
        verify(subsectorAssociationSchemeMapper, times(1)).toSubsectorAssociationSchemeDTO(subsectorAssociationScheme);
    }

    private SubsectorAssociationSchemeDTO createSubsectorAssociationSchemeDTO() {
        return SubsectorAssociationSchemeDTO.builder()
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