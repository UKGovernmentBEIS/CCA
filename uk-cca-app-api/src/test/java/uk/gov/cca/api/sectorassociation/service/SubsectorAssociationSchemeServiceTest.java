package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeInfo;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemesDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetInfo;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationSchemeRepository;
import uk.gov.cca.api.sectorassociation.transform.SubsectorAssociationSchemeMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;

    @Test
    void getSubsectorAssociationSchemesBySubsectorAssociationId() {
        final AppUser appUser = AppUser.builder().build();
        final Long sectorAssociationId = 1L;
        final Long subsectorAssociationId = 2L;
        final String name = "name";
        final SubsectorAssociationDTO subsectorAssociationDTO = SubsectorAssociationDTO.builder().name(name).build();
        final SubsectorAssociationSchemeDTO subsectorAssociationSchemeDTO = createSubsectorAssociationSchemeDTO();
        final SubsectorAssociationSchemesDTO subsectorAssociationSchemesDTO = SubsectorAssociationSchemesDTO.builder()
                .name(name)
                .subsectorAssociationSchemeMap(Map.of(SchemeVersion.CCA_2, subsectorAssociationSchemeDTO))
                .build();
        final boolean isEditable = false;

        SubsectorAssociationScheme subsectorAssociationScheme = SubsectorAssociationScheme.builder().schemeVersion(SchemeVersion.CCA_2).build();

        when(subsectorAssociationSchemeRepository.findSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId))
                .thenReturn(List.of(subsectorAssociationScheme));
        when(subsectorAssociationSchemeMapper.toSubsectorAssociationSchemeDTO(subsectorAssociationScheme, isEditable)).thenReturn(subsectorAssociationSchemeDTO);
        when(subsectorAssociationSchemeMapper.toSubsectorAssociationSchemesDTO(name, Map.of(SchemeVersion.CCA_2, subsectorAssociationSchemeDTO))).thenReturn(subsectorAssociationSchemesDTO);
        when(subsectorAssociationService.getSectorAssociationIdBySubsectorId(subsectorAssociationId)).thenReturn(sectorAssociationId);
        when(subsectorAssociationService.getSubsectorById(subsectorAssociationId)).thenReturn(subsectorAssociationDTO);
        when(sectorAssociationAuthorizationResourceService.hasUserScopeToSubsectorAssociationSchemesBySectorId(appUser, CcaScope.EDIT_SECTOR_ADVANCED_DETAILS, sectorAssociationId))
                .thenReturn(isEditable);

        SubsectorAssociationSchemesDTO result = subsectorAssociationSchemeService.getSubsectorAssociationSchemesBySubsectorAssociationId(subsectorAssociationId, appUser);

        assertThat(result).isEqualTo(subsectorAssociationSchemesDTO);
        assertFalse(subsectorAssociationSchemesDTO.getSubsectorAssociationSchemeMap().get(SchemeVersion.CCA_2).isEditable());
        verify(subsectorAssociationSchemeRepository, times(1)).findSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId);
        verify(subsectorAssociationSchemeMapper, times(1)).toSubsectorAssociationSchemeDTO(subsectorAssociationScheme, isEditable);
        verify(subsectorAssociationService, times(1)).getSectorAssociationIdBySubsectorId(subsectorAssociationId);
        verify(subsectorAssociationService, times(1)).getSubsectorById(subsectorAssociationId);
        verify(sectorAssociationAuthorizationResourceService, times(1)).hasUserScopeToSubsectorAssociationSchemesBySectorId(appUser, CcaScope.EDIT_SECTOR_ADVANCED_DETAILS, sectorAssociationId);
    }

    @Test
    void getSubsectorAssociationSchemesMap() {
        final SubsectorAssociationSchemeInfo subsectorAssociationSchemeInfo = createSubsectorAssociationSchemeInfo();
        final Map<SchemeVersion, SubsectorAssociationSchemeInfo> subsectorAssociationSchemeMap =
                Map.of(SchemeVersion.CCA_2, subsectorAssociationSchemeInfo);

        SubsectorAssociationScheme subsectorAssociationScheme = SubsectorAssociationScheme.builder()
                .schemeVersion(SchemeVersion.CCA_2)
                .build();

        final Long subsectorAssociationId = 1L;

        when(subsectorAssociationSchemeRepository.findSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId))
                .thenReturn(List.of(subsectorAssociationScheme));
        when(subsectorAssociationSchemeMapper.toSubsectorAssociationSchemeInfo(subsectorAssociationScheme)).thenReturn(subsectorAssociationSchemeInfo);

        Map<SchemeVersion, SubsectorAssociationSchemeInfo> result = subsectorAssociationSchemeService.getSubsectorAssociationSchemesMap(subsectorAssociationId);

        assertThat(result).isEqualTo(subsectorAssociationSchemeMap);
        verify(subsectorAssociationSchemeRepository, times(1)).findSubsectorAssociationSchemeBySubsectorAssociationId(subsectorAssociationId);
        verify(subsectorAssociationSchemeMapper, times(1)).toSubsectorAssociationSchemeInfo(subsectorAssociationScheme);
    }

    @Test
    void getSectorAssociationIdBySubsectorSchemeId() {
        final long subsectorAssociationSchemeId = 1L;
        final long subsectorAssociationId = 2L;
        final long sectorAssociationId = 3L;
        final SubsectorAssociationScheme subsectorAssociationScheme = SubsectorAssociationScheme.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .subsectorAssociation(SubsectorAssociation.builder()
                        .id(subsectorAssociationId)
                        .sectorAssociation(SectorAssociation.builder()
                                .id(sectorAssociationId)
                                .build())
                        .build())
                .id(subsectorAssociationSchemeId)
                .build();

        when(subsectorAssociationSchemeRepository.findById(subsectorAssociationSchemeId)).thenReturn(Optional.of(subsectorAssociationScheme));

        Long result = subsectorAssociationSchemeService.getSectorAssociationIdBySubsectorSchemeId(subsectorAssociationSchemeId);

        assertThat(result).isEqualTo(sectorAssociationId);
        verify(subsectorAssociationSchemeRepository, times(1)).findById(subsectorAssociationSchemeId);
    }

    private SubsectorAssociationSchemeInfo createSubsectorAssociationSchemeInfo() {
        return SubsectorAssociationSchemeInfo.builder()
                .targetSet(TargetSetInfo.builder()
                        .energyOrCarbonUnit("kWh")
                        .throughputUnit("tonne")
                        .build())
                .build();
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