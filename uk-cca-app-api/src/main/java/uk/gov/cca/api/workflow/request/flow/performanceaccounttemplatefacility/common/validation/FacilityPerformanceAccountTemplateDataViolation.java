package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

@Data
@EqualsAndHashCode(callSuper = true)
public class FacilityPerformanceAccountTemplateDataViolation extends BusinessViolation {

    private String message;

    public FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage violationMessage) {
        super("", violationMessage.getMessage());
        this.message = violationMessage.getMessage();
    }

    public FacilityPerformanceAccountTemplateDataViolation(String sectionName, Object... data) {
        super(sectionName, data);
    }

    public FacilityPerformanceAccountTemplateDataViolation(String sectionName, FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum FacilityPerformanceAccountTemplateDataViolationMessage {
        INVALID_PERFORMANCE_ACCOUNT_TEMPLATE_DATA("Invalid performance account template data"),
        INVALID_FACILITY_ID("The facility does not exist in DB or does not belong to selected sector"),
        INVALID_FACILITY_ACTIVATION_DATE("Facility entry date is after 1 January in the reporting period"),
        INVALID_FACILITY_CLOSE_DATE("Facility close date is not after 1 January in the reporting period"),
        INVALID_IMPLEMENTATION_DATE("Implementation date outside valid range"),
        INVALID_ACTION_CATEGORY_TYPE("Contradictory information submitted for facility - correct and resubmit"),
        FACILITY_PROCESS_FAILED("Facility process failed"),
        PROCESS_NOT_COMPLETED("Upload process not completed"),
        ATTACHMENT_NOT_FOUND("Attachment not found");

        private final String message;

        FacilityPerformanceAccountTemplateDataViolationMessage(String message) {
            this.message = message;
        }
    }
}
