package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactInfoResponse;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.netz.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorAssociationSiteContactServiceTest {

    @Mock
    private SectorAssociationRepository sectorAssociationRepository;
    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
    @Mock
    private RegulatorAuthorityResourceService regulatorAuthorityResourceService;
    @InjectMocks
    private SectorAssociationSiteContactService associationSiteContactService;

    @Test
    void testGetSectorAssociationSiteContacts() {
        AppUser user = AppUser.builder()
            .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
            .build();

        SectorAssociationSiteContactInfoDTO sectorAssociationSiteContactInfoDTO = SectorAssociationSiteContactInfoDTO.builder()
            .sectorName("SCT1 - sectorName1")
            .sectorAssociationId(1L)
            .userId("userId1")
            .build();

        List<SectorAssociationSiteContactInfoDTO> contacts = List.of(sectorAssociationSiteContactInfoDTO);

        Page<SectorAssociationSiteContactInfoDTO> contactInfoPage =
            new PageImpl<>(contacts, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "acronym")), contacts.size());

        SectorAssociationSiteContactInfoResponse expected = SectorAssociationSiteContactInfoResponse.builder()
            .siteContacts(contacts).editable(true).totalItems(1L).build();


        when(sectorAssociationRepository.findSectorAssociationsSiteContactsByCA(user.getCompetentAuthority(),
            PageRequest.of(0, 10, Sort.by( "acronym")))).thenReturn(contactInfoPage);


        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(user, Scope.EDIT_USER)).thenReturn(true);

        SectorAssociationSiteContactInfoResponse
            response = associationSiteContactService.getSectorAssociationSiteContacts(user, 0, 10);

        assertEquals(expected, response);
        verify(compAuthAuthorizationResourceService, times(1))
            .hasUserScopeToCompAuth(user, Scope.EDIT_USER);
    }

    @Test
    void testUpdateSectorAssociationSiteContacts() {
        AppUser user = AppUser.builder()
                .authorities(
                        Arrays.asList(
                                AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()
                        )
                )
                .roleType(RoleTypeConstants.REGULATOR)
                .build();
        List<SectorAssociationSiteContactDTO> contactDTOs = List.of(
            new SectorAssociationSiteContactDTO(1L, "userId1")
        );
        List<Long> sectors = List.of(1L, 2L);
        List<String> accountIds = List.of("userId1", "userId2");

        List<SectorAssociation> sectorAssociations = List.of(createSectorAssociation());

        when(sectorAssociationRepository.findAllByIdIn(anyList())).thenReturn(sectorAssociations);
        when(sectorAssociationRepository.findSectorAssociationsIdsByCompetentAuthority(CompetentAuthorityEnum.ENGLAND)).thenReturn(sectors);
        when(regulatorAuthorityResourceService.findUsersByCompetentAuthority(CompetentAuthorityEnum.ENGLAND)).thenReturn(accountIds);

        associationSiteContactService.updateSectorAssociationSiteContacts(user, contactDTOs);

        verify(sectorAssociationRepository, times(1)).findAllByIdIn(anyList());
        verify(sectorAssociationRepository, times(1)).findSectorAssociationsIdsByCompetentAuthority(any());
        verify(regulatorAuthorityResourceService, times(1)).findUsersByCompetentAuthority(any());
    }

    private SectorAssociation createSectorAssociation() {
        Location location = Location.builder()
            .postcode("12345")
            .line1("123 Main St")
            .city("Springfield")
            .county("CountyName")
            .build();

        SectorAssociationContact contact = SectorAssociationContact.builder()
            .title("Mr.")
            .firstName("John")
            .lastName("Doe")
            .jobTitle("Director")
            .organisationName("Acme Corp")
            .phoneNumber("123456789")
            .email("john.doe@example.com")
            .location(location)
            .build();

        return SectorAssociation.builder()
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .legalName("Some Association Legal")
            .name("Some Association")
            .acronym("SA")
            .facilitatorUserId("userId")
            .energyEprFactor("Energy Factor")
            .location(location)
            .sectorAssociationContact(contact)
            .id(1L)
            .build();
    }
}