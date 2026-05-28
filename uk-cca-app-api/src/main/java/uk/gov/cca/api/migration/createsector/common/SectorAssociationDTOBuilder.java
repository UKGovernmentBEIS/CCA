package uk.gov.cca.api.migration.createsector.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.MigrationUtil;
import uk.gov.cca.api.migration.createsector.cca2.SectorAssociationVO;
import uk.gov.cca.api.migration.createsector.cca2.SubSectorAssociationVO;
import uk.gov.cca.api.migration.createsector.cca2.TargetSetVO;
import uk.gov.cca.api.migration.createsector.cca3.Cca3SectorAssociationVO;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDocumentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemesDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class SectorAssociationDTOBuilder {
    public SectorAssociationDTO constructSectorAssociation(SectorAssociationSource vo) {
        return SectorAssociationDTO.builder()
                .sectorAssociationDetails(constructSectorDetails(vo))
                .sectorAssociationContact(constructSectorContact(vo)).build();
    }

    private SectorAssociationDetailsDTO constructSectorDetails(SectorAssociationSource vo) {
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
                .energyIntensiveOrEPR(vo.getEnergyIntensiveOrEPR() != null ? vo.getEnergyIntensiveOrEPR().name() : null)
                .noticeServiceAddress(noticeServiceAddressDTO)
                .build();
    }

    private SectorAssociationContactDTO constructSectorContact(SectorAssociationSource vo) {
        AddressDTO sectorContactAddressDTO = AddressDTO.builder()
                .line1(vo.getSectorContactAddressLine1())
                .line2(vo.getSectorContactAddressLine2())
                .city(vo.getSectorContactAddressCity())
                .county(vo.getSectorContactAddressCounty())
                .postcode(vo.getSectorContactAddressPostcode()).build();

        return SectorAssociationContactDTO.builder()
                .title(vo.getSectorContactTitle())
                .firstName(vo.getSectorContactFirstName())
                .organisationName(vo.getSectorContactOrganisationName())
                .jobTitle(vo.getSectorContactJobTitle())
                .lastName(vo.getSectorContactLastName())
                .phoneNumber(vo.getSectorContactPhoneNumber())
                .email(vo.getSectorContactEmail())
                .address(sectorContactAddressDTO).build();
    }

    public SectorAssociationSchemeDTO constructSectorAssociationScheme(SectorAssociationSource vo, boolean hasSubsectors, SchemeVersion schemeVersion) {
        SectorAssociationSchemeDocumentDTO umbrellaAgreementDTO = SectorAssociationSchemeDocumentDTO.builder()
                .uuid(UUID.randomUUID().toString())
                .fileName(constructUmbrellaDocumentFilename(vo, schemeVersion))
                .build();
        
        SectorAssociationSchemeDTO sectorSchemeDTO = SectorAssociationSchemeDTO.builder()
                .id(SchemeVersion.CCA_2.equals(schemeVersion) ? ((SectorAssociationVO) vo).getOriginalSectorId() : null)
                .umbrellaAgreement(umbrellaAgreementDTO)
                .umaDate(vo.getUmaDate())
                .sectorDefinition(vo.getSectorDefinition())
                .schemeVersion(schemeVersion)
                .build();

        if (!hasSubsectors) {
        	if (SchemeVersion.CCA_2.equals(schemeVersion)) {
        		sectorSchemeDTO.setTargetSet(constructCca2TargetSet(((SectorAssociationVO) vo).getTargetSet()));
        	} else if (SchemeVersion.CCA_3.equals(schemeVersion)) {
				sectorSchemeDTO.setTargetSet(constructCca3TargetSet((Cca3SectorAssociationVO) vo));
			}
        }
        
        return sectorSchemeDTO;
    }

	public SubsectorAssociationSchemesDTO constructSubsectorAssociationSchemeDTO(SubSectorAssociationVO vo) {
        return SubsectorAssociationSchemesDTO.builder()
                .name(vo.getName())
                .subsectorAssociationSchemeMap(Map.of(SchemeVersion.CCA_2, SubsectorAssociationSchemeDTO.builder()
                		.targetSet(constructCca2TargetSet(vo.getTargetSet()))
                		.build()))
                .build();
    }
	
	private TargetSetDTO constructCca3TargetSet(Cca3SectorAssociationVO vo) {
		
		return TargetSetDTO.builder()
                .id(1L)
                .targetCurrencyType(AgreementCompositionType.NOVEM.getDescription())
                .energyOrCarbonUnit(vo.getEnergyCarbonUnit().getUnit())
                .targetCommitments(List.of(
                        constructTargetCommitment("TP7 (2026)", 
                        		vo.getTp7SectorCommitment().divide(new BigDecimal("100"), 3, RoundingMode.HALF_DOWN)),
                        constructTargetCommitment("TP8 (2027-2028)", 
                        		vo.getTp8SectorCommitment().divide(new BigDecimal("100"), 3, RoundingMode.HALF_DOWN)),
                        constructTargetCommitment("TP9 (2029-2030)", 
                        		vo.getTp9SectorCommitment().divide(new BigDecimal("100"), 3, RoundingMode.HALF_DOWN))))
                .build();
	}

    private TargetSetDTO constructCca2TargetSet(TargetSetVO vo) {
    	
    	MeasurementType measurementType = MigrationUtil.getMeasurementType(vo.getEnergyCarbonUnit());

        return TargetSetDTO.builder()
                .id(1L)
                .targetCurrencyType(vo.getTargetType())
                .throughputUnit(vo.getThroughputUnit())
                .energyOrCarbonUnit(measurementType != null ? measurementType.getUnit() : null)
                .targetCommitments(List.of(
                        constructTargetCommitment("TP1 (2013-2014)", vo.getTp1SectorCommitment()),
                        constructTargetCommitment("TP2 (2015-2016)", vo.getTp2SectorCommitment()),
                        constructTargetCommitment("TP3 (2017-2018)", vo.getTp3SectorCommitment()),
                        constructTargetCommitment("TP4 (2019-2020)", vo.getTp4SectorCommitment()),
                        constructTargetCommitment("TP5 (2021-2022)", vo.getTp5SectorCommitment()),
                        constructTargetCommitment("TP6 (2024)", vo.getTp6SectorCommitment())))
                .build();
    }

    private TargetCommitmentDTO constructTargetCommitment(String targetPeriod, BigDecimal targetImprovement) {
        return TargetCommitmentDTO.builder()
                .targetPeriod(targetPeriod)
                .targetImprovement(targetImprovement).build();
    }
    
    private String constructUmbrellaDocumentFilename(SectorAssociationSource vo, SchemeVersion schemeVersion) {
		return "Umbrella Agreement " + schemeVersion.getDescription() + " (" + vo.getAcronym() + ").pdf";
	}
}
