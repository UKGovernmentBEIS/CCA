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
        PERFORMANCE_ACCOUNT_TEMPLATE_SUBMISSION_DATE_HAS_EXPIRED("The PAT workflow has expired and can no longer be submitted"),
        ATTACHMENT_NOT_FOUND("Attachment not found"),
        ;

        private final String message;

        FacilityPerformanceAccountTemplateDataViolationMessage(String message) {
            this.message = message;
        }
    }
}
