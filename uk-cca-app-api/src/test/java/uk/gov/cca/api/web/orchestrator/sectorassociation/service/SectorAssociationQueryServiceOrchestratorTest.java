package uk.gov.cca.api.web.orchestrator.sectorassociation.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.web.orchestrator.sectorassociation.dto.SectorAssociationDetailsResponseDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.dto.SectorAssociationResponseDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.transform.SectorAssociationDetailsResponseMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;

@ExtendWith(MockitoExtension.class)
class SectorAssociationQueryServiceOrchestratorTest {

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Mock
    private SectorAssociationDetailsResponseMapper sectorAssociationDetailsResponseMapper;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;

    @Mock
    private SectorAssociationRepository sectorAssociationRepository;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @InjectMocks
    private SectorAssociationQueryServiceOrchestrator orchestrator;

    @Test
    void testGetSectorAssociationByIdSuccessful() {
        String scope = CcaScope.EDIT_SECTOR_ASSOCIATION;
        AppUser user = AppUser.builder().build();
        Long id = 1L;
        SectorAssociationDTO sectorAssociationDTO = mock(SectorAssociationDTO.class);
        SectorAssociationDetailsDTO detailsDTO = mock(SectorAssociationDetailsDTO.class);
        SectorAssociationDetailsResponseDTO detailsResponseDTO = mock(SectorAssociationDetailsResponseDTO.class);
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
            .firstName("firstName")
            .lastName("lastName")
            .build();

        when(sectorAssociationQueryService.getSectorAssociationById(id)).thenReturn(sectorAssociationDTO);
        when(sectorAssociationDTO.getSectorAssociationDetails()).thenReturn(detailsDTO);
        when(sectorAssociationDetailsResponseMapper.toSectorAssociationResponseDTO(any(),any())).thenReturn(detailsResponseDTO);
        when(detailsDTO.getFacilitatorUserId()).thenReturn("facilitator1");
        when(userAuthService.getUserByUserId("facilitator1")).thenReturn(userInfoDTO);
        when(sectorAssociationAuthorizationResourceService
            .hasUserScopeToSectorAssociation(user, scope, id)).thenReturn(true);

        SectorAssociationResponseDTO result = orchestrator.getSectorAssociationById(id, user);

        assertNotNull(result);
        assertEquals(detailsResponseDTO, result.getSectorAssociationDetails());
    }

    @Test
    void getSectorAssociations_whenRegulatorUser() {
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        final AppUser regulatorUser = AppUser.builder()
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(AppCcaAuthority.builder().competentAuthority(ca).build()))
                .build();

        final List<SectorAssociationInfoDTO> sectorAssociationsInfo = createSectorAssociationInfoList();

        when(sectorAssociationQueryService.getRegulatorSectorAssociations(regulatorUser))
                .thenReturn(sectorAssociationsInfo);

        final List<SectorAssociationInfoDTO> result = orchestrator.getSectorAssociations(regulatorUser);

        AssertionsForClassTypes.assertThat(result).isEqualTo(sectorAssociationsInfo);

        verify(sectorAssociationQueryService, times(1))
                .getRegulatorSectorAssociations(regulatorUser);

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

        when(sectorAssociationQueryService.getSectorUserSectorAssociations(sectorUser))
                .thenReturn(sectorAssociationsInfo);

        final List<SectorAssociationInfoDTO> result2 = orchestrator.getSectorAssociations(sectorUser);
        AssertionsForClassTypes.assertThat(result2).isEqualTo(sectorAssociationsInfo);

        verify(sectorAssociationQueryService, times(1)).getSectorUserSectorAssociations(sectorUser);
    }

    @Test
    void getSectorAssociations_whenOperatorUser() {
        final List<SectorAssociationInfoDTO> sectorAssociationsInfo = createSectorAssociationInfoList();
        final  Set<Long> sectorAssociationsIds = Set.of(1L, 2L);
        final AppUser operatorUser = AppUser.builder()
                .roleType(OPERATOR)
                .authorities(List.of(
                        AppCcaAuthority.builder().sectorAssociationId(1L).build(),
                        AppCcaAuthority.builder().sectorAssociationId(2L).build()))
                .build();

        TargetUnitAccountDTO accountDTO1 = new TargetUnitAccountDTO();
        accountDTO1.setSectorAssociationId(1L);
        TargetUnitAccountDTO accountDTO2 = new TargetUnitAccountDTO();
        accountDTO2.setSectorAssociationId(2L);


        when(targetUnitAccountQueryService.getSectorAssociationIdsByAccountIds(anyList()))
                .thenReturn(sectorAssociationsIds);
        when(sectorAssociationQueryService.getUserSectorAssociations(sectorAssociationsIds))
                .thenReturn(sectorAssociationsInfo);

        final List<SectorAssociationInfoDTO> result = orchestrator.getSectorAssociations(operatorUser);

        assertThat(result).isEqualTo(sectorAssociationsInfo);

        verify(targetUnitAccountQueryService, times(1)).getSectorAssociationIdsByAccountIds(anyList());
        verify(sectorAssociationQueryService, times(1)).getUserSectorAssociations(sectorAssociationsIds);
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
        final List<SectorAssociationInfoDTO> result = orchestrator.getSectorAssociations(sectorUser);

        // Verify
        assertThat(result).isEmpty();
        verifyNoInteractions(sectorAssociationRepository);
    }

    private static List<SectorAssociationInfoDTO> createSectorAssociationInfoList() {
        final SectorAssociationInfoDTO.SectorAssociationInfoDTOBuilder builder = SectorAssociationInfoDTO.builder();
        final SectorAssociationInfoDTO sector1 = builder.id(1L).sector("ADS - Aerospace").mainContact("William MacDonald").build();
        final SectorAssociationInfoDTO sector2 = builder.id(2L).sector("AFED - Aluminium").mainContact("Sharon McBride").build();
        return List.of(sector1, sector2);
    }
}