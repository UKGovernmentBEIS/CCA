package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationParseCsvServiceTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationParseCsvService service;

    @Mock
    private CcaFileAttachmentService ccaFileAttachmentService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Test
    void submitAndGetSourceFile() {

        when(ccaFileAttachmentService.updateMigrationFileStatusByName("cca3_facility_migration.csv", FileStatus.SUBMITTED))
                .thenReturn("uuid");

        // Invoke
        String result = service.submitAndGetSourceFile();

        // Verify
        assertThat(result).isEqualTo("uuid");
        verify(ccaFileAttachmentService, times(1))
                .updateMigrationFileStatusByName("cca3_facility_migration.csv", FileStatus.SUBMITTED);
    }

    @Test
    void parseSourceFile() {
        final FileStatus status = FileStatus.PENDING_MIGRATION;
        final String csvFile = "3e7ef542-ff79-4cef-a751-a304707be663";
        List<String> errors = new ArrayList<>();

        final String csvFileContent = "TU ID,Facility ID,Facility Name,facility participate,Baseline start date,Reason,Baseline energy or carbon (unit),Baseline energy to carbon factor (kgC/kWh),SRM used,TP7 improvement target,TP8 improvement target,TP9 improvement target,Baseline total fixed energy (or carbon) value,Baseline total variable energy (or carbon),Baseline Total Throughput,Baseline throughput unit\n" +
                "ADS_2-T00006,ADS_2-F00022,Facility 1-36,Yes,1/1/2022,,Energy (kWh),153.1234568,Yes,55,12,97,0,123.4444,123,unit\n" +
                "ADS_2-T00006,ADS_2-F00023,Facility 1-37,Yes,1/1/2022,,Energy (kWh),153.1234568,Yes,55,12,97,0,123.4444,123,unit\n" +
                "ADS_2-T00006,ADS_2-F00024,Facility 1-38,No,,,,,,,,,,,,\n" +
                "ADS_2-T00006,ADS_2-F00025,Facility 1-37,Yes,1/1/2022,,Energy (kWh),153.1234568,Yes,55,12,97,0,123.4444,123,unit";

        final List<FileInfoDTO> calculatorFiles = List.of(
                FileInfoDTO.builder().uuid("calculator1").name("ADS_2-F00022 CCA3 Migration Calculator1.xlsx").build(),
                FileInfoDTO.builder().uuid("calculator21").name("ADS_2-F00023 CCA3 Migration Calculator21.xlsx").build(),
                FileInfoDTO.builder().uuid("calculator22").name("ADS_2-F00023 CCA3 Migration Calculator22.xlsx").build()
        );
        final Cca3FacilityMigrationData facility1 = Cca3FacilityMigrationData.builder()
                .accountBusinessId("ADS_2-T00006")
                .facilityBusinessId("ADS_2-F00022")
                .facilityName("Facility 1-36")
                .participatingInCca3Scheme(true)
                .baselineDate(LocalDate.of(2022, 1, 1))
                .measurementType(MeasurementType.ENERGY_KWH)
                .energyCarbonFactor(BigDecimal.valueOf(153.1234568).setScale(7, RoundingMode.HALF_UP))
                .usedReportingMechanism(true)
                .tp7Improvement(BigDecimal.valueOf(55).setScale(7, RoundingMode.HALF_UP))
                .tp8Improvement(BigDecimal.valueOf(12).setScale(7, RoundingMode.HALF_UP))
                .tp9Improvement(BigDecimal.valueOf(97).setScale(7, RoundingMode.HALF_UP))
                .totalFixedEnergy(BigDecimal.ZERO.setScale(7, RoundingMode.HALF_UP))
                .totalVariableEnergy(BigDecimal.valueOf(123.4444).setScale(7, RoundingMode.HALF_UP))
                .totalThroughput(BigDecimal.valueOf(123).setScale(7, RoundingMode.HALF_UP))
                .throughputUnit("unit")
                .calculatorFileUuid("calculator1")
                .calculatorFileName("Calculator1.xlsx")
                .calculatorFileProvided(true)
                .build();
        final Cca3FacilityMigrationData facility3 = Cca3FacilityMigrationData.builder()
                .accountBusinessId("ADS_2-T00006")
                .facilityBusinessId("ADS_2-F00024")
                .facilityName("Facility 1-38")
                .participatingInCca3Scheme(false)
                .calculatorFileProvided(false)
                .build();

        when(fileAttachmentService.getFileDTO(csvFile))
                .thenReturn(FileDTO.builder().fileContent(csvFileContent.getBytes()).build());
        when(ccaFileAttachmentService.getAllByFileNameLikeAndStatus("CCA3 Migration", status))
                .thenReturn(calculatorFiles);

        // Invoke
        List<Cca3FacilityMigrationData> result = service.parseSourceFile(csvFile, errors);

        // Verify
        assertThat(errors).isNotEmpty();
        assertThat(result).contains(facility1, facility3);
        assertThat(result.stream().map(Cca3FacilityMigrationData::getCalculatorFileName).filter(Objects::nonNull).toList())
                .containsExactlyInAnyOrder("Calculator1.xlsx", "Facility base year and target data (Placeholder).xlsx");
        verify(fileAttachmentService, times(1)).getFileDTO(csvFile);
        verify(ccaFileAttachmentService, times(1)).getAllByFileNameLikeAndStatus("CCA3 Migration", status);
    }
}
