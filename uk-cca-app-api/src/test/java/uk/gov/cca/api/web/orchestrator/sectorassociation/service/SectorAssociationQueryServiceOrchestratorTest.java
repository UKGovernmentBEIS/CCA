package uk.gov.cca.api.web.orchestrator.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.cca.api.authorization.ccaauth.rules.services.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.web.orchestrator.sectorassociation.dto.SectorAssociationDetailsResponseDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.dto.SectorAssociationResponseDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.transform.SectorAssociationDetailsResponseMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SectorAssociationQueryServiceOrchestratorTest {

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Mock
    private SectorAssociationDetailsResponseMapper sectorAssociationDetailsResponseMapper;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;


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
}