package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.domain.TargetCommitment;
import uk.gov.cca.api.sectorassociation.domain.TargetSet;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentsUpdateDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDetailsUpdateDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentUpdateDTO;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorAssociationSchemeUpdateServiceTest {

    @InjectMocks
    private SectorAssociationSchemeUpdateService service;

    @Mock
    private SectorAssociationSchemeRepository sectorAssociationSchemeRepository;

    @Mock
    private SectorAssociationSchemeDocumentService sectorAssociationSchemeDocumentService;


    @Test
    void updateSectorAssociationSchemeDetails() {
        final Long sectorAssociationId = 1L;
        final Long sectorAssociationSchemeId = 1L;
        final SectorAssociationScheme sectorAssociationScheme =
                getSectorAssociationScheme(sectorAssociationId, sectorAssociationSchemeId);
        final String newSectorDef = "new scope def";
        final String newUuid = "07bde92d-e259-4e38-adeb-1c23915a33ed";
        final LocalDate newUmaDate = LocalDate.of(2026, 2, 2);
        final SectorAssociationSchemeDetailsUpdateDTO sectorAssociationSchemeDetailsUpdateDTO = SectorAssociationSchemeDetailsUpdateDTO.builder()
                .umbrellaAgreementUuid(newUuid)
                .sectorDefinition(newSectorDef)
                .umaDate(newUmaDate)
                .build();
        final SectorAssociationSchemeDocument newSectorAssociationSchemeDocument = SectorAssociationSchemeDocument.builder()
                .fileName("file")
                .createdBy("createdBy")
                .id(2L)
                .fileSize(100L)
                .fileContent("fileContent".getBytes())
                .uuid(newUuid)
                .build();

        when(sectorAssociationSchemeRepository.findById(sectorAssociationSchemeId)).thenReturn(Optional.of(sectorAssociationScheme));
        when(sectorAssociationSchemeDocumentService.getSectorAssociationSchemeDocumentByUuid(newUuid)).thenReturn(newSectorAssociationSchemeDocument);
        // invoke
        service.updateSectorAssociationSchemeDetails(sectorAssociationSchemeId, sectorAssociationSchemeDetailsUpdateDTO);

        // verify
        assertThat(sectorAssociationScheme.getSectorDefinition()).isEqualTo(newSectorDef);
        assertThat(sectorAssociationScheme.getUmbrellaAgreement().getUuid()).isEqualTo(newUuid);
        assertThat(sectorAssociationScheme.getUmaDate()).isEqualTo(newUmaDate);
        verify(sectorAssociationSchemeRepository, times(1)).findById(sectorAssociationSchemeId);
        verify(sectorAssociationSchemeDocumentService, times(1)).cleanUpUnusedFiles();
    }

    @Test
    void updateSectorAssociationSchemeTargetCommitments() {
        final Long sectorAssociationId = 1L;
        final Long sectorAssociationSchemeId = 1L;
        final SectorAssociationScheme sectorAssociationScheme =
                getSectorAssociationScheme(sectorAssociationId, sectorAssociationSchemeId);
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

        when(sectorAssociationSchemeRepository.findById(sectorAssociationSchemeId)).thenReturn(Optional.of(sectorAssociationScheme));

        // invoke
        service.updateSectorAssociationSchemeTargetCommitments(sectorAssociationSchemeId, sectorAssociationTargetCommitmentsUpdateDTO);

        // verify
        List<BigDecimal> improvements = sectorAssociationScheme.getTargetSet().getTargetCommitments().stream()
                .map(TargetCommitment::getTargetImprovement)
                .toList();
        assertThat(improvements).containsExactlyInAnyOrder(BigDecimal.valueOf(0.58888), BigDecimal.valueOf(0.25999));
        verify(sectorAssociationSchemeRepository, times(1)).findById(sectorAssociationSchemeId);
    }

    private SectorAssociationScheme getSectorAssociationScheme(Long sectorAssociationId, Long sectorAssociationSchemeId) {
        return SectorAssociationScheme.builder()
                .id(sectorAssociationSchemeId)
                .sectorAssociation(SectorAssociation.builder()
                        .id(sectorAssociationId)
                        .build())
                .schemeVersion(SchemeVersion.CCA_3)
                .sectorDefinition("SectorDef")
                .umaDate(LocalDate.of(2026, 1, 1))
                .umbrellaAgreement(SectorAssociationSchemeDocument.builder()
                        .fileName("file")
                        .createdBy("createdBy")
                        .id(1L)
                        .fileSize(100L)
                        .fileContent("fileContent".getBytes())
                        .uuid("old-uuid")
                        .build())
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
