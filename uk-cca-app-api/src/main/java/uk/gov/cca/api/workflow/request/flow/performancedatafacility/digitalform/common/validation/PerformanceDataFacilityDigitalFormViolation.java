package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PerformanceDataFacilityDigitalFormViolation extends BusinessViolation {

    private String message;

    public PerformanceDataFacilityDigitalFormViolation(PerformanceDataFacilityDigitalFormViolation.PerformanceDataFacilityDigitalFormViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public PerformanceDataFacilityDigitalFormViolation(String sectionName, PerformanceDataFacilityDigitalFormViolation.PerformanceDataFacilityDigitalFormViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum PerformanceDataFacilityDigitalFormViolationMessage {
        TARGET_PERIOD_REPORTING_NOT_STARTED("Reporting for the target period has not started"),
        TARGET_PERIOD_REPORTING_IS_ENDED("Reporting for the target period has ended"),
        REPORT_TYPE_NOT_VALID("Report type is not valid"),
        FACILITY_NOT_ELIGIBLE("Facility is not eligible"),
        ;

        private final String message;

        PerformanceDataFacilityDigitalFormViolationMessage(String message) {
            this.message = message;
        }
    }
}
