package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUpload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityUploadCsvData;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDataUploadExtractCsvDataServiceTest {

    @InjectMocks
    private PerformanceDataFacilityDataUploadExtractCsvDataService service;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Test
    void exportCsvData() {
        final Long sectorAssociationId = 2L;
        final PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                        .sectorAssociationInfo(SectorAssociationInfo.builder().id(sectorAssociationId).build())
                        .performanceDataUpload(PerformanceDataFacilityUpload.builder()
                                .files(Set.of(UUID.randomUUID(), UUID.randomUUID()))
                                .build())
                        .build();
        List<PerformanceDataFacilityCsvErrorEntry> errors = new ArrayList<>();

        final String csvFileContent1 = ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
                "facility11,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,NO,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
                "facility12,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,NO,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
                "facility13,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,NO,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n";
        final String csvFileContent2 = ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
                "facility11,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,NO,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
                "facility22,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,YES,,,product1,,product1,,product3,,product4,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
        List<FileDTO> files = List.of(
                FileDTO.builder().fileName("csv1").fileContent(csvFileContent1.getBytes()).build(),
                FileDTO.builder().fileName("csv2").fileContent(csvFileContent2.getBytes()).build()
        );

        List<FacilityDTO> facilities = List.of(
                FacilityDTO.builder().id(1L).accountId(11L).facilityBusinessId("facility12").build()
        );

        when(fileAttachmentService.getFiles(anySet())).thenReturn(files);
        when(facilityDataQueryService.getAllFacilitiesInfoDataBySectorForSchemeVersion(sectorAssociationId, SchemeVersion.CCA_3))
                .thenReturn(facilities);

        // Invoke
        Map<Long, FacilityUploadReport> result = service.exportCsvData(taskPayload, errors);

        // Verify
        assertThat(errors).hasSize(4).containsExactlyInAnyOrder(
                PerformanceDataFacilityCsvErrorEntry.builder()
                        .facilityBusinessId("facility11").filename("csv2").message("Facility duplicate found").build(),
                PerformanceDataFacilityCsvErrorEntry.builder()
                        .facilityBusinessId("facility11").filename("csv1").message("Facility duplicate found").build(),
                PerformanceDataFacilityCsvErrorEntry.builder()
                        .facilityBusinessId("facility22").filename("csv2").message("Facility duplicate products found").build(),
                PerformanceDataFacilityCsvErrorEntry.builder()
                        .facilityBusinessId("facility13").filename("csv1").message("Facility business ID does not exist or is not associated with the selected sector or scheme").build()
        );
        assertThat(result).hasSize(1).containsExactlyEntriesOf(Map.of(1L,
                FacilityUploadReport.builder()
                        .facilityId(1L)
                        .facilityBusinessId("facility12")
                        .accountId(11L)
                        .csvFileName("csv1")
                        .csvData(PerformanceDataFacilityUploadCsvData.builder()
                                .facilityBusinessId("facility12")
                                .atLeastSeventyPercentEnergyUsed(false)
                                .build())
                        .build())
        );
        verify(fileAttachmentService, times(1)).getFiles(anySet());
        verify(facilityDataQueryService, times(1))
                .getAllFacilitiesInfoDataBySectorForSchemeVersion(sectorAssociationId, SchemeVersion.CCA_3);
    }
}
