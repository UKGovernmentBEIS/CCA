package uk.gov.cca.api.workflow.request.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoNameDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationInfoService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeService;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.core.transform.SectorReferenceDataMapper;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorReferenceDetailsServiceTest {

    @InjectMocks
    private SectorReferenceDetailsService service;

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Mock
    private SectorAssociationInfoService sectorAssociationInfoService;

    @Mock
    private SectorAssociationSchemeService sectorAssociationSchemeService;

    private static final SectorReferenceDataMapper sectorReferenceDataMapper = Mappers.getMapper(SectorReferenceDataMapper.class);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "sectorReferenceDataMapper", sectorReferenceDataMapper);
    }

    @Test
    void getSectorAssociationDetails() {
        final long sectorAssociationId = 1L;
        final String sectorName = "Sector Name";
        final String sectorAcronym = "Sector Acronym";
        final SectorAssociationContactDTO sectorAssociationContact = SectorAssociationContactDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .address(AddressDTO.builder()
                        .city("city")
                        .county("county")
                        .postcode("postcode")
                        .line1("line 1")
                        .build())
                .build();

        final SectorAssociationDTO sectorAssociationDTO = SectorAssociationDTO.builder()
                .sectorAssociationDetails(SectorAssociationDetailsDTO.builder()
                        .id(sectorAssociationId)
                        .acronym(sectorAcronym)
                        .commonName(sectorName)
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build())
                .sectorAssociationContact(sectorAssociationContact)
                .build();

        when(sectorAssociationQueryService.getSectorAssociationById(sectorAssociationId)).thenReturn(sectorAssociationDTO);

        // invoke
        service.getSectorAssociationDetails(sectorAssociationId);

        // verify
        verify(sectorAssociationQueryService, times(1)).getSectorAssociationById(sectorAssociationId);
    }

    @Test
    void getSectorAssociationMeasurementDetails() {
        final long sectorId = 2L;
        final long subSectorId = 5L;

        when(sectorAssociationInfoService.getSectorAssociationMeasurementInfo(sectorId, subSectorId))
                .thenReturn(SectorAssociationMeasurementInfoDTO.builder().build());

        // Invoke
        service.getSectorAssociationMeasurementDetails(sectorId, subSectorId);

        // Verify
        verify(sectorAssociationInfoService, times(1))
                .getSectorAssociationMeasurementInfo(sectorId, subSectorId);
    }

    @Test
    void getSectorAssociationInfo() {
        final long sectorAssociationId = 1L;
        final SectorAssociationInfoNameDTO sectorAssociationInfoNameDTO = SectorAssociationInfoNameDTO.builder()
                .id(sectorAssociationId)
                .name("Legal Name")
                .acronym("Acronym")
                .build();
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder()
                .id(sectorAssociationId)
                .name("Legal Name")
                .acronym("Acronym")
                .build();

        when(sectorAssociationQueryService.getSectorAssociationInfoNameDTO(sectorAssociationId))
                .thenReturn(sectorAssociationInfoNameDTO);

        // invoke
        SectorAssociationInfo actual = service.getSectorAssociationInfo(sectorAssociationId);

        // verify
        assertThat(actual).isEqualTo(sectorAssociationInfo);
        verify(sectorAssociationQueryService, times(1))
                .getSectorAssociationInfoNameDTO(sectorAssociationId);
    }
    
    @Test
    void getSectorAssociationAcronymAndNameBySectorAssociationId() {
        final long sectorId = 2L;
        
        when(sectorAssociationQueryService.getSectorAssociationAcronymAndName(sectorId))
                .thenReturn("ADS-identifier");

        service.getSectorAssociationAcronymAndNameBySectorAssociationId(sectorId);

        verify(sectorAssociationQueryService, times(1)).getSectorAssociationAcronymAndName(sectorId);
    }
    
    @Test
    void getSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion() {
        final long sectorId = 2L;
        
        when(sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(sectorId, SchemeVersion.CCA_2))
                .thenReturn(SectorAssociationSchemeDTO.builder().build());

        service.getSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(sectorId, SchemeVersion.CCA_2);

        verify(sectorAssociationSchemeService, times(1))
        		.getSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(sectorId, SchemeVersion.CCA_2);
    }

}
