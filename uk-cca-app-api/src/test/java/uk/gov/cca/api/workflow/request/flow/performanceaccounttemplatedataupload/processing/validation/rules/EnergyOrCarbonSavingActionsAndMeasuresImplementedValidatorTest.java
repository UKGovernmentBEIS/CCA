package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules;

import org.apache.poi.ss.util.CellAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.EnergyOrCarbonSavingActionsAndMeasuresImplementedProcessor.Columns;

class EnergyOrCarbonSavingActionsAndMeasuresImplementedValidatorTest {
    
    private EnergyOrCarbonSavingActionsAndMeasuresImplementedValidator validator;
    private PerformanceAccountTemplateReportData data;
    private List<EnergyOrCarbonSavingActionsAndMeasuresImplementedRow> rows;
    
    @BeforeEach
    void setUp() {
        validator = new EnergyOrCarbonSavingActionsAndMeasuresImplementedValidator();
        data = new PerformanceAccountTemplateReportData();
        rows = new ArrayList<>();
        data.setEnergyOrCarbonSavingActionsAndMeasuresImplemented(rows);
    }
    
    @Test
    void validate_shouldReturnNoViolationsForValidRow() {
        rows.add(createValidRow(1));
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validate_shouldReturnViolationForInvalidCategory() {
        EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row = createValidRow(2);
        row.setActionCategoryType("Invalid Category");
        rows.add(row);
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertEquals(1, violations.size());
        PerformanceAccountTemplateViolation violation = violations.get(0);
        assertEquals(new CellAddress(2, Columns.ACTION_CATEGORY_TYPE.getIndex()), violation.getCellAddress());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_ACTION_CATEGORY.getMessage(),
                violation.getMessage());
    }
    
    @Test
    void validate_shouldReturnViolationForBlankSavingActions() {
        EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row = createValidRow(3);
        row.setSavingActionsImplemented("");
        rows.add(row);
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertEquals(1, violations.size());
        PerformanceAccountTemplateViolation violation = violations.get(0);
        assertEquals(new CellAddress(3, Columns.SAVING_ACTIONS_IMPLEMENTED.getIndex()), violation.getCellAddress());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_PROVIDED.getMessage(),
                violation.getMessage());
    }
    
    @Test
    void validate_shouldReturnViolationForBlankReasons() {
        PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row = createValidRow(4);
        row.setReasonsForImplementation(null);
        rows.add(row);
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertEquals(1, violations.size());
        PerformanceAccountTemplateViolation violation = violations.get(0);
        assertEquals(new CellAddress(4, Columns.REASONS_FOR_IMPLEMENTATION.getIndex()), violation.getCellAddress());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_PROVIDED.getMessage(),
                violation.getMessage());
    }
    
    @Test
    void validate_shouldReturnViolationForEmptyDate() {
        EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row = createValidRow(5);
        row.setImplementationDate(null);
        rows.add(row);
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertEquals(1, violations.size());
        PerformanceAccountTemplateViolation violation = violations.get(0);
        assertEquals(new CellAddress(5, Columns.IMPLEMENTATION_DATE.getIndex()), violation.getCellAddress());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_PROVIDED.getMessage(),
                violation.getMessage());
    }
    
    @Test
    void validate_shouldReturnViolationForInvalidImpactedType() {
        EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row = createValidRow(6);
        row.setFixedEnergyConsumptionOrCarbonEmissionsImpacted("Invalid Type");
        rows.add(row);
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertEquals(1, violations.size());
        PerformanceAccountTemplateViolation violation = violations.get(0);
        assertEquals(new CellAddress(6, Columns.FIXED_ENERGY_CONSUMPTION_OR_CARBON_EMISSIONS_IMPACTED.getIndex()),
                violation.getCellAddress());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_IMPACTED_TYPE.getMessage(),
                violation.getMessage());
    }
    
    @Test
    void validate_shouldReturnViolationForNonNumericImpactedPercentage() {
        EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row = createValidRow(7);
        row.setEnergyConsumptionOrCarbonEmissionsImpactedPercentage("not a number");
        rows.add(row);
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertEquals(1, violations.size());
        PerformanceAccountTemplateViolation violation = violations.get(0);
        assertEquals(new CellAddress(7, Columns.ENERGY_CONSUMPTION_OR_CARBON_EMISSIONS_IMPACTED_PERCENTAGE.getIndex()),
                violation.getCellAddress());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC.getMessage(),
                violation.getMessage());
    }
    
    @Test
    void validate_shouldReturnViolationForBlankExpectedChangePercentage() {
        EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row = createValidRow(8);
        row.setExpectedExtentOfChangeImplementedPercentage(" ");
        rows.add(row);
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertEquals(1, violations.size());
        PerformanceAccountTemplateViolation violation = violations.get(0);
        assertEquals(new CellAddress(8, Columns.EXPECTED_EXTENT_OF_CHANGE_IMPLEMENTED_PERCENTAGE.getIndex()),
                violation.getCellAddress());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC.getMessage(),
                violation.getMessage());
    }
    
    @Test
    void validate_shouldReturnViolationForNonNumericExpectedSavings() {
        PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row = createValidRow(9);
        row.setExpectedSavingsFromTheChangeImplementedPercentage("123abc");
        rows.add(row);
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertEquals(1, violations.size());
        PerformanceAccountTemplateViolation violation = violations.get(0);
        assertEquals(new CellAddress(9, Columns.EXPECTED_SAVINGS_FROM_CHANGE_IMPLEMENTED_PERCENTAGE.getIndex()),
                violation.getCellAddress());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.INVALID_VALUE_NUMERIC.getMessage(),
                violation.getMessage());
    }
    
    @Test
    void validate_shouldReturnMultipleViolationsForMultipleIssues() {
        PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row = createValidRow(10);
        row.setActionCategoryType(null);
        row.setSavingActionsImplemented("");
        row.setEnergyConsumptionOrCarbonEmissionsImpactedPercentage("invalid");
        rows.add(row);
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertEquals(3, violations.size());
    }
    
    @Test
    void validate_shouldHandleNullRowsList() {
        data.setEnergyOrCarbonSavingActionsAndMeasuresImplemented(null);
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validate_shouldHandleEmptyRowsList() {
        rows.clear();
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(data);
        
        assertTrue(violations.isEmpty());
    }
    
    private EnergyOrCarbonSavingActionsAndMeasuresImplementedRow createValidRow(int rowIndex) {
        PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row = new PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow();
        row.setExcelRowIndex(rowIndex);
        row.setActionCategoryType(ActionCategoryType.ENERGY_MANAGEMENT.getDescription());
        row.setSavingActionsImplemented("Installed LED lighting");
        row.setReasonsForImplementation("Energy efficiency improvement");
        row.setImplementationDate("2023-01-15");
        row.setFixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED.getDescription());
        row.setEnergyConsumptionOrCarbonEmissionsImpactedPercentage("10.5");
        row.setExpectedExtentOfChangeImplementedPercentage("15.0");
        row.setExpectedSavingsFromTheChangeImplementedPercentage("12.3");
        return row;
    }
}
