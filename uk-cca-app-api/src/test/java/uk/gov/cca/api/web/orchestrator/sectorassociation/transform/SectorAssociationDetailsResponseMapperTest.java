package uk.gov.cca.api.web.orchestrator.sectorassociation.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.user.core.domain.UserBasicInfoDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.dto.SectorAssociationDetailsResponseDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SectorAssociationDetailsResponseMapperTest {

    private SectorAssociationDetailsResponseMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(SectorAssociationDetailsResponseMapper.class);
    }

    @Test
    public void testMapping() {
        SectorAssociationDetailsDTO dto = SectorAssociationDetailsDTO.builder()
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .commonName("Common Name")
            .acronym("ACR")
            .legalName("Legal Name")
            .facilitatorUserId("Facilitator Name")
            .noticeServiceAddress(AddressDTO.builder()
                .line1("123 Street")
                .line2(null)
                .city("Anytown")
                .county("Anycounty")
                .postcode("12345")
                .build())
            .energyIntensiveOrEPR("Energy Intensive")
            .build();

        UserBasicInfoDTO userBasicInfoDTO = UserBasicInfoDTO.builder()
            .firstName("firstName")
            .lastName("lastName")
            .build();

        SectorAssociationDetailsResponseDTO result = mapper.toSectorAssociationResponseDTO(dto, userBasicInfoDTO);

        assertEquals(dto.getCompetentAuthority(), result.getCompetentAuthority());
        assertEquals(dto.getCommonName(), result.getCommonName());
        assertEquals(dto.getAcronym(), result.getAcronym());
        assertEquals(dto.getLegalName(), result.getLegalName());
        assertEquals(result.getFacilitator(), userBasicInfoDTO);
        assertEquals(dto.getNoticeServiceAddress().getLine1(), result.getNoticeServiceAddress().getLine1());
        assertEquals(dto.getNoticeServiceAddress().getCity(), result.getNoticeServiceAddress().getCity());
        assertEquals(dto.getNoticeServiceAddress().getPostcode(), result.getNoticeServiceAddress().getPostcode());
        assertEquals(dto.getEnergyIntensiveOrEPR(), result.getEnergyIntensiveOrEPR());
    }
}
