package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

@EqualsAndHashCode(callSuper = true)
@Data
public class PerformanceDataUploadViolation extends BusinessViolation {

    private String message;

    public PerformanceDataUploadViolation(PerformanceDataUploadViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public PerformanceDataUploadViolation(String sectionName, PerformanceDataUploadViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum PerformanceDataUploadViolationMessage {
        PROCESS_NOT_COMPLETED("Upload process not completed"),
        PROCESS_EXCEL_FAILED("There was an issue processing the excel file. Please contact the regulator for further advice."),
        FILE_NAME_NOT_VALID("The spreadsheet you upload must have the same file name as the spreadsheet you downloaded for this target unit."),
        SECTOR_NOT_VALID("This spreadsheet cannot be uploaded to this sector. Please navigate to the correct sector report submissions page."),
        TU_NOT_VALID("This spreadsheet cannot be uploaded either because a report is not expected for the target unit, or a report has already been uploaded for this target unit. Please contact the regulator for further advice"),
        GENERATE_CSV_FAILED("Generate csv failed"),
        READ_EXCEL_FAILED("File could not be read or file in incorrect size"),
        MULTIPLE_FILES_FOR_ACCOUNT_FOUND("Multiple files found for the account"),
        ATTACHMENT_NOT_FOUND("Attachment not found"),
        INVALID_SECTION_DATA("File contains invalid sections data"),

        INVALID_REPORT_VERSION("The file uploaded for this target unit uses a superseded version of the reporting spreadsheet. Please download the latest version of the reporting spreadsheet for this target unit."),
        INVALID_PREPOPULATED_DATA("Prepopulated data does not match the active underlying agreement. Please download the latest version of the reporting spreadsheet for this target unit."),
        INVALID_CALCULATED_DATA("Calculated values differ from expected values. Please download the latest version of the reporting spreadsheet for this target unit."),
        INVALID_CARBON_FACTORS_DATA("Other fuel identifier must be provided if consumption is greater than 0"),
        INVALID_EMPTY_DATA("Should not be null or empty"),
        INVALID_NOT_EMPTY_DATA("Should be null or empty"),
        FILE_NAME_REPORT_VERSION_NOT_VALID("The file name uploaded for this target unit uses a superseded version of the reporting spreadsheet. Please download the latest version of the reporting spreadsheet for this target unit."),
        REPORT_VERSION_NOT_VALID_FOR_PRIMARY_REPORTING("Correction to performance data is not allowed before the buy-out payment deadline"),
        ;

        private final String message;

        PerformanceDataUploadViolationMessage(String message) {
            this.message = message;
        }
    }
}
