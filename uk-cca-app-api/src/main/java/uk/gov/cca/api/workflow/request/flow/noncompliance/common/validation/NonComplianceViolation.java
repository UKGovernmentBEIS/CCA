package uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class NonComplianceViolation extends BusinessViolation {

    private String message;

    public NonComplianceViolation(NonComplianceViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public NonComplianceViolation(String sectionName, NonComplianceViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum NonComplianceViolationMessage {
        INVALID_NON_COMPLIANCE_DETAILS_DATA("Invalid Non-compliance details data"),
        INVALID_NON_COMPLIANCE_CLOSURE_DATA("Invalid Non-compliance closure data"),
        INVALID_NON_COMPLIANCE_NOTICE_OF_INTENT_DATA("Invalid Non-compliance notice of intent data"),
        INVALID_NON_COMPLIANCE_DECISION_NOTIFICATION_DATA("Invalid Non-compliance decision notification data"),
        ATTACHMENT_NOT_FOUND("Attachment not found");

        private final String message;

        NonComplianceViolationMessage(String message) {
            this.message = message;
        }
    }
}
