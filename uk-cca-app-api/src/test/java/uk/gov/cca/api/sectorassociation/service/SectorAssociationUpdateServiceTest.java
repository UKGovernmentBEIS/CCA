package uk.gov.cca.api.sectorassociation.service;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsUpdateDTO;
import uk.gov.cca.api.sectorassociation.transform.LocationMapper;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SectorAssociationUpdateServiceTest {

    @InjectMocks
    private SectorAssociationUpdateService sectorAssociationUpdateService;

    @Mock
    private SectorAssociationRepository sectorAssociationRepository;

    @Mock
    private LocationMapper locationMapper;

    @Test
    void updateSectorAssociation_Details_regulator() {

        Long sectorAssociationId = 1L;
        SectorAssociationDetailsUpdateDTO updateDTO = createSectorAssociationDetailsUpdateDTO();

        SectorAssociation sectorAssociation = createSectorAssociation();
        when(sectorAssociationRepository.findById(sectorAssociationId)).thenReturn(Optional.ofNullable(sectorAssociation));

        Location mockLocation = new Location();
        when(locationMapper.addressDTOToLocation(any(AddressDTO.class))).thenReturn(mockLocation);

        sectorAssociationUpdateService.updateSectorAssociationDetails(sectorAssociationId, updateDTO);

        assertEquals(updateDTO.getCommonName(), sectorAssociation.getName());
        assertEquals(updateDTO.getLegalName(), sectorAssociation.getLegalName());

        verify(sectorAssociationRepository).findById(sectorAssociationId);
        verify(locationMapper, times(1)).addressDTOToLocation(any(AddressDTO.class));
    }

    @Test
    void updateSectorAssociation_Details_sector() {

        Long sectorAssociationId = 1L;

        SectorAssociationDetailsUpdateDTO updateDTO = createSectorAssociationDetailsUpdateDTO();

        SectorAssociation sectorAssociation = createSectorAssociation();
        when(sectorAssociationRepository.findById(sectorAssociationId)).thenReturn(Optional.ofNullable(sectorAssociation));

        Location mockLocation = new Location();
        when(locationMapper.addressDTOToLocation(any(AddressDTO.class))).thenReturn(mockLocation);

        sectorAssociationUpdateService.updateSectorAssociationDetails(sectorAssociationId, updateDTO);

        assertEquals(updateDTO.getCommonName(), sectorAssociation.getName());
        assertEquals(updateDTO.getLegalName(), sectorAssociation.getLegalName());
        assertEquals(updateDTO.getLegalName(), sectorAssociation.getLegalName());

        verify(sectorAssociationRepository).findById(sectorAssociationId);
        verify(locationMapper, times(1)).addressDTOToLocation(any(AddressDTO.class));
    }

    @Test
    void updateSectorAssociation_Contact() {

        Long sectorAssociationId = 1L;
        SectorAssociationContactDTO updateDTO = createSectorAssociationContactDTO();

        SectorAssociation sectorAssociation = createSectorAssociation();
        when(sectorAssociationRepository.findById(sectorAssociationId)).thenReturn(Optional.ofNullable(sectorAssociation));

        Location mockLocation = new Location();
        when(locationMapper.addressDTOToLocation(any(AddressDTO.class))).thenReturn(mockLocation);

        sectorAssociationUpdateService.updateSectorAssociationContact(sectorAssociationId, updateDTO);

        SectorAssociationContact sectorAssociationContact = sectorAssociation.getSectorAssociationContact();

        assertEquals(updateDTO.getFirstName(), sectorAssociationContact.getFirstName());
        assertEquals(updateDTO.getLastName(), sectorAssociationContact.getLastName());
        assertEquals(updateDTO.getFirstName(), sectorAssociationContact.getFirstName());
        assertEquals(updateDTO.getTitle(), sectorAssociationContact.getTitle());
        assertEquals(updateDTO.getEmail(), sectorAssociationContact.getEmail());
        assertEquals(updateDTO.getJobTitle(), sectorAssociationContact.getJobTitle());
        assertEquals(updateDTO.getPhoneNumber(), sectorAssociationContact.getPhoneNumber());
        assertEquals(updateDTO.getOrganisationName(), sectorAssociationContact.getOrganisationName());

        verify(sectorAssociationRepository).findById(sectorAssociationId);
        verify(locationMapper, times(1)).addressDTOToLocation(any(AddressDTO.class));
    }

    private SectorAssociationDetailsUpdateDTO createSectorAssociationDetailsUpdateDTO() {
        AddressDTO addressDTO = AddressDTO.builder()
            .postcode("12345")
            .line1("123 Main St")
            .line2("124 Second St")
            .city("Springfield")
            .county("CountyName")
            .build();

       return SectorAssociationDetailsUpdateDTO.builder()
            .legalName("Some Association Legal")
            .commonName("Some Association")
            .noticeServiceAddress(addressDTO)
            .build();
    }

    private SectorAssociationContactDTO createSectorAssociationContactDTO() {
        AddressDTO addressDTO = AddressDTO.builder()
            .postcode("12345")
            .line1("123 Main St")
            .line2("124 Second St")
            .city("Springfield")
            .county("CountyName")
            .build();

       return SectorAssociationContactDTO.builder()
            .title("Mr.")
            .firstName("John")
            .lastName("Doe")
            .jobTitle("Director")
            .organisationName("Acme Corp")
            .phoneNumber("123456789")
            .email("john.doe@example.com")
            .address(addressDTO)
            .build();
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
            .facilitatorUserId("Facilitator User Id")
            .energyEprFactor("Energy Factor")
            .location(location)
            .sectorAssociationContact(contact)
            .build();
    }
}
