package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PerformanceDataFacilityViolation extends BusinessViolation {

    private String message;

    public PerformanceDataFacilityViolation(PerformanceDataFacilityViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public PerformanceDataFacilityViolation(String sectionName, PerformanceDataFacilityViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum PerformanceDataFacilityViolationMessage {
        INVALID_PERFORMANCE_DATA("Invalid performance data"),
        INVALID_PRIMARY_ENERGY_DATA("Invalid primary energy data"),
        INVALID_SRM_DATA("Invalid SRM data"),
        INVALID_CHP_DATA("Invalid CHP data"),
        INVALID_FUELS_THROUGHPUT_ADJUSTMENT_FACTOR_DATA("Invalid fuels throughput adjustment factor data"),
        INVALID_VARIABLE_ENERGY_DATA("Invalid variable energy data"),
        INVALID_PRODUCTS("Invalid products"),
        INVALID_PRODUCT_CALCULATED_DATA("Invalid product calculated data"),
        DUPLICATE_PRODUCTS_EXISTS("Duplicate products exists"),
        INVALID_CALCULATED_RESULT_DATA("Invalid expected calculated data"),
        TARGET_PERIOD_REPORTING_NOT_STARTED("Reporting for the target period has not started"),
        TARGET_PERIOD_REPORTING_IS_ENDED("Reporting for the target period has ended"),
        REPORT_TYPE_NOT_VALID("Report type is not valid"),
        FACILITY_NOT_ELIGIBLE("Facility is not eligible"),
        FACILITY_NOT_ELIGIBLE_PRODUCTS("Facility has no eligible products"),
        FACILITY_IS_LOCKED("Facility is locked for secondary reporting period"),
        FACILITY_BASELINE_DATE_NOT_ELIGIBLE("Facility baseline date is not eligible for target period year"),
        ;

        private final String message;

        PerformanceDataFacilityViolationMessage(String message) {
            this.message = message;
        }
    }
}
