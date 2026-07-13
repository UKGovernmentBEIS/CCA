package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDocumentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeInfo;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemesDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetInfo;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationSchemeMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private SectorAssociationRepository sectorAssociationRepository;

    @Mock
    private SectorAssociationSchemeMapper sectorAssociationSchemeMapper;

    @Mock
    private SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;

    @Test
    void getSectorAssociationSchemesBySectorAssociationId() {
        final AppUser appUser = AppUser.builder().build();
        final boolean isEditable = false;
        final Long sectorAssociationId = 1L;
        final SectorAssociationSchemeDTO sectorAssociationSchemeDTO = createSectorAssociationSchemeDTO();
        final SubsectorAssociation subsector = SubsectorAssociation.builder()
                .id(1L)
                .name("subsector name")
                .build();
        final SectorAssociation sectorAssociation = SectorAssociation.builder()
                .id(sectorAssociationId)
                .subsectorAssociations(List.of(subsector))
                .build();
        Map<SchemeVersion, SectorAssociationSchemeDTO> sectorAssociationSchemeViewMap =
                Map.of(SchemeVersion.CCA_2, sectorAssociationSchemeDTO);

        SectorAssociationSchemesDTO expected = SectorAssociationSchemesDTO.builder()
                .sectorAssociationSchemeMap(null)
                .subsectorAssociations(List.of(SubsectorAssociationInfoDTO.builder().id(1L).name("subsector name").build()))
                .build();

        SectorAssociationScheme sectorAssociationScheme = SectorAssociationScheme.builder().schemeVersion(SchemeVersion.CCA_2).build();

        when(sectorAssociationSchemeRepository.findSectorAssociationSchemesBySectorAssociationId(sectorAssociationId))
                .thenReturn(List.of(sectorAssociationScheme));
        when(sectorAssociationRepository.findById(sectorAssociationId)).thenReturn(Optional.of(sectorAssociation));
        when(sectorAssociationSchemeMapper.toSectorAssociationSchemeDTO(sectorAssociationScheme, isEditable))
                .thenReturn(sectorAssociationSchemeDTO);
        when(sectorAssociationSchemeMapper.toSectorAssociationSchemesDTO(sectorAssociationSchemeViewMap, List.of(subsector)))
                .thenReturn(expected);
        when(sectorAssociationAuthorizationResourceService.hasUserScopeToSectorAssociationSchemesBySectorId(appUser, CcaScope.EDIT_SECTOR_ADVANCED_DETAILS, sectorAssociationId))
                .thenReturn(isEditable);

        SectorAssociationSchemesDTO result = sectorAssociationSchemeService.getSectorAssociationSchemesBySectorAssociationId(sectorAssociationId, appUser);

        assertThat(result).isEqualTo(expected);
        verify(sectorAssociationSchemeRepository, times(1)).findSectorAssociationSchemesBySectorAssociationId(sectorAssociationId);
        verify(sectorAssociationRepository, times(1)).findById(sectorAssociationId);
        verify(sectorAssociationSchemeMapper, times(1)).toSectorAssociationSchemeDTO(sectorAssociationScheme, isEditable);
        verify(sectorAssociationSchemeMapper, times(1)).toSectorAssociationSchemesDTO(sectorAssociationSchemeViewMap, List.of(subsector));
        verify(sectorAssociationAuthorizationResourceService, times(1)).hasUserScopeToSectorAssociationSchemesBySectorId(appUser, CcaScope.EDIT_SECTOR_ADVANCED_DETAILS, sectorAssociationId);
    }

    @Test
    void getSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion() {
        final Long sectorAssociationId = 1L;
        final SectorAssociationSchemeInfo sectorAssociationSchemeInfo = createSectorAssociationSchemeInfo();

        SectorAssociationScheme sectorAssociationScheme = Mockito.mock(SectorAssociationScheme.class);

        when(sectorAssociationSchemeRepository
                .findSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(sectorAssociationId, SchemeVersion.CCA_2))
                .thenReturn(Optional.of(sectorAssociationScheme));
        when(sectorAssociationSchemeMapper.toSectorAssociationSchemeInfo(sectorAssociationScheme))
                .thenReturn(sectorAssociationSchemeInfo);

        SectorAssociationSchemeInfo result = sectorAssociationSchemeService
                .getSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(sectorAssociationId, SchemeVersion.CCA_2);

        assertThat(result).isEqualTo(sectorAssociationSchemeInfo);
        verify(sectorAssociationSchemeRepository, times(1))
                .findSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(sectorAssociationId, SchemeVersion.CCA_2);
        verify(sectorAssociationSchemeMapper, times(1)).toSectorAssociationSchemeInfo(sectorAssociationScheme);
    }

    @Test
    void getSectorAssociationIdBySchemeId() {
        final long sectorAssociationSchemeId = 1L;
        final long sectorAssociationId = 3L;
        final SectorAssociationScheme sectorAssociationScheme = SectorAssociationScheme.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .sectorAssociation(SectorAssociation.builder()
                        .id(sectorAssociationId)
                        .build())
                .id(sectorAssociationSchemeId)
                .build();

        when(sectorAssociationSchemeRepository.findById(sectorAssociationSchemeId)).thenReturn(Optional.of(sectorAssociationScheme));

        Long result = sectorAssociationSchemeService.getSectorAssociationIdBySchemeId(sectorAssociationSchemeId);

        assertThat(result).isEqualTo(sectorAssociationId);
        verify(sectorAssociationSchemeRepository, times(1)).findById(sectorAssociationSchemeId);
    }

    private static SectorAssociationSchemeInfo createSectorAssociationSchemeInfo() {
        return SectorAssociationSchemeInfo.builder()
                .targetSet(TargetSetInfo.builder()
                        .energyOrCarbonUnit("kWh")
                        .throughputUnit("Throughput Unit")
                        .build())
                .sectorDefinition("This is the sector definition")
                .umaDate(LocalDate.now())
                .build();
    }

    private static SectorAssociationSchemeDTO createSectorAssociationSchemeDTO() {
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
