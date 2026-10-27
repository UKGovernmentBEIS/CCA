package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.SupplyDemandSideMeasure;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.validation.FacilityPerformanceAccountTemplateDataViolation;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUpload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadCsvData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.validation.FacilityPerformanceAccountTemplateDataUploadCSVRowDataValidator;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataUploadExtractCsvDataServiceTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadExtractCsvDataService service;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private FacilityPerformanceAccountTemplateDataUploadCSVRowDataValidator csvRowDataValidator;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Test
    void exportAndValidateCsvData() {
        final UUID csvFile1 = UUID.randomUUID();
        final UUID csvFile2 = UUID.randomUUID();
        final String fileName1 = "csv1";
        final String fileName2 = "csv2";
        final Long sectorAssociationId = 1L;
        final Year targetYear = Year.of(2026);
        final LocalDate implementationDate = LocalDate.of(2026, Month.APRIL, 1);
        final Set<UUID> files = Set.of(csvFile1, csvFile2);
        Map<UUID, String> uploadAttachments = Map.of(csvFile1, "csv1", csvFile2, "csv2");
        final FacilityPerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = FacilityPerformanceAccountTemplateDataUpload.builder()
                .targetYear(targetYear)
                .files(files)
                .build();
        final FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .performanceAccountTemplateDataUpload(performanceAccountTemplateDataUpload)
                .sectorAssociationInfo(SectorAssociationInfo.builder()
                        .id(sectorAssociationId)
                        .build())
                .processingStatus(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET)
                .uploadAttachments(uploadAttachments)
                .build();
        final Long facilityId1 = 1L;
        final String facilityBusinessId1 = "FDF1-F00001";
        final Long facilityId2 = 2L;
        final String facilityBusinessId2 = "FDF1-F00002";
        final String facilityBusinessId3 = "FDF1-F00003";
        final List<FacilityDTO> persistedFacilities = List.of(
                FacilityDTO.builder()
                        .id(facilityId1)
                        .accountId(1L)
                        .facilityBusinessId(facilityBusinessId1)
                        .createdDate(LocalDateTime.of(2024, 6, 1, 0, 0, 0))
                        .build(),
                FacilityDTO.builder()
                        .id(facilityId2)
                        .accountId(1L)
                        .facilityBusinessId(facilityBusinessId2)
                        .facilityBusinessId(facilityBusinessId2)
                        .createdDate(LocalDateTime.of(2026, 6, 1, 0, 0, 0))
                        .build());
        final Map<String, FacilityDTO> facilityByBusinessId = persistedFacilities.stream()
                .collect(Collectors.toMap(FacilityDTO::getFacilityBusinessId, Function.identity()));
        final Set<String> csvFiles = files.stream().map(UUID::toString).collect(Collectors.toSet());

        final String csvFileContent1 = "Facility Identifier,Category,Supply/Demand Side Measure,Description of Savings Action,Reason for Implementation,Implementation Date (DD/MM/YYYY),Fixed/Variable Energy or Carbon Emissions,Proportion of energy or emissions affected (%),Expected extent of implementation (%),Expected efficiency improvement (%),Notes\n" +
                "FDF1-F00001,Process optimisation,Demand side,Process improvements,Tweaked a few settings to make the process more efficient,01/04/2026,Fixed and variable,10,25,60,We could only implement this measure half way through TP6\n" +
                "FDF1-F00002,No action,0,0,0,,0,0,0,0,We've exhausted all possible options to improve efficiency at this facility.\n" +
                "FDF1-F00002,No action,0,0,0,,0,0,0,0,";
        final String csvFileContent2 = "Facility Identifier,Category,Supply/Demand Side Measure,Description of Savings Action,Reason for Implementation,Implementation Date (DD/MM/YYYY),Fixed/Variable Energy or Carbon Emissions,Proportion of energy or emissions affected (%),Expected extent of implementation (%),Expected efficiency improvement (%),Notes\n" +
                "FDF1-F00003,No action,0,0,0,,0,0,0,0,testNotes";
        final List<FileDTO> attachedCsvFiles = List.of(
                FileDTO.builder().fileName(fileName1).fileType("text/csv").fileContent(csvFileContent1.getBytes()).fileSize(20).build(),
                FileDTO.builder().fileName(fileName2).fileType("text/csv").fileContent(csvFileContent2.getBytes()).fileSize(20).build()
        );

        final BigDecimal energyConsumptionOrCarbonEmissionsImpactedPercentage = new BigDecimal(10).setScale(7, RoundingMode.HALF_DOWN);
        final BigDecimal expectedExtentOfChangeImplementedPercentage = new BigDecimal(25).setScale(7, RoundingMode.HALF_DOWN);
        final BigDecimal expectedSavingsFromTheChangeImplementedPercentage = new BigDecimal(60).setScale(7, RoundingMode.HALF_DOWN);
        final BigDecimal estimatedChangeInEnergyConsumptionPercentage = new BigDecimal("1.125").setScale(7, RoundingMode.HALF_DOWN);
        final FacilityPerformanceAccountTemplateDataUploadCsvData row1_1 = FacilityPerformanceAccountTemplateDataUploadCsvData.builder()
                .filename(fileName1)
                .rowNumber(2)
                .facilityBusinessId(facilityBusinessId1)
                .actionCategoryType(ActionCategoryType.PROCESS_OPTIMISATION)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("Process improvements")
                .reasonsForImplementation("Tweaked a few settings to make the process more efficient")
                .implementationDate(implementationDate)
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE)
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(energyConsumptionOrCarbonEmissionsImpactedPercentage)
                .expectedExtentOfChangeImplementedPercentage(expectedExtentOfChangeImplementedPercentage)
                .expectedSavingsFromTheChangeImplementedPercentage(expectedSavingsFromTheChangeImplementedPercentage)
                .notes("We could only implement this measure half way through TP6")
                .build();

        final FacilityPerformanceAccountTemplateDataUploadCsvData row1_2 = FacilityPerformanceAccountTemplateDataUploadCsvData.builder()
                .filename(fileName1)
                .rowNumber(3)
                .facilityBusinessId(facilityBusinessId2)
                .actionCategoryType(ActionCategoryType.NO_ACTION)
                .notes("We've exhausted all possible options to improve efficiency at this facility.")
                .build();

        final FacilityPerformanceAccountTemplateDataUploadCsvData row1_3 = FacilityPerformanceAccountTemplateDataUploadCsvData.builder()
                .filename(fileName1)
                .rowNumber(4)
                .facilityBusinessId(facilityBusinessId2)
                .actionCategoryType(ActionCategoryType.NO_ACTION)
                .build();


        final FacilityPerformanceAccountTemplateDataUploadCsvData row2_1 = FacilityPerformanceAccountTemplateDataUploadCsvData.builder()
                .filename(fileName2)
                .rowNumber(2)
                .facilityBusinessId(facilityBusinessId3)
                .actionCategoryType(ActionCategoryType.NO_ACTION)
                .notes("testNotes")
                .build();

        final FacilityPerformanceAccountTemplateSavingAction savingAction = FacilityPerformanceAccountTemplateSavingAction.builder()
                .actionCategoryType(ActionCategoryType.PROCESS_OPTIMISATION)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("Process improvements")
                .reasonsForImplementation("Tweaked a few settings to make the process more efficient")
                .implementationDate(implementationDate)
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE)
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(energyConsumptionOrCarbonEmissionsImpactedPercentage)
                .expectedExtentOfChangeImplementedPercentage(expectedExtentOfChangeImplementedPercentage)
                .expectedSavingsFromTheChangeImplementedPercentage(expectedSavingsFromTheChangeImplementedPercentage)
                .estimatedChangeInEnergyConsumptionPercentage(estimatedChangeInEnergyConsumptionPercentage)
                .notes("We could only implement this measure half way through TP6")
                .build();

        final List<FacilityPerformanceAccountTemplateDataCsvErrorEntry> csvRowErrors = List.of(
                FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                        .facilityBusinessId(facilityBusinessId2)
                        .rowNumber(row1_3.getRowNumber())
                        .filename(fileName1)
                        .message(FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_PERFORMANCE_ACCOUNT_TEMPLATE_DATA.getMessage())
                        .build(),
                FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                        .facilityBusinessId(facilityBusinessId3)
                        .rowNumber(row2_1.getRowNumber())
                        .filename(fileName2)
                        .message(FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_FACILITY_ID.getMessage())
                        .build()
        );

        when(facilityDataQueryService
                .getAllFacilitiesInfoDataBySectorForSchemeVersion(sectorAssociationId, SchemeVersion.CCA_3)).thenReturn(persistedFacilities);
        when(fileAttachmentService.getFiles(csvFiles)).thenReturn(attachedCsvFiles);
        when(csvRowDataValidator.validate(fileName1, targetYear, facilityByBusinessId, 2, csvRowErrors, row1_1)).thenReturn(List.of());
        when(csvRowDataValidator.validate(fileName1, targetYear, facilityByBusinessId, 3, csvRowErrors, row1_2)).thenReturn(List.of());
        when(csvRowDataValidator.validate(fileName1, targetYear, facilityByBusinessId, 4, csvRowErrors, row1_3)).thenReturn(List.of(
                new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUploadCsvData.class.getName(),
                        FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_PERFORMANCE_ACCOUNT_TEMPLATE_DATA)));
        when(csvRowDataValidator.validate(fileName2, targetYear, facilityByBusinessId, 2, csvRowErrors, row2_1)).thenReturn(List.of(
                new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUploadCsvData.class.getName(),
                        FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_FACILITY_ID,
                        facilityBusinessId3)));

        // invoke
        Map<Long, FacilityPerformanceAccountTemplateUploadReport> results = service.exportAndValidateCsvData(requestTaskPayload, csvRowErrors);

        // verify
        assertThat(results).hasSize(1).containsExactlyEntriesOf(Map.of(
                facilityId1, FacilityPerformanceAccountTemplateUploadReport.builder()
                        .facilityId(facilityId1)
                        .facilityBusinessId(facilityBusinessId1)
                        .accountId(1L)
                        .savingActions(List.of(savingAction))
                        .succeeded(true)
                        .build()
        ));
        verify(fileAttachmentService, times(1)).getFiles(anySet());
        verify(csvRowDataValidator, times(4)).validate(anyString(), eq(targetYear), eq(facilityByBusinessId), anyInt(), eq(csvRowErrors), any());
        verify(facilityDataQueryService, times(1))
                .getAllFacilitiesInfoDataBySectorForSchemeVersion(sectorAssociationId, SchemeVersion.CCA_3);
    }
}
