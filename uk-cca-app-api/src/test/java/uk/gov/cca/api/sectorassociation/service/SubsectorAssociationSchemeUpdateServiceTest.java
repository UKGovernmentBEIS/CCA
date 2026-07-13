package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.TargetCommitment;
import uk.gov.cca.api.sectorassociation.domain.TargetSet;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentsUpdateDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentUpdateDTO;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationSchemeRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubsectorAssociationSchemeUpdateServiceTest {

    @InjectMocks
    private SubsectorAssociationSchemeUpdateService service;

    @Mock
    private SubsectorAssociationSchemeRepository subsectorAssociationSchemeRepository;

    @Test
    void updateSubsectorAssociationSchemeTargetCommitments() {
        final Long sectorAssociationId = 1L;
        final Long subsectorAssociationSchemeId = 2L;
        final SubsectorAssociationScheme subsectorAssociationScheme =
                getSubsectorAssociationScheme(sectorAssociationId, subsectorAssociationSchemeId);
        final TargetCommitmentsUpdateDTO sectorAssociationTargetCommitmentsUpdateDTO = TargetCommitmentsUpdateDTO.builder()
                .targetCommitments(List.of(
                        TargetCommitmentUpdateDTO.builder()
                                .id(1L)
                                .targetImprovement(BigDecimal.valueOf(58.888))
                                .build(),
                        TargetCommitmentUpdateDTO.builder()
                                .id(2L)
                                .targetImprovement(BigDecimal.valueOf(25.999))
                                .build()))
                .build();

        when(subsectorAssociationSchemeRepository.findById(subsectorAssociationSchemeId)).thenReturn(Optional.of(subsectorAssociationScheme));

        // invoke
        service.updateSubsectorAssociationSchemeTargetCommitments(subsectorAssociationSchemeId, sectorAssociationTargetCommitmentsUpdateDTO);

        // verify
        List<BigDecimal> improvements = subsectorAssociationScheme.getTargetSet().getTargetCommitments().stream()
                .map(TargetCommitment::getTargetImprovement)
                .toList();
        assertThat(improvements).containsExactlyInAnyOrder(BigDecimal.valueOf(0.58888), BigDecimal.valueOf(0.25999));
        verify(subsectorAssociationSchemeRepository, times(1)).findById(subsectorAssociationSchemeId);
    }

    private SubsectorAssociationScheme getSubsectorAssociationScheme(Long sectorAssociationId, Long subsectorAssociationSchemeId) {
        return SubsectorAssociationScheme.builder()
                .id(subsectorAssociationSchemeId)
                .subsectorAssociation(SubsectorAssociation.builder()
                        .sectorAssociation(SectorAssociation.builder()
                                .id(sectorAssociationId)
                                .build())
                        .build())
                .schemeVersion(SchemeVersion.CCA_3)
                .targetSet(TargetSet.builder()
                        .targetCurrencyType("Novem")
                        .energyOrCarbonUnit("kWh")
                        .targetCommitments(List.of(TargetCommitment.builder()
                                        .id(1L)
                                        .targetImprovement(BigDecimal.valueOf(15.000))
                                        .targetPeriod("TP7")
                                        .build(),
                                TargetCommitment.builder()
                                        .id(2L)
                                        .targetImprovement(BigDecimal.valueOf(20.000))
                                        .targetPeriod("TP8")
                                        .build()))
                        .build())
                .build();
    }
}
