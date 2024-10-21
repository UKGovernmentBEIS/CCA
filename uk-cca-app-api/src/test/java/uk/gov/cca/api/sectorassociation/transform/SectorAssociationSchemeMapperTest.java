package uk.gov.cca.api.sectorassociation.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.TargetCommitment;
import uk.gov.cca.api.sectorassociation.domain.TargetSet;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDocumentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.FileStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SectorAssociationSchemeMapperTest {

    private static final SectorAssociationSchemeMapper sectorAssociationSchemeMapper = Mappers.getMapper(SectorAssociationSchemeMapper.class);

    @Test
    void test_toSubsectorAssociationSchemeDTO() {
    	LocalDate umaDate = LocalDate.now();
        SectorAssociationSchemeDocument umbrellaAgreement = SectorAssociationSchemeDocument.builder()
                .uuid("test")
                .id(1L)
                .fileName("umbrellaAgreement")
                .fileType(".pdf")
                .status(FileStatus.SUBMITTED)
                .fileSize(1)
                .createdBy("test user")
                .build();


        TargetCommitment targetCommitment = TargetCommitment.builder()
                .targetImprovement(BigDecimal.valueOf(19.000))
                .targetPeriod("2013-2014")
                .build();

        TargetSet sectorTargetSet = TargetSet.builder()
                .targetCurrencyType("Novem")
                .energyOrCarbonUnit("kWh")
                .targetCommitments(Collections.singletonList(targetCommitment))
                .build();

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

        SectorAssociation sectorAssociation = SectorAssociation.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .legalName("Some Association Legal")
                .name("Some Association")
                .acronym("SA")
                .facilitatorUserId("Facilitator User Id")
                .energyEprFactor("Energy Factor")
                .location(location)
                .sectorAssociationContact(contact)
                .build();

        TargetCommitment subsectorTargetCommitment = TargetCommitment.builder()
                .targetImprovement(BigDecimal.valueOf(19.000))
                .targetPeriod("2013-2014")
                .build();

        TargetSet subsectorTargetSet = TargetSet.builder()
                .targetCurrencyType("Novem")
                .energyOrCarbonUnit("kWh")
                .targetCommitments(Collections.singletonList(subsectorTargetCommitment))
                .build();

        SubsectorAssociation subsectorAssociation = SubsectorAssociation.builder()
                .name("name")
                .build();

        SubsectorAssociationScheme subsectorAssociationScheme = SubsectorAssociationScheme.builder()
                .subsectorAssociation(subsectorAssociation)
                .targetSet(subsectorTargetSet)
                .build();

        SectorAssociationScheme sectorAssociationScheme = SectorAssociationScheme.builder()
                .umbrellaAgreement(umbrellaAgreement)
                .sectorAssociation(sectorAssociation)
                .targetSet(sectorTargetSet)
                .subsectorAssociationSchemes(Collections.singletonList(subsectorAssociationScheme))
                .sectorDefinition("This is the sector definition")
                .umaDate(umaDate)
                .build();

        SectorAssociationSchemeDTO dto = sectorAssociationSchemeMapper.sectorAssociationSchemeToDTO(sectorAssociationScheme);

        // Assertions for SectorAssociationSchemeDocumentDTO within SectorAssociationSchemeDTO
        assertEquals(sectorAssociationScheme.getUmbrellaAgreement().getId(), dto.getUmbrellaAgreement().getId());
        assertEquals(sectorAssociationScheme.getUmbrellaAgreement().getFileName(), dto.getUmbrellaAgreement().getFileName());
        assertEquals(sectorAssociationScheme.getUmbrellaAgreement().getFileSize(), dto.getUmbrellaAgreement().getFileSize());
        assertEquals(sectorAssociationScheme.getUmbrellaAgreement().getFileType(), dto.getUmbrellaAgreement().getFileType());

        // Assertions for TargetSetDTO within SectorAssociationSchemeDTO
        assertEquals(sectorAssociationScheme.getTargetSet().getEnergyOrCarbonUnit(), dto.getTargetSet().getEnergyOrCarbonUnit());
        assertEquals(sectorAssociationScheme.getTargetSet().getTargetCurrencyType(), dto.getTargetSet().getTargetCurrencyType());
        assertEquals(sectorAssociationScheme.getTargetSet().getTargetCommitments().size(), dto.getTargetSet().getTargetCommitments().size());
        assertEquals(sectorAssociationScheme.getTargetSet().getTargetCommitments().get(0).getTargetPeriod(),
                dto.getTargetSet().getTargetCommitments().get(0).getTargetPeriod());
        assertEquals(sectorAssociationScheme.getTargetSet().getTargetCommitments().get(0).getTargetImprovement(),
                dto.getTargetSet().getTargetCommitments().get(0).getTargetImprovement());

        // Assertions for SubsectorAssociationSchemeInfoDTO within SectorAssociationSchemeDTO
        assertEquals(sectorAssociationScheme.getSubsectorAssociationSchemes().size(), dto.getSubsectorAssociationSchemes().size());
        assertEquals(sectorAssociationScheme.getSubsectorAssociationSchemes().get(0).getSubsectorAssociation().getName(),
                dto.getSubsectorAssociationSchemes().get(0).getSubsectorAssociation().getName());
        
        // Assertions for umaDate and sectorDefiniton
        assertEquals(umaDate, dto.getUmaDate());
        assertEquals(sectorAssociationScheme.getSectorDefinition(), dto.getSectorDefinition());
    }

    @Test
    void test_toSubsectorAssociationScheme() {
    	LocalDate umaDate = LocalDate.now();
        SectorAssociationSchemeDocumentDTO umbrellaAgreement = SectorAssociationSchemeDocumentDTO.builder()
                .id(1)
                .uuid("test")
                .fileName("umbrellaAgreement")
                .fileType(".pdf")
                .fileSize(1)
                .build();

        TargetCommitmentDTO targetCommitment = TargetCommitmentDTO.builder()
                .targetImprovement(BigDecimal.valueOf(19.000))
                .targetPeriod("2013-2014")
                .build();

        TargetSetDTO sectorTargetSet = TargetSetDTO.builder()
                .targetCurrencyType("Novem")
                .energyOrCarbonUnit("kWh")
                .targetCommitments(Collections.singletonList(targetCommitment))
                .build();

        SubsectorAssociationDTO subsectorAssociation = SubsectorAssociationDTO.builder()
                .name("name")
                .build();

        SubsectorAssociationSchemeInfoDTO subsectorAssociationScheme = SubsectorAssociationSchemeInfoDTO.builder()
                .subsectorAssociation(subsectorAssociation)
                .build();

        SectorAssociationSchemeDTO dto = SectorAssociationSchemeDTO.builder()
                .umbrellaAgreement(umbrellaAgreement)
                .subsectorAssociationSchemes(Collections.singletonList(subsectorAssociationScheme))
                .targetSet(sectorTargetSet)
                .sectorDefinition("definition")
                .umaDate(umaDate)
                .build();

        SectorAssociationScheme sectorAssociationScheme =
                sectorAssociationSchemeMapper.toSectorAssociationScheme(dto);

        // Assertions for SectorAssociationSchemeDocumentDTO within SectorAssociationSchemeDTO
        assertEquals(sectorAssociationScheme.getUmbrellaAgreement().getId(), dto.getUmbrellaAgreement().getId());
        assertEquals(sectorAssociationScheme.getUmbrellaAgreement().getFileName(), dto.getUmbrellaAgreement().getFileName());
        assertEquals(sectorAssociationScheme.getUmbrellaAgreement().getFileSize(), dto.getUmbrellaAgreement().getFileSize());
        assertEquals(sectorAssociationScheme.getUmbrellaAgreement().getFileType(), dto.getUmbrellaAgreement().getFileType());

        // Assertions for TargetSetDTO within SectorAssociationSchemeDTO
        assertEquals(sectorAssociationScheme.getTargetSet().getEnergyOrCarbonUnit(), dto.getTargetSet().getEnergyOrCarbonUnit());
        assertEquals(sectorAssociationScheme.getTargetSet().getTargetCurrencyType(), dto.getTargetSet().getTargetCurrencyType());
        assertEquals(sectorAssociationScheme.getTargetSet().getTargetCommitments().size(), dto.getTargetSet().getTargetCommitments().size());
        assertEquals(sectorAssociationScheme.getTargetSet().getTargetCommitments().get(0).getTargetPeriod(),
                dto.getTargetSet().getTargetCommitments().get(0).getTargetPeriod());
        assertEquals(sectorAssociationScheme.getTargetSet().getTargetCommitments().get(0).getTargetImprovement(),
                dto.getTargetSet().getTargetCommitments().get(0).getTargetImprovement());

        // Assertions for SubsectorAssociationSchemeInfoDTO within SectorAssociationSchemeDTO
        assertEquals(sectorAssociationScheme.getSubsectorAssociationSchemes().size(), dto.getSubsectorAssociationSchemes().size());
        assertEquals(sectorAssociationScheme.getSubsectorAssociationSchemes().get(0).getSubsectorAssociation().getName(),
                dto.getSubsectorAssociationSchemes().get(0).getSubsectorAssociation().getName());
        
        // Assertions for umaDate and sectorDefiniton
        assertEquals(umaDate, sectorAssociationScheme.getUmaDate());
        assertEquals(dto.getSectorDefinition(), sectorAssociationScheme.getSectorDefinition());
    }
}
