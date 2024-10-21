package uk.gov.cca.api.sectorassociation.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@SpringBootTest
@ContextConfiguration(classes = {SectorAssociationContactMapperImpl.class, LocationMapperImpl.class,
    SectorAssociationMapperImpl.class})
class SectorAssociationMapperTest {


    @Autowired
    private SectorAssociationMapper sectorAssociationMapper;

    @Test
    void test_toSectorAssociationDTO() {

        Location location = Location.builder()
            .postcode("12345")
            .line1("123 Main St")
            .line2("124 Second St")
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

        SectorAssociation association =  SectorAssociation.builder()
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .legalName("Some Association Legal")
            .name("Some Association")
            .acronym("SA")
            .energyEprFactor("Energy Factor")
            .location(location)
            .sectorAssociationContact(contact)
            .build();

        SectorAssociationDTO dto = sectorAssociationMapper.toSectorAssociationDTO(association);

        // Assertions for SectorAssociationDetailsDTO within SectorAssociationDTO
        assertEquals(association.getCompetentAuthority(), dto.getSectorAssociationDetails().getCompetentAuthority());
        assertEquals(association.getName(), dto.getSectorAssociationDetails().getCommonName());
        assertEquals(association.getAcronym(), dto.getSectorAssociationDetails().getAcronym());
        assertEquals(association.getLegalName(), dto.getSectorAssociationDetails().getLegalName());
        assertEquals(association.getEnergyEprFactor(), dto.getSectorAssociationDetails().getEnergyIntensiveOrEPR());

        // Assertions for AddressDTO within SectorAssociationDetailsDTO
        AddressDTO addressDTO = dto.getSectorAssociationDetails().getNoticeServiceAddress();
        assertEquals(location.getLine1(), addressDTO.getLine1());
        assertEquals(location.getLine2(), addressDTO.getLine2());
        assertEquals(location.getCity(), addressDTO.getCity());
        assertEquals(location.getCounty(), addressDTO.getCounty());
        assertEquals(location.getPostcode(), addressDTO.getPostcode());

        // Assertions for SectorAssociationContactDTO within SectorAssociationDTO
        SectorAssociationContactDTO contactDTO = dto.getSectorAssociationContact();
        assertEquals(contact.getTitle(), contactDTO.getTitle());
        assertEquals(contact.getFirstName(), contactDTO.getFirstName());
        assertEquals(contact.getLastName(), contactDTO.getLastName());
        assertEquals(contact.getJobTitle(), contactDTO.getJobTitle());
        assertEquals(contact.getOrganisationName(), contactDTO.getOrganisationName());
        assertEquals(contact.getPhoneNumber(), contactDTO.getPhoneNumber());
        assertEquals(contact.getEmail(), contactDTO.getEmail());

        // Assertions for AddressDTO within SectorAssociationContactDTO
        addressDTO = contactDTO.getAddress();
        assertEquals(location.getLine1(), addressDTO.getLine1());
        assertEquals(location.getLine2(), addressDTO.getLine2());
        assertEquals(location.getCity(), addressDTO.getCity());
        assertEquals(location.getCounty(), addressDTO.getCounty());
        assertEquals(location.getPostcode(), addressDTO.getPostcode());
    }

    @Test
    void test_toSectorAssociation() {

        AddressDTO addressDTO = AddressDTO.builder()
            .postcode("12345")
            .line1("123 Main St")
            .line2("124 Second St")
            .city("Springfield")
            .county("CountyName")
            .build();

        SectorAssociationContactDTO sectorAssociationContactDTO = SectorAssociationContactDTO.builder()
            .title("Mr.")
            .firstName("John")
            .lastName("Doe")
            .jobTitle("Director")
            .organisationName("Acme Corp")
            .phoneNumber("123456789")
            .email("john.doe@example.com")
            .address(addressDTO)
            .build();

        SectorAssociationDetailsDTO sectorAssociationDetailsDTO = SectorAssociationDetailsDTO.builder()
            .legalName("Some Association Legal")
            .commonName("Some Association")
            .noticeServiceAddress(addressDTO)
            .build();

        SectorAssociationDTO dto = SectorAssociationDTO.builder()
            .sectorAssociationDetails(sectorAssociationDetailsDTO)
            .sectorAssociationContact(sectorAssociationContactDTO)
            .build();

        SectorAssociation association = sectorAssociationMapper.toSectorAssociation(dto);

        // Assertions for SectorAssociationDetailsDTO within SectorAssociationDTO
        assertEquals(association.getCompetentAuthority(), dto.getSectorAssociationDetails().getCompetentAuthority());
        assertEquals(association.getName(), dto.getSectorAssociationDetails().getCommonName());
        assertEquals(association.getAcronym(), dto.getSectorAssociationDetails().getAcronym());
        assertEquals(association.getLegalName(), dto.getSectorAssociationDetails().getLegalName());
        assertEquals(association.getEnergyEprFactor(), dto.getSectorAssociationDetails().getEnergyIntensiveOrEPR());

        // Assertions for AddressDTO within SectorAssociationDetailsDTO
        Location location = association.getLocation();
        assertEquals(location.getLine1(), addressDTO.getLine1());
        assertEquals(location.getLine2(), addressDTO.getLine2());
        assertEquals(location.getCity(), addressDTO.getCity());
        assertEquals(location.getCounty(), addressDTO.getCounty());
        assertEquals(location.getPostcode(), addressDTO.getPostcode());

        // Assertions for SectorAssociationContactDTO within SectorAssociationDTO
        SectorAssociationContact contact = association.getSectorAssociationContact();
        assertEquals(contact.getTitle(), sectorAssociationContactDTO.getTitle());
        assertEquals(contact.getFirstName(), sectorAssociationContactDTO.getFirstName());
        assertEquals(contact.getLastName(), sectorAssociationContactDTO.getLastName());
        assertEquals(contact.getJobTitle(), sectorAssociationContactDTO.getJobTitle());
        assertEquals(contact.getOrganisationName(), sectorAssociationContactDTO.getOrganisationName());
        assertEquals(contact.getPhoneNumber(), sectorAssociationContactDTO.getPhoneNumber());
        assertEquals(contact.getEmail(), sectorAssociationContactDTO.getEmail());

        // Assertions for AddressDTO within SectorAssociationContactDTO
        location = association.getSectorAssociationContact().getLocation();
        assertEquals(location.getLine1(), addressDTO.getLine1());
        assertEquals(location.getLine2(), addressDTO.getLine2());
        assertEquals(location.getCity(), addressDTO.getCity());
        assertEquals(location.getCounty(), addressDTO.getCounty());
        assertEquals(location.getPostcode(), addressDTO.getPostcode());
    }
}
