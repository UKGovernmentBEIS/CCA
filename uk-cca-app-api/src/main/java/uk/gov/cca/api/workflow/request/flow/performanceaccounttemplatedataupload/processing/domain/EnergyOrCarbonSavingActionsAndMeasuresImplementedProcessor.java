package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import static uk.gov.cca.api.common.utils.ExcelCellUtils.getCellValueAsString;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.util.PerformanceAccountTemplateProcessingUtils.findTableSize;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.util.PerformanceAccountTemplateProcessingUtils.isRowEmpty;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor {
    
    public static final int FIRST_TABLE_ROW_INDEX = 14;
    public static final int FIRST_TABLE_COLUMN_INDEX = 1;
    public static final int LAST_TABLE_COLUMN_INDEX = 12;
    public static final int MAX_ROW_LIMIT = 10000;
    
    public static void populate(Sheet sheet, PerformanceAccountTemplateReportData data, FormulaEvaluator eval) {
        int lastTableRowIndex = FIRST_TABLE_ROW_INDEX + findTableSize(sheet, MAX_ROW_LIMIT, FIRST_TABLE_ROW_INDEX, FIRST_TABLE_COLUMN_INDEX);
        
        populateEnergyOrCarbonSavingActionsAndMeasuresImplemented(data, sheet, eval, lastTableRowIndex);
        populateTotalEstimatedChanges(data, sheet, eval, lastTableRowIndex);
        
    }
    
    private static void populateEnergyOrCarbonSavingActionsAndMeasuresImplemented(
            PerformanceAccountTemplateReportData data, Sheet sheet, FormulaEvaluator eval, int lastTableRowIndex) {
        
        List<PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow> actionsAndMeasuresList = new ArrayList<>();
        int rowIndex = FIRST_TABLE_ROW_INDEX;
        
        while (rowIndex < lastTableRowIndex) {
            Row row = sheet.getRow(rowIndex);
            if (row != null && !isRowEmpty(row, FIRST_TABLE_COLUMN_INDEX, LAST_TABLE_COLUMN_INDEX, eval)) {
                actionsAndMeasuresList.add(extractEnergyOrCarbonSavingActionsAndMeasuresImplementedFromRow(eval, rowIndex, row));
            }
            rowIndex++;
        }
        
        data.setEnergyOrCarbonSavingActionsAndMeasuresImplemented(actionsAndMeasuresList);
    }
    
    private static PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow extractEnergyOrCarbonSavingActionsAndMeasuresImplementedFromRow(FormulaEvaluator eval, int rowIndex, Row row) {
        return PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow.builder()
                .excelRowIndex(rowIndex)
                .facilityId(
                        getCellValueAsString(row.getCell(Columns.FACILITY_ID.getIndex()), eval)
                )
                .actionCategoryType(
                        getCellValueAsString(row.getCell(Columns.ACTION_CATEGORY_TYPE.getIndex()), eval)
                )
                .savingActionsImplemented(
                        getCellValueAsString(row.getCell(Columns.SAVING_ACTIONS_IMPLEMENTED.getIndex()), eval)
                )
                .reasonsForImplementation(
                        getCellValueAsString(row.getCell(Columns.REASONS_FOR_IMPLEMENTATION.getIndex()), eval)
                )
                .implementationDate(
                        getCellValueAsString(row.getCell(Columns.IMPLEMENTATION_DATE.getIndex()), eval)
                )
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(
                        getCellValueAsString(row.getCell(
                                Columns.FIXED_ENERGY_CONSUMPTION_OR_CARBON_EMISSIONS_IMPACTED.getIndex()), eval)
                )
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(
                        getCellValueAsString(row.getCell(
                                Columns.ENERGY_CONSUMPTION_OR_CARBON_EMISSIONS_IMPACTED_PERCENTAGE.getIndex()), eval)
                )
                .expectedExtentOfChangeImplementedPercentage(
                        getCellValueAsString(row.getCell(
                                Columns.EXPECTED_EXTENT_OF_CHANGE_IMPLEMENTED_PERCENTAGE.getIndex()), eval)
                )
                .expectedSavingsFromTheChangeImplementedPercentage(
                        getCellValueAsString(row.getCell(
                                Columns.EXPECTED_SAVINGS_FROM_CHANGE_IMPLEMENTED_PERCENTAGE.getIndex()), eval)
                )
                .estimatedChangeInEnergyConsumptionPercentage(
                        getCellValueAsString(row.getCell(
                                Columns.ESTIMATED_CHANGE_IN_ENERGY_CONSUMPTION_PERCENTAGE.getIndex()), eval)
                )
                .estimatedChangeInCarbonEmissionsPercentage(
                        getCellValueAsString(row.getCell(
                                Columns.ESTIMATED_CHANGE_IN_CARBON_EMISSIONS_PERCENTAGE.getIndex()), eval)
                )
                .notes(getCellValueAsString(row.getCell(Columns.NOTES.getIndex()), eval))
                .build();
    }
    
    private static void populateTotalEstimatedChanges(
            PerformanceAccountTemplateReportData data,
            Sheet sheet,
            FormulaEvaluator eval,
            int lastTableRowIndex) {
        
        Row totalRow = sheet.getRow(lastTableRowIndex);
        
        Cell totalEstimateChangeInEnergyConsumptionCell =
                totalRow.getCell(Columns.ESTIMATED_CHANGE_IN_ENERGY_CONSUMPTION_PERCENTAGE.getIndex());
        Cell totalEstimateChangeInCarbonEmissionsCell =
                totalRow.getCell(Columns.ESTIMATED_CHANGE_IN_CARBON_EMISSIONS_PERCENTAGE.getIndex());
        
        data.setTotalRowIndex(lastTableRowIndex);
        data.setTotalEstimateChangeInEnergyConsumptionPercentage(
                getCellValueAsString(totalEstimateChangeInEnergyConsumptionCell, eval)
        );
        data.setTotalEstimateChangeInCarbonEmissionsPercentage(
                getCellValueAsString(totalEstimateChangeInCarbonEmissionsCell, eval)
        );
    }
    
    @Getter
    @AllArgsConstructor
    public enum Columns {
        FACILITY_ID(1),
        ACTION_CATEGORY_TYPE(2),
        SAVING_ACTIONS_IMPLEMENTED(3),
        REASONS_FOR_IMPLEMENTATION(4),
        IMPLEMENTATION_DATE(5),
        FIXED_ENERGY_CONSUMPTION_OR_CARBON_EMISSIONS_IMPACTED(6),
        ENERGY_CONSUMPTION_OR_CARBON_EMISSIONS_IMPACTED_PERCENTAGE(7),
        EXPECTED_EXTENT_OF_CHANGE_IMPLEMENTED_PERCENTAGE(8),
        EXPECTED_SAVINGS_FROM_CHANGE_IMPLEMENTED_PERCENTAGE(9),
        ESTIMATED_CHANGE_IN_ENERGY_CONSUMPTION_PERCENTAGE(10),
        ESTIMATED_CHANGE_IN_CARBON_EMISSIONS_PERCENTAGE(11),
        NOTES(12);
        
        private final int index;
        
    }
}
