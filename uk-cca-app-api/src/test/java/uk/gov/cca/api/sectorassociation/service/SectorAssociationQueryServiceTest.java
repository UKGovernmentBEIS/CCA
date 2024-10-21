package uk.gov.cca.api.sectorassociation.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.service.AppUserService;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationMapper;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

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

        SectorAssociation sectorAssociation = Mockito.mock(SectorAssociation.class);

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
    void getSectorAssociationIdentifier() {
        final Long sectorAssociationId = 1L;

        SectorAssociation sectorAssociation = SectorAssociation.builder()
                .id(sectorAssociationId)
                .acronym("ACR")
                .name("SectorName")
                .build();

        when(sectorAssociationRepository.findById(sectorAssociationId)).thenReturn(Optional.of(sectorAssociation));

        String result = sectorAssociationQueryService.getSectorAssociationIdentifier(sectorAssociationId);

        assertThat(result).isEqualTo(sectorAssociation.getAcronym() + " - " + sectorAssociation.getName());
        verify(sectorAssociationRepository, times(1)).findById(sectorAssociationId);
    }

    @Test
    void getSectorAssociations_whenRegulatorUser() {
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        final AppUser regulatorUser = AppUser.builder()
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(AppCcaAuthority.builder().competentAuthority(ca).build()))
                .build();

        final List<SectorAssociationInfoDTO> sectorAssociationsInfo = createSectorAssociationInfoList();

        when(sectorAssociationRepository.findSectorAssociations(ca)).thenReturn(sectorAssociationsInfo);

        final List<SectorAssociationInfoDTO> result = sectorAssociationQueryService.getSectorAssociations(regulatorUser);

        AssertionsForClassTypes.assertThat(result).isEqualTo(sectorAssociationsInfo);

        verify(sectorAssociationRepository, times(1)).findSectorAssociations(ca);

    }

    @Test
    void getSectorAssociations_whenSectorUser() {
        final List<SectorAssociationInfoDTO> sectorAssociationsInfo = createSectorAssociationInfoList();

        final AppUser sectorUser = AppUser.builder()
                .roleType(SECTOR_USER)
                .authorities(List.of(
                        AppCcaAuthority.builder().sectorAssociationId(1L).build(),
                        AppCcaAuthority.builder().sectorAssociationId(2L).build()))
                .build();

        when(appUserService.getUserSectorAssociations(sectorUser)).thenReturn(Set.of(1L, 2L));
        when(sectorAssociationRepository.findSectorAssociations(Set.of(1L, 2L))).thenReturn(sectorAssociationsInfo);

        final List<SectorAssociationInfoDTO> result2 = sectorAssociationQueryService.getSectorAssociations(sectorUser);
        AssertionsForClassTypes.assertThat(result2).isEqualTo(sectorAssociationsInfo);

        verify(sectorAssociationRepository, times(1)).findSectorAssociations(Set.of(1L, 2L));
    }

    @Test
    void getSectorAssociations_whenDefaultUser() {
        final AppUser sectorUser = AppUser.builder()
                .roleType(RoleTypeConstants.VERIFIER)
                .authorities(List.of(
                        AppCcaAuthority.builder().sectorAssociationId(1L).build(),
                        AppCcaAuthority.builder().sectorAssociationId(2L).build()))
                .build();

        // Invoke
        final List<SectorAssociationInfoDTO> result = sectorAssociationQueryService.getSectorAssociations(sectorUser);

        // Verify
        assertThat(result).isEmpty();
        verifyNoInteractions(sectorAssociationRepository);
    }

    @Test
    void getSectorAssociationCa() {
        Long sectorAssociationId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        SectorAssociation sectorAssociation = Mockito.mock(SectorAssociation.class);

        when(sectorAssociation.getCompetentAuthority()).thenReturn(competentAuthority);
        when(sectorAssociationRepository.findById(sectorAssociationId)).thenReturn(Optional.of(sectorAssociation));

        CompetentAuthorityEnum sectorAssociationCa = sectorAssociationQueryService.getSectorAssociationCa(sectorAssociationId);

        assertEquals(competentAuthority, sectorAssociationCa);

    }

    @Test
    void findSectorAssociationContactById() {
        final long sectorAssociationId = 1L;
        final NoticeRecipientDTO sectorContact = NoticeRecipientDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                .build();

        when(sectorAssociationRepository.findSectorAssociationNoticeRecipientById(sectorAssociationId))
                .thenReturn(sectorContact);

        // Invoke
        NoticeRecipientDTO sectorAssociationContactById = sectorAssociationQueryService.getSectorAssociationNoticeRecipientById(sectorAssociationId);

        // Verify
        assertThat(sectorContact).isEqualTo(sectorAssociationContactById);
        verify(sectorAssociationRepository, times(1)).findSectorAssociationNoticeRecipientById(sectorAssociationId);
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

    private static List<SectorAssociationInfoDTO> createSectorAssociationInfoList() {
        final SectorAssociationInfoDTO.SectorAssociationInfoDTOBuilder builder = SectorAssociationInfoDTO.builder();
        final SectorAssociationInfoDTO sector1 = builder.id(1L).sector("ADS - Aerospace").mainContact("William MacDonald").build();
        final SectorAssociationInfoDTO sector2 = builder.id(2L).sector("AFED - Aluminium").mainContact("Sharon McBride").build();
        return List.of(sector1, sector2);
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
}
