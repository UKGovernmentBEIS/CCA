package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.service.AppUserService;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoNameDTO;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorAssociationQueryServiceTest {

    @InjectMocks
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Mock
    private SectorAssociationRepository sectorAssociationRepository;

    @Mock
    private SectorAssociationMapper sectorAssociationMapper;

    @Mock
    private AppUserService appUserService;

    @Test
    void getSectorAssociationById() {
        final Long sectorAssociationId = 1L;
        final SectorAssociationDTO sectorAssociationDTO = createSectorAssociationDTO();

        SectorAssociation sectorAssociation = mock(SectorAssociation.class);

        when(sectorAssociationRepository.findById(sectorAssociationId)).thenReturn(Optional.of(sectorAssociation));
        when(sectorAssociationMapper.toSectorAssociationDTO(sectorAssociation)).thenReturn(sectorAssociationDTO);

        SectorAssociationDTO result = sectorAssociationQueryService.getSectorAssociationById(sectorAssociationId);
        assertThat(result).isEqualTo(sectorAssociationDTO);
        verify(sectorAssociationRepository, times(1)).findById(sectorAssociationId);
        verify(sectorAssociationMapper, times(1)).toSectorAssociationDTO(sectorAssociation);
    }

    @Test
    void getSectorAssociationAcronymById() {
        final long sectorAssociationId = 1L;
        final String acronym = "acronym";

        when(sectorAssociationRepository.findSectorAssociationAcronymById(sectorAssociationId))
                .thenReturn(acronym);

        // Invoke
        String actual = sectorAssociationQueryService.getSectorAssociationAcronymById(sectorAssociationId);

        // Verify
        assertThat(actual).isEqualTo(acronym);
        verify(sectorAssociationRepository, times(1)).findSectorAssociationAcronymById(sectorAssociationId);
    }
    
    @Test
    void getSectorAssociationName() {
        final Long sectorAssociationId = 1L;

        SectorAssociation sectorAssociation = SectorAssociation.builder()
                .id(sectorAssociationId)
                .name("SectorName")
                .build();

        when(sectorAssociationRepository.findById(sectorAssociationId)).thenReturn(Optional.of(sectorAssociation));

        String result = sectorAssociationQueryService.getSectorAssociationName(sectorAssociationId);

        assertThat(result).isEqualTo(sectorAssociation.getName());
        verify(sectorAssociationRepository, times(1)).findById(sectorAssociationId);
    }

    @Test
    void getSectorAssociationAcronymAndName() {
        final Long sectorAssociationId = 1L;

        SectorAssociation sectorAssociation = SectorAssociation.builder()
                .id(sectorAssociationId)
                .acronym("ACR")
                .name("SectorName")
                .build();

        when(sectorAssociationRepository.findById(sectorAssociationId)).thenReturn(Optional.of(sectorAssociation));

        String result = sectorAssociationQueryService.getSectorAssociationAcronymAndName(sectorAssociationId);

        assertThat(result).isEqualTo(sectorAssociation.getAcronym() + " - " + sectorAssociation.getName());
        verify(sectorAssociationRepository, times(1)).findById(sectorAssociationId);
    }

    @Test
    void getSectorAssociationCa() {
        Long sectorAssociationId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        SectorAssociation sectorAssociation = mock(SectorAssociation.class);

        when(sectorAssociation.getCompetentAuthority()).thenReturn(competentAuthority);
        when(sectorAssociationRepository.findById(sectorAssociationId)).thenReturn(Optional.of(sectorAssociation));

        CompetentAuthorityEnum sectorAssociationCa = sectorAssociationQueryService.getSectorAssociationCa(sectorAssociationId);

        assertEquals(competentAuthority, sectorAssociationCa);

    }

    @Test
    void getSectorAssociationFacilitatorUserId() {
        final Long sectorAssociationId = 1L;

        SectorAssociation sectorAssociation = SectorAssociation.builder()
                .id(sectorAssociationId)
                .acronym("ACR")
                .name("SectorName")
                .facilitatorUserId("Facilitator")
                .build();

        when(sectorAssociationRepository.findById(sectorAssociationId)).thenReturn(Optional.of(sectorAssociation));

        String result = sectorAssociationQueryService.getSectorAssociationFacilitatorUserId(sectorAssociationId);

        assertThat(result).isEqualTo(sectorAssociation.getFacilitatorUserId() );
        verify(sectorAssociationRepository, times(1)).findById(sectorAssociationId);
    }

    private static SectorAssociationDTO createSectorAssociationDTO() {
        return SectorAssociationDTO.builder()
                .sectorAssociationContact(SectorAssociationContactDTO.builder()
                        .title("Mr.")
                        .firstName("John")
                        .lastName("Doe")
                        .jobTitle("Director")
                        .organisationName("Acme Corp")
                        .phoneNumber("123456789")
                        .email("john.doe@example.com")
                        .build())
                .sectorAssociationDetails(SectorAssociationDetailsDTO.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .legalName("Some Association Legal")
                        .commonName("Some Association")
                        .acronym("SA")
                        .energyIntensiveOrEPR("Energy Factor")
                        .noticeServiceAddress(AddressDTO.builder()
                                .postcode("12345")
                                .line1("123 Main St")
                                .line2("124 Second St")
                                .city("Springfield")
                                .county("CountyName")
                                .build())
                        .build())
                .build();
    }

    @Test
    void getSectorAssociationIdByAcronym() {
        final long sectorAssociationId = 1L;
        final String acronym = "acronym";

        when(sectorAssociationRepository.findSectorAssociationIdByAcronym(acronym))
                .thenReturn(Optional.of(sectorAssociationId));

        // Invoke
        Optional<Long> actual = sectorAssociationQueryService.getSectorAssociationIdByAcronym(acronym);

        // Verify
        assertThat(actual).isEqualTo(Optional.of(sectorAssociationId));
        verify(sectorAssociationRepository, times(1)).findSectorAssociationIdByAcronym(acronym);
    }

    @Test
    void testGetRegulatorSectorAssociations() {
        AppUser appUser = mock(AppUser.class);
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        when(appUser.getCompetentAuthority()).thenReturn(competentAuthority);

        List<SectorAssociationInfoDTO> expectedList = List.of(new SectorAssociationInfoDTO());
        when(sectorAssociationRepository.findSectorAssociations(competentAuthority)).thenReturn(expectedList);

        List<SectorAssociationInfoDTO> result = sectorAssociationQueryService.getRegulatorSectorAssociations(appUser);
        assertEquals(expectedList, result);

        verify(appUser).getCompetentAuthority();
        verify(sectorAssociationRepository).findSectorAssociations(competentAuthority);
    }

    @Test
    void testGetSectorUserSectorAssociations() {
        AppUser appUser = mock(AppUser.class);
        Set<Long> sectorIds = Set.of(1L, 2L, 3L);
        List<SectorAssociationInfoDTO> expectedList = List.of(new SectorAssociationInfoDTO());

        when(appUserService.getUserSectorAssociations(appUser)).thenReturn(sectorIds);
        when(sectorAssociationRepository.findSectorAssociations(sectorIds)).thenReturn(expectedList);

        List<SectorAssociationInfoDTO> result = sectorAssociationQueryService.getSectorUserSectorAssociations(appUser);
        assertEquals(expectedList, result);

        verify(appUserService).getUserSectorAssociations(appUser);
        verify(sectorAssociationRepository).findSectorAssociations(sectorIds);
    }

    @Test
    void testGetOperatorUserSectorAssociations() {

        Set<Long> sectorAssociationsIds = Set.of(1L, 2L, 3L);

        final SectorAssociationInfoDTO sectorAssociationInfoDTO = SectorAssociationInfoDTO.builder().id(1L).build();
        List<SectorAssociationInfoDTO> expectedList = List.of(sectorAssociationInfoDTO);

        when(sectorAssociationQueryService
                        .getUserSectorAssociations(sectorAssociationsIds))
                .thenReturn(expectedList);

        List<SectorAssociationInfoDTO> result = sectorAssociationQueryService
                .getUserSectorAssociations(sectorAssociationsIds);
        assertEquals(expectedList, result);

        verify(sectorAssociationRepository).findSectorAssociations(sectorAssociationsIds);
    }

    @Test
    void getSectorAssociationInfoNameDTO() {
        final long sectorAssociationId = 1L;
        final SectorAssociation sectorAssociation = SectorAssociation.builder()
                .id(sectorAssociationId)
                .legalName("SectorName")
                .acronym("acronym")
                .build();
        final SectorAssociationInfoNameDTO sectorAssociationInfo = SectorAssociationInfoNameDTO.builder()
                .id(sectorAssociationId)
                .name("SectorName")
                .acronym("acronym")
                .build();

        when(sectorAssociationRepository.findById(sectorAssociationId))
                .thenReturn(Optional.of(sectorAssociation));
        when(sectorAssociationMapper.toSectorAssociationInfoNameDTO(sectorAssociation))
                .thenReturn(sectorAssociationInfo);

        // Invoke
        sectorAssociationQueryService.getSectorAssociationInfoNameDTO(sectorAssociationId);

        // Verify
        verify(sectorAssociationRepository, times(1)).findById(sectorAssociationId);
        verify(sectorAssociationMapper, times(1)).toSectorAssociationInfoNameDTO(sectorAssociation);
    }

    @Test
    void getSectorAssociationInfoNameDTO_error() {
        final long sectorAssociationId = 1L;

        when(sectorAssociationRepository.findById(sectorAssociationId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                sectorAssociationQueryService.getSectorAssociationInfoNameDTO(sectorAssociationId));

        // Verify
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
        verify(sectorAssociationRepository, times(1)).findById(sectorAssociationId);
        verifyNoInteractions(sectorAssociationMapper);
    }
    
    @Test
    void getSectorAssociationsInfoNameDTO() {
        final long sectorAssociationId1 = 1L;
        final long sectorAssociationId2 = 2L;
        final SectorAssociation sectorAssociation1 = SectorAssociation.builder()
                .id(sectorAssociationId1)
                .legalName("SectorName")
                .acronym("acronym")
                .build();
        final SectorAssociation sectorAssociation2 = SectorAssociation.builder()
                .id(sectorAssociationId2)
                .legalName("SectorName2")
                .acronym("acronym2")
                .build();
        final SectorAssociationInfoNameDTO sectorAssociationInfo1 = SectorAssociationInfoNameDTO.builder()
                .id(sectorAssociationId1)
                .name("SectorName")
                .acronym("acronym")
                .build();
        final SectorAssociationInfoNameDTO sectorAssociationInfo2 = SectorAssociationInfoNameDTO.builder()
                .id(sectorAssociationId2)
                .name("SectorName2")
                .acronym("acronym2")
                .build();

        when(sectorAssociationRepository.findAllByIdIn(List.of(1L, 2L)))
                .thenReturn(List.of(sectorAssociation1, sectorAssociation2));
        when(sectorAssociationMapper.toSectorAssociationInfoNameDTO(sectorAssociation1))
                .thenReturn(sectorAssociationInfo1);
        when(sectorAssociationMapper.toSectorAssociationInfoNameDTO(sectorAssociation2))
        		.thenReturn(sectorAssociationInfo2);

        // Invoke
        sectorAssociationQueryService.getSectorAssociationsInfoNameDTO(List.of(sectorAssociationId1, sectorAssociationId2));

        // Verify
        verify(sectorAssociationRepository, times(1)).findAllByIdIn(List.of(sectorAssociationId1, sectorAssociationId2));
        verify(sectorAssociationMapper, times(1)).toSectorAssociationInfoNameDTO(sectorAssociation1);
        verify(sectorAssociationMapper, times(1)).toSectorAssociationInfoNameDTO(sectorAssociation2);
    }

	@Test
	void shouldReturnSectorAssociationWhenExists() {
		String acronym = "ADS_1";
		SchemeVersion version = SchemeVersion.CCA_3;

		SectorAssociation expected = mock(SectorAssociation.class);
		when(sectorAssociationRepository.findByAcronymAndSectorAssociationSchemesSchemeVersionIs(acronym, version))
				.thenReturn(Optional.of(expected));

		Optional<SectorAssociation> result = sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme(acronym, version);

		assertTrue(result.isPresent());
		assertEquals(expected, result.get());
	}
}
