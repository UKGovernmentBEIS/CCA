package uk.gov.cca.api.sectorassociation.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.TargetCommitment;
import uk.gov.cca.api.sectorassociation.domain.TargetSet;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeInfo;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SubsectorAssociationSchemeMapperTest {

    private static final SubsectorAssociationSchemeMapper subsectorAssociationSchemeMapper = Mappers.getMapper(SubsectorAssociationSchemeMapper.class);

    @Test
    void test_toSubsectorAssociationSchemeInfo() {
        TargetCommitment targetCommitment = TargetCommitment.builder()
                .targetImprovement(BigDecimal.valueOf(19.000))
                .targetPeriod("2013-2014")
                .build();

        TargetSet subsectorTargetSet = TargetSet.builder()
                .targetCurrencyType("Novem")
                .energyOrCarbonUnit("kWh")
                .targetCommitments(Collections.singletonList(targetCommitment))
                .throughputUnit("tonne")
                .build();

        SubsectorAssociation subsectorAssociation = SubsectorAssociation.builder()
                .name("name")
                .build();

        SubsectorAssociationScheme subsectorAssociationScheme = SubsectorAssociationScheme.builder()
                .subsectorAssociation(subsectorAssociation)
                .targetSet(subsectorTargetSet)
                .build();

        SubsectorAssociationSchemeInfo dto = subsectorAssociationSchemeMapper.toSubsectorAssociationSchemeInfo(subsectorAssociationScheme);

        // Assertions for TargetSetDTO within SubsectorAssociationSchemeDTO
        assertEquals(subsectorAssociationScheme.getTargetSet().getEnergyOrCarbonUnit(), dto.getTargetSet().getEnergyOrCarbonUnit());
        assertEquals(subsectorAssociationScheme.getTargetSet().getThroughputUnit(), dto.getTargetSet().getThroughputUnit());
    }

    @Test
    void test_toSubsectorAssociationSchemeDTO() {
        SubsectorAssociation subsectorAssociation = SubsectorAssociation.builder()
                .name("name")
                .build();

        TargetCommitment targetCommitment = TargetCommitment.builder()
                .targetImprovement(BigDecimal.valueOf(19.000))
                .targetPeriod("2013-2014")
                .build();

        TargetSet subsectorTargetSet = TargetSet.builder()
                .targetCurrencyType("Novem")
                .energyOrCarbonUnit("kWh")
                .targetCommitments(Collections.singletonList(targetCommitment))
                .throughputUnit("tonne")
                .build();

        SubsectorAssociationScheme subsectorAssociationScheme = SubsectorAssociationScheme.builder()
                .subsectorAssociation(subsectorAssociation)
                .targetSet(subsectorTargetSet)
                .schemeVersion(SchemeVersion.CCA_2)
                .build();

        SubsectorAssociationSchemeDTO subsectorAssociationSchemeDTO =
                subsectorAssociationSchemeMapper.toSubsectorAssociationSchemeDTO(subsectorAssociationScheme, true);

        // Assertions for TargetSetDTO within SubsectorAssociationSchemeDTO
        assertFalse(subsectorAssociationSchemeDTO.isEditable());
        assertEquals(subsectorAssociationScheme.getTargetSet().getEnergyOrCarbonUnit(), subsectorAssociationSchemeDTO.getTargetSet().getEnergyOrCarbonUnit());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCurrencyType(), subsectorAssociationSchemeDTO.getTargetSet().getTargetCurrencyType());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCommitments().size(), subsectorAssociationSchemeDTO.getTargetSet().getTargetCommitments().size());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCommitments().get(0).getTargetPeriod(),
                subsectorAssociationSchemeDTO.getTargetSet().getTargetCommitments().get(0).getTargetPeriod());
        assertEquals(subsectorAssociationScheme.getTargetSet().getTargetCommitments().get(0).getTargetImprovement(),
                subsectorAssociationSchemeDTO.getTargetSet().getTargetCommitments().get(0).getTargetImprovement());
    }

}
