package uk.gov.cca.api.sectorassociation.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.TargetCommitment;
import uk.gov.cca.api.sectorassociation.domain.TargetSet;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetSetDTO;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubsectorAssociationSchemeMapperTest {

    private static final SubsectorAssociationSchemeMapper subsectorAssociationSchemeMapper = Mappers.getMapper(SubsectorAssociationSchemeMapper.class);

    @Test
    void test_toSubsectorAssociationSchemeDTO() {
        TargetCommitment targetCommitment = TargetCommitment.builder()
                .targetImprovement(BigDecimal.valueOf(19.000))
                .targetPeriod("2013-2014")
                .build();

        TargetSet subsectorTargetSet = TargetSet.builder()
                .targetCurrencyType("Novem")
                .energyOrCarbonUnit("kWh")
                .targetCommitments(Collections.singletonList(targetCommitment))
                .build();

        SubsectorAssociation subsectorAssociation = SubsectorAssociation.builder()
                .name("name")
                .build();

        SubsectorAssociationScheme subsectorAssociationScheme = SubsectorAssociationScheme.builder()
                .subsectorAssociation(subsectorAssociation)
                .targetSet(subsectorTargetSet)
                .build();

        SubsectorAssociationSchemeDTO dto = subsectorAssociationSchemeMapper.toSubsectorAssociationSchemeDTO(subsectorAssociationScheme);

        // Assertions for TargetSetDTO within SubsectorAssociationSchemeDTO
        assertEquals(subsectorAssociationScheme.getTargetSet().getEnergyOrCarbonUnit(), dto.getTargetSet().getEnergyOrCarbonUnit());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCurrencyType(), dto.getTargetSet().getTargetCurrencyType());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCommitments().size(), dto.getTargetSet().getTargetCommitments().size());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCommitments().get(0).getTargetPeriod(),
                dto.getTargetSet().getTargetCommitments().get(0).getTargetPeriod());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCommitments().get(0).getTargetImprovement(),
                dto.getTargetSet().getTargetCommitments().get(0).getTargetImprovement());
    }

    @Test
    void test_toSubsectorAssociationScheme() {
        TargetCommitmentDTO targetCommitmentDTO = TargetCommitmentDTO.builder()
                .targetImprovement(BigDecimal.valueOf(19.000))
                .targetPeriod("2013-2014")
                .build();

        TargetSetDTO targetSetDTO = TargetSetDTO.builder()
                .targetCurrencyType("Novem")
                .energyOrCarbonUnit("kWh")
                .targetCommitments(Collections.singletonList(targetCommitmentDTO))
                .build();

        SubsectorAssociationSchemeDTO subsectorAssociationSchemeDTO = SubsectorAssociationSchemeDTO.builder()
                .targetSet(targetSetDTO)
                .build();

        SubsectorAssociationScheme subsectorAssociationScheme =
                subsectorAssociationSchemeMapper.toSubsectorAssociationScheme(subsectorAssociationSchemeDTO);

        // Assertions for TargetSetDTO within SubsectorAssociationSchemeDTO
        assertEquals(subsectorAssociationScheme.getTargetSet().getEnergyOrCarbonUnit(), subsectorAssociationSchemeDTO.getTargetSet().getEnergyOrCarbonUnit());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCurrencyType(), subsectorAssociationSchemeDTO.getTargetSet().getTargetCurrencyType());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCommitments().size(), subsectorAssociationSchemeDTO.getTargetSet().getTargetCommitments().size());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCommitments().get(0).getTargetPeriod(),
                subsectorAssociationSchemeDTO.getTargetSet().getTargetCommitments().get(0).getTargetPeriod());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCommitments().get(0).getTargetImprovement(),
                subsectorAssociationSchemeDTO.getTargetSet().getTargetCommitments().get(0).getTargetImprovement());
    }

}
