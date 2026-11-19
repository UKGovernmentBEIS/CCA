package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.utils.ConversionUtils;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.SupplyDemandSideMeasure;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.validation.FacilityPerformanceAccountTemplateDataViolation;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadCsvData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.transform.FacilityPerformanceTemplateDataUploadMapper;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.validation.FacilityPerformanceAccountTemplateDataUploadCSVRowDataValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns.CATEGORY;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns.ENERGY_EMISSIONS_IMPACTED;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns.EXPECTED_EXTENT_IMPLEMENTED;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns.EXPECTED_SAVINGS_IMPLEMENTED;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns.FACILITY_ID;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns.FIXED_ENERGY_EMISSIONS_IMPACTED;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns.IMPLEMENTATION_DATE;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns.IMPLEMENTATION_REASONS;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns.NOTES;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns.SAVING_ACTIONS;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.CsvRowColumns.SIDE_MEASURE;

@Log4j2
@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadExtractCsvDataService {

    private final FileAttachmentService fileAttachmentService;
    private final FacilityPerformanceAccountTemplateDataUploadCSVRowDataValidator csvRowDataValidator;
    private final FacilityDataQueryService facilityDataQueryService;

    private static final FacilityPerformanceTemplateDataUploadMapper MAPPER = Mappers
            .getMapper(FacilityPerformanceTemplateDataUploadMapper.class);

    public Map<Long, FacilityPerformanceAccountTemplateUploadReport> exportAndValidateCsvData(final FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload,
                                                                                              final List<FacilityPerformanceAccountTemplateDataCsvErrorEntry> csvRowErrors) {

        Long sectorAssociationId = requestTaskPayload.getSectorAssociationInfo().getId();
        Year targetYear = requestTaskPayload.getPerformanceAccountTemplateDataUpload().getTargetYear();

        List<FacilityPerformanceAccountTemplateDataUploadCsvData> facilityCsvDataList = new ArrayList<>();
        // Get persisted facilities for this sector and scheme version
        List<FacilityDTO> persistedFacilities = facilityDataQueryService
                .getAllFacilitiesInfoDataBySectorForSchemeVersion(sectorAssociationId, SchemeVersion.CCA_3);
        Map<String, FacilityDTO> facilityByBusinessId = persistedFacilities.stream()
                .collect(Collectors.toMap(FacilityDTO::getFacilityBusinessId, Function.identity()));

        // Extract CSV data
        Set<String> files = requestTaskPayload.getReferencedAttachmentIds().stream().map(UUID::toString).collect(Collectors.toSet());
        fileAttachmentService.getFiles(files).forEach(fileDTO -> {
            List<FacilityPerformanceAccountTemplateDataUploadCsvData> validData = readAndValidateCSVRowData(fileDTO, targetYear, facilityByBusinessId, csvRowErrors);
            facilityCsvDataList.addAll(validData);
        });

        return getFacilitiesReport(targetYear, facilityByBusinessId, facilityCsvDataList, csvRowErrors);
    }

    private List<FacilityPerformanceAccountTemplateDataUploadCsvData> readAndValidateCSVRowData(FileDTO fileDTO, Year targetYear, Map<String, FacilityDTO> persistedFacilities, List<FacilityPerformanceAccountTemplateDataCsvErrorEntry> csvRowErrors) {

        String[] row;
        int rowNumber = 1;
        String filename = fileDTO.getFileName();
        List<FacilityPerformanceAccountTemplateDataUploadCsvData> validCsvData = new ArrayList<>();
        String facilityBusinessId = null;
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(fileDTO.getFileContent()), StandardCharsets.UTF_8))) {
            csvReader.skip(1); // skip header
            while ((row = csvReader.readNext()) != null) {
                try {
                    rowNumber++;

                    // validate the columns number for that row
                    validateColumnNumber(row);

                    ActionCategoryType actionCategoryType = ActionCategoryType.fromDescription(row[CATEGORY.getRowNumber()]);
                    facilityBusinessId = ConversionUtils.toStringTrim(row[FACILITY_ID.getRowNumber()]);
                    String notes = ConversionUtils.toStringTrim(row[NOTES.getRowNumber()]);
                    FacilityPerformanceAccountTemplateDataUploadCsvData rowData = actionCategoryType == ActionCategoryType.NO_ACTION
                            ?
                            FacilityPerformanceAccountTemplateDataUploadCsvData.builder()
                                    .filename(filename)
                                    .rowNumber(rowNumber)
                                    .facilityBusinessId(facilityBusinessId)
                                    .actionCategoryType(actionCategoryType)
                                    .notes(notes)
                                    .build()
                            :
                            FacilityPerformanceAccountTemplateDataUploadCsvData.builder()
                                    .filename(filename)
                                    .rowNumber(rowNumber)
                                    .facilityBusinessId(facilityBusinessId)
                                    .actionCategoryType(actionCategoryType)
                                    .supplyDemandSideMeasure(SupplyDemandSideMeasure.fromDescription(row[SIDE_MEASURE.getRowNumber()]))
                                    .savingActionsImplemented(ConversionUtils.toStringTrim(row[SAVING_ACTIONS.getRowNumber()]))
                                    .reasonsForImplementation(ConversionUtils.toStringTrim(row[IMPLEMENTATION_REASONS.getRowNumber()]))
                                    .implementationDate(ConversionUtils.toLocalDate(row[IMPLEMENTATION_DATE.getRowNumber()]))
                                    .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.fromDescription(row[FIXED_ENERGY_EMISSIONS_IMPACTED.getRowNumber()]))
                                    .energyConsumptionOrCarbonEmissionsImpactedPercentage(ConversionUtils.toBigDecimalScale7HalfDown(row[ENERGY_EMISSIONS_IMPACTED.getRowNumber()]))
                                    .expectedExtentOfChangeImplementedPercentage(ConversionUtils.toBigDecimalScale7HalfDown(row[EXPECTED_EXTENT_IMPLEMENTED.getRowNumber()]))
                                    .expectedSavingsFromTheChangeImplementedPercentage(ConversionUtils.toBigDecimalScale7HalfDown(row[EXPECTED_SAVINGS_IMPLEMENTED.getRowNumber()]))
                                    .notes(notes)
                                    .build();

                    // Validate row data
                    List<FacilityPerformanceAccountTemplateDataViolation> rowDataViolations =
                            csvRowDataValidator.validate(filename, targetYear, persistedFacilities, rowNumber, csvRowErrors, rowData);

                    if (rowDataViolations.isEmpty()) {
                        validCsvData.add(rowData);
                    }
                } catch (Exception ex) {
                    csvRowErrors.add(FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                            .facilityBusinessId(facilityBusinessId)
                            .rowNumber(rowNumber)
                            .filename(filename)
                            .message(ex.getMessage())
                            .build());
                }
            }
        } catch (IOException | CsvValidationException ex) {
            log.error("Error parsing csv file: ", ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, ex.getMessage());
        }

        return validCsvData;
    }

    private Map<Long, FacilityPerformanceAccountTemplateUploadReport> getFacilitiesReport(final Year targetPeriodYear,
                                                                                          final Map<String, FacilityDTO> facilityByBusinessId,
                                                                                          final List<FacilityPerformanceAccountTemplateDataUploadCsvData> facilityCsvDataList,
                                                                                          final List<FacilityPerformanceAccountTemplateDataCsvErrorEntry> csvRowErrors) {

        // Validate NO_ACTION with additional rows and enhance csv errors
        validateNoActionWithAdditionalRows(facilityCsvDataList, csvRowErrors);

        final Set<String> facilitiesWithErrors = csvRowErrors.stream().map(FacilityPerformanceAccountTemplateDataCsvErrorEntry::getFacilityBusinessId).collect(Collectors.toSet());
        final Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityUploadReport = new HashMap<>();

        facilityCsvDataList.forEach((csvFacility) -> {
            String facilityBusinessId = csvFacility.getFacilityBusinessId();
            if (!facilitiesWithErrors.contains(facilityBusinessId)) {
                FacilityDTO facilityDTO = facilityByBusinessId.get(facilityBusinessId);
                Long facilityId = facilityDTO.getId();
                FacilityPerformanceAccountTemplateSavingAction savingAction = MAPPER.toSavingAction(csvFacility, targetPeriodYear);
                // first addition in report
                if (!facilityUploadReport.containsKey(facilityId)) {
                    List<FacilityPerformanceAccountTemplateSavingAction> savingActions = new ArrayList<>();
                    savingActions.add(savingAction);
                    FacilityPerformanceAccountTemplateUploadReport templateUploadReport = FacilityPerformanceAccountTemplateUploadReport.builder()
                            .facilityId(facilityId)
                            .facilityBusinessId(facilityBusinessId)
                            .accountId(facilityDTO.getAccountId())
                            .savingActions(savingActions)
                            .succeeded(true)
                            .build();
                    facilityUploadReport.put(facilityId, templateUploadReport);
                } else {
                    FacilityPerformanceAccountTemplateUploadReport templateUploadReport = facilityUploadReport.get(facilityId);
                    templateUploadReport.getSavingActions().add(savingAction);
                }
            }
        });

        return facilityUploadReport;
    }

    private void validateNoActionWithAdditionalRows(final List<FacilityPerformanceAccountTemplateDataUploadCsvData> facilityCsvDataList, final List<FacilityPerformanceAccountTemplateDataCsvErrorEntry> csvRowErrors) {
        facilityCsvDataList.stream()
                .collect(Collectors.groupingBy(FacilityPerformanceAccountTemplateDataUploadCsvData::getFacilityBusinessId,
                        Collectors.mapping(f -> f, Collectors.toList())))
                .forEach((facilityBusinessId, facilityData) -> {
                    if (facilityData.size() > 1 && facilityData.stream().anyMatch(f -> f.getActionCategoryType() == ActionCategoryType.NO_ACTION)
                            && facilityData.stream().anyMatch(f -> f.getActionCategoryType() != ActionCategoryType.NO_ACTION)) {
                        facilityData.forEach(invalidData -> {
                            FacilityPerformanceAccountTemplateDataCsvErrorEntry csvErrorEntry = FacilityPerformanceAccountTemplateDataCsvErrorEntry.builder()
                                    .facilityBusinessId(invalidData.getFacilityBusinessId())
                                    .rowNumber(invalidData.getRowNumber())
                                    .filename(invalidData.getFilename())
                                    .message(FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_ACTION_CATEGORY_TYPE.getMessage())
                                    .build();
                            csvRowErrors.add(csvErrorEntry);
                        });
                    }
                });
    }

    private void validateColumnNumber(String[] row) throws CsvValidationException {
        int validColumnNumber = CsvRowColumns.values().length;
        if (row.length != validColumnNumber) {
            throw new CsvValidationException("Row must contain exactly " + CsvRowColumns.values().length + " columns");
        }
    }
}
