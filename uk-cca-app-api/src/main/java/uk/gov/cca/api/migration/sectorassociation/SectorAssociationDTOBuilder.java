package uk.gov.cca.api.migration.sectorassociation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDocumentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;


@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class SectorAssociationDTOBuilder {
    public SectorAssociationDTO constructSectorAssociation(SectorAssociationVO vo) {
        return SectorAssociationDTO.builder()
                .sectorAssociationDetails(constructSectorDetails(vo))
                .sectorAssociationContact(constructSectorContact(vo)).build();
    }

    private SectorAssociationDetailsDTO constructSectorDetails(SectorAssociationVO vo) {
        AddressDTO noticeServiceAddressDTO = AddressDTO.builder()
                .line1(vo.getLine1())
                .line2(vo.getLine2())
                .city(vo.getCity())
                .county(vo.getCounty())
                .postcode(vo.getPostcode()).build();

        return SectorAssociationDetailsDTO.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .acronym(vo.getAcronym())
                .commonName(vo.getCommonName())
                .legalName(vo.getLegalName())
                .energyIntensiveOrEPR(vo.getEnergyIntensiveOrEPR())
                .noticeServiceAddress(noticeServiceAddressDTO)
                .build();
    }

    private SectorAssociationContactDTO constructSectorContact(SectorAssociationVO vo) {
        AddressDTO sectorContactAddressDTO = AddressDTO.builder()
                .line1(vo.getSectorContactAddressLine1())
                .line2(vo.getSectorContactAddressLine2())
                .city(vo.getSectorContactAddressCity())
                .county(vo.getSectorContactAddressCounty())
                .postcode(vo.getSectorContactAddressPostcode()).build();

        return SectorAssociationContactDTO.builder()
                .title(vo.getSectorContactTitle())
                .firstName(vo.getSectorContactFirstName())
                .lastName(vo.getSectorContactLastName())
                .phoneNumber(vo.getSectorContactPhoneNumber())
                .email(vo.getSectorContactEmail())
                .address(sectorContactAddressDTO).build();
    }

    public SectorAssociationSchemeDTO constructSectorAssociationScheme(SectorAssociationVO vo, boolean hasSubsectors) {
        SectorAssociationSchemeDocumentDTO umbrellaAgreementDTO = SectorAssociationSchemeDocumentDTO.builder()
                .uuid(UUID.randomUUID().toString())
                .fileName("Umbrella Agreement (" + vo.getAcronym() + ").pdf")
                .build();
        
        SectorAssociationSchemeDTO sectorSchemeDTO = SectorAssociationSchemeDTO.builder()
                .id(vo.getOriginalSectorId())
                .umbrellaAgreement(umbrellaAgreementDTO)
                .umaDate(vo.getUmaDate())
                .sectorDefinition(vo.getSectorDefinition())
                .build();

        if (!hasSubsectors) {
            sectorSchemeDTO.setTargetSet(constructTargetSet(vo.getTargetSet()));
        }
        
        return sectorSchemeDTO;
    }
    
    public SubsectorAssociationSchemeDTO constructSubsectorAssociationSchemeDTO(SubSectorAssociationVO vo) {
        return SubsectorAssociationSchemeDTO.builder()
                .subsectorAssociation(SubsectorAssociationDTO.builder().name(vo.getName()).build())
                .targetSet(constructTargetSet(vo.getTargetSet())).build();
    }

    private TargetSetDTO constructTargetSet(TargetSetVO vo) {

        return TargetSetDTO.builder()
                .id(1L)
                .targetCurrencyType(vo.getTargetType())
                .throughputUnit(vo.getThroughputUnit())
                .energyOrCarbonUnit(vo.getEnergyCarbonUnit())
                .targetCommitments(List.of(
                        constructTargetCommitment("2013 - 2014", vo.getTp1SectorCommitment()),
                        constructTargetCommitment("2015 - 2016", vo.getTp2SectorCommitment()),
                        constructTargetCommitment("2017 - 2018", vo.getTp3SectorCommitment()),
                        constructTargetCommitment("2019 - 2020", vo.getTp4SectorCommitment()),
                        constructTargetCommitment("2021 - 2022", vo.getTp5SectorCommitment()),
                        constructTargetCommitment("2023 - 2024", vo.getTp6SectorCommitment())))
                .build();
    }

    private TargetCommitmentDTO constructTargetCommitment(String targetPeriod, BigDecimal targetImprovement) {
        return TargetCommitmentDTO.builder()
                .targetPeriod(targetPeriod)
                .targetImprovement(targetImprovement).build();
    }
}
