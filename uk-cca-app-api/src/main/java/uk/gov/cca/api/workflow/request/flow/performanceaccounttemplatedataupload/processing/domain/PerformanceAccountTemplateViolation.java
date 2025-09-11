package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import lombok.Data;
import lombok.Getter;
import org.apache.poi.ss.util.CellAddress;

@Data
public class PerformanceAccountTemplateViolation {
    
    private CellAddress cellAddress;
    private String message;
    
    public PerformanceAccountTemplateViolation(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage violationMessage) {
        this.message = violationMessage.getMessage();
    }
    
    public PerformanceAccountTemplateViolation(CellAddress cellAddress, PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage violationMessage) {
        this.cellAddress = cellAddress;
        this.message = violationMessage.getMessage();
    }
    
    @Override
    public String toString() {
        return this.cellAddress != null
                ? this.cellAddress + ": " + this.message
                : this.message;
    }
    
    @Getter
    public enum PerformanceAccountTemplateViolationMessage {
        PROCESS_EXCEL_FAILED("There was an issue processing the excel file. Please contact the regulator for further advice."),
        CELL_ADDRESS_NOT_FOUND("Unable to locate the specified field"),
        TOTAL_KEYWORD_NOT_FOUND("Unable to locate 'Total' row in the expected range"),
        INVALID_TARGET_UNIT_BUSINESS_ID("Target Unit Id is empty or does not match Target Unit Id in filename"),
        INVALID_TARGET_TYPE("Target type is empty or does not belong to the specified Target Types"),
        
        INVALID_PERFORMANCE_IMPACTED_VALUE("Value must be 'Yes' or 'No'"),
        INVALID_PERFORMANCE_IMPACTED_SUPPORTING_TEXT("Must provide supporting text if performance was not impacted"),
        INVALID_PERFORMANCE_IMPACTED_EMPTY_TABLE("At least one row in 'Energy/Carbon Saving Action and Measure Implemented' table is required in case performance was impacted"),
        
        INVALID_ACTION_CATEGORY("Category is empty or does not belong to the specified Action Categories"),
        INVALID_IMPACTED_TYPE("Fixed Energy Consumption or Carbon Emissions Impacted is empty or does not belong to the specified list of values"),
        
        INVALID_VALUE_PROVIDED("Missing or invalid value detected"),
        INVALID_VALUE_NUMERIC("Value must be numeric");
        
        private final String message;
        
        PerformanceAccountTemplateViolationMessage(String message) {
            this.message = message;
        }
    }
}
