package uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import uk.gov.cca.api.common.validation.BusinessViolation;

@EqualsAndHashCode(callSuper = true)
@Data
public class PerformanceDataFacilityViolation extends BusinessViolation {

    private String message;

    public PerformanceDataFacilityViolation(PerformanceDataFacilityViolationMessage violationMessage) {
        super("", violationMessage.getMessage());
        this.message = violationMessage.getMessage();
    }

    public PerformanceDataFacilityViolation(String sectionName, Object... data) {
        super(sectionName, data);
    }

    public PerformanceDataFacilityViolation(String sectionName, PerformanceDataFacilityViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum PerformanceDataFacilityViolationMessage {
        INVALID_PERFORMANCE_DATA("Invalid performance data"),
        INVALID_PRIMARY_ENERGY_DATA("Invalid primary energy data"),
        SRM_DATA_DOES_NOT_MATCH_USE_SRM_SELECTION("The SRM related data provided does not match the selected use-SRM option"),
        INVALID_CHP_DATA("Input inconsistent with SRM rules"),
        INVALID_FUELS_THROUGHPUT_ADJUSTMENT_FACTOR_DATA("Invalid fuels throughput adjustment factor data"),
        INVALID_VARIABLE_ENERGY_DATA("Invalid variable energy data"),
        VARIABLE_ENERGY_DATA_DOES_NOT_MATCH_TYPE("The variable energy data provided does not match the selected variable energy type"),
        PRODUCTS_DO_NOT_MATCH_UNDERLYING_AGREEMENT("The products provided do not match the products declared in the underlying agreement"),
        INVALID_PRODUCT_CALCULATED_DATA("Invalid product calculated data"),
        DUPLICATE_PRODUCTS_EXISTS("Duplicate products exists"),
        INVALID_CALCULATED_RESULT_DATA("Invalid expected calculated data"),
        TARGET_PERIOD_REPORTING_NOT_STARTED("Reporting for the target period has not started"),
        TARGET_PERIOD_REPORTING_IS_ENDED("Reporting for the target period has ended"),
        REPORT_TYPE_NOT_VALID("Report type is not valid"),
        FACILITY_NOT_ELIGIBLE("Facility is not eligible"),
        FACILITY_NOT_ELIGIBLE_PRODUCTS("Facility has no eligible products"),
        FACILITY_PRODUCT_WITH_FACILITY_BASE_YEAR_DOES_NOT_EXIST("None of the facility products have a product year that matches the facility base year"),
        FACILITY_IS_LOCKED("Facility is locked for secondary reporting period"),
        FACILITY_BASELINE_DATE_NOT_ELIGIBLE("Facility baseline date is not eligible for target period year"),
        ATTACHMENT_NOT_FOUND("Attachment not found"),
        INVALID_CSV_FACILITY_BUSINESS_ID("Facility business ID does not exist or is not associated with the selected sector or scheme"),
        INVALID_CSV_FACILITY_DUPLICATE_FOUND("Facility duplicate found"),
        INVALID_CSV_FACILITY_DUPLICATE_PRODUCTS_FOUND("Facility duplicate products found"),
        FACILITY_CSV_FUELS_NOT_VALID("At least one of the fuels (standard or non-standard) MUST have a value greater than zero"),
        FACILITY_CSV_PRODUCTS_NOT_VALID("The name and throughput for at least one product must be provided"),
        FACILITY_PROCESS_FAILED("Facility process failed"),
        PROCESS_NOT_COMPLETED("Upload process not completed"),
        ;

        private final String message;

        PerformanceDataFacilityViolationMessage(String message) {
            this.message = message;
        }
    }
}
